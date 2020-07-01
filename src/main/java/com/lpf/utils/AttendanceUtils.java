package com.lpf.utils;

import com.lpf.pojo.DayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttendanceUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceUtils.class);

    private static final String SEPARATOR = ": ";

    public static LocalTime getOffworkTime(LocalTime startTime) {
        LocalTime localTime = LocalTime.of(9, 0);
        LocalTime offworkTime = LocalTime.of(18, 0);

        if (startTime.isAfter(localTime)) {
            offworkTime = offworkTime.plusMinutes(30);
        }
        return offworkTime;
    }

    public static LocalTime getWorkOvertimeStartTime(LocalTime startTime) {
        return getOffworkTime(startTime).plusMinutes(30);
    }


    public static Map<LocalDate, DayType> getWorkCalendar(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, DayType> result = new LinkedHashMap<>();

        for (LocalDate temp = startDate; !temp.isAfter(endDate); temp = temp.plusDays(1)) {
            DayOfWeek dayOfWeek = temp.getDayOfWeek();

            DayType dayType = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) ? DayType.WEEKEND : DayType.WORK;

            result.put(temp, dayType);
        }

        return result;
    }

    /**
     * 从文件中导入工作日历，以修改节假日
     * @param fileUri
     */
    public static Map<LocalDate, DayType> getCalendar(String fileUri) {
        Map<LocalDate, DayType> workCalendar = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileUri)))){
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(SEPARATOR);
                if (splits.length == 2) {
                    LocalDate localDate = LocalDate.parse(splits[0]);
                    DayType dayType = DayType.valueOf(splits[1]);

                    workCalendar.put(localDate, dayType);
                }
            }
        } catch (IOException e) {
            LOGGER.error("解析日历出错", e);
        }
        LOGGER.info("解析日历成功, 天数为：{}", workCalendar.size());
        return workCalendar;
    }

    /**
     * 将工作日历导出到文件中，以修改节假日
     * @param startDateStr
     * @param endDateStr
     * @param fileUri
     */
    public static void buildCalendar(String startDateStr, String endDateStr, String fileUri){
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        Map<LocalDate, DayType> workCalendar = AttendanceUtils.getWorkCalendar(startDate, endDate);

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File(fileUri)))){
            for (Map.Entry<LocalDate, DayType> entry : workCalendar.entrySet()) {
                LocalDate localDate = entry.getKey();
                DayType dayType = entry.getValue();

                String line = localDate.toString() + SEPARATOR + dayType;

                fileWriter.write(line);
                fileWriter.newLine();
            }
        } catch (IOException e) {
            LOGGER.error("导出日历出错", e);
        }
    }
}
