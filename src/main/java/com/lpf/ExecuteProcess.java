package com.lpf;

import com.lpf.export.AttendanceExcelExport;
import com.lpf.export.WorkOvertimeExcelExport;
import com.lpf.extract.DataExtract;
import com.lpf.handler.AttendanceHandler;
import com.lpf.pojo.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 执行模板
 *
 * @author liupf
 * @date 2020-06-06 13:28
 */
@RequiredArgsConstructor
public class ExecuteProcess {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteProcess.class);
    @NonNull
    private Map<LocalDate, DayType> workCalendar;
    @NonNull
    private Config config;

    @Setter
    private DataExtract dataExtract;
    @Setter
    private AttendanceHandler attendanceHandler;


    public void execute() {
        List<Record> values = dataExtract.getRecord(config.getRecordUri());
        LOGGER.info("数据抽取完毕，准备进行数据处理！共 {} 条记录", values.size());

        List<Person> personList = transformData(values);

        if (personList.size() <= 0) {
            return;
        }
        personList.forEach(person -> {
            collectAttendanceDay(person);

            List<AttendanceDay> attendanceDays = person.getAttendanceDays();
            for (AttendanceDay attendanceDay : attendanceDays) {
                attendanceHandler.handler(attendanceDay);
            }
        });
        LOGGER.info("数据处理完毕准备导出！ 共 {} 名人员", personList.size());

        AttendanceExcelExport attendanceExcelExport = new AttendanceExcelExport(personList, computeMustWorkDays(), config);
        attendanceExcelExport.export();

        LOGGER.info("汇总表导出完毕，准备导出加班表！");

        for (Person person : personList) {
            WorkOvertimeExcelExport workOvertimeExcelExport = new WorkOvertimeExcelExport(person);
            workOvertimeExcelExport.export();
        }
        LOGGER.info("加班表导出完毕！");
    }


    private int computeMustWorkDays(){
        return (int) workCalendar.values().stream().filter(dayType -> dayType == DayType.WORK).count();
    }
    /**
     * 将所有打卡信息按照人员分类
     * @param data
     * @return
     */
    private List<Person> transformData(List<Record> data) {
        Map<String, List<Record>> map = data.stream().collect(Collectors.groupingBy(Record::getEnNo));

        List<Person> result = new ArrayList<>(map.size());
        map.forEach((enno, list) -> {
            Person person = new Person(enno, list.get(0).getName(), workCalendar);
            person.setRecords(list);
            result.add(person);
        });

        result.sort(Comparator.comparing(Person::getEnNo));
        return result;
    }

    /**
     *  归类员工的一天的打卡
     * @param person
     */
    private void collectAttendanceDay(Person person) {
        List<Record> records = person.getRecords();
        Map<LocalDate, DayType> workCalendars = person.getWorkCalendar();
        List<AttendanceDay> attendanceDays = new ArrayList<>(workCalendars.size());

        int index = 0;
        for (Map.Entry<LocalDate, DayType> entry : workCalendars.entrySet()) {
            LocalDate toDay = entry.getKey();
            DayType dayType = entry.getValue();
            Record startRecord = null;
            Record endRecord = null;

            LocalDateTime onWorkTime = toDay.atTime(config.getOffWordHourLimit(), 0);
            LocalDateTime dayMiddleTime = toDay.atTime(12, 0);
            LocalDateTime offWorkTime = onWorkTime.plusDays(1);
            for (; index < records.size(); index++){
                Record temp = records.get(index);
                LocalDateTime recordTime = temp.getDataTime();

                //如果打开超过第二天6点的算是 第二天的上班时间  第二天的打卡就保持指针位置不变
                if (offWorkTime.isBefore(recordTime)){
                    break;
                }
                //在新的一天上班时间 从 offWordHourLimit 开始计算
                if (startRecord == null && onWorkTime.isBefore(recordTime)){
                    startRecord = temp;
                }
                //取上班时间的最后一次打开
                //todo  在上班时间外出又回来的再考虑怎么处理
                endRecord = temp;
            }
            //周末没有加班就不加到考勤列表中
            if (dayType == DayType.WEEKEND && startRecord == null && endRecord == null){
                continue;
            }

            //如果一天 只有一次打卡 开始打开和结束打开会指向同一条打卡记录
            if (startRecord != null && endRecord == startRecord) {
                //没超过12点就算是早上打卡
                if (dayMiddleTime.isAfter(startRecord.getDataTime())){
                    endRecord = null;
                } else {
                    startRecord = null;
                }
            }

            AttendanceDay attendanceDay = new AttendanceDay(toDay, dayType);
            attendanceDay.setStartRecord(startRecord);
            attendanceDay.setEndRecord(endRecord);
            attendanceDays.add(attendanceDay);
        }
        person.setAttendanceDays(attendanceDays);
    }
}
