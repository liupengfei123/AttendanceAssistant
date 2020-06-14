package com.lpf.pojo;

import com.lpf.info.AttendanceInfo;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * 工作日的考勤信息
 *
 * @author liupf
 * @date 2020-06-06 16:42
 */
@Getter
@RequiredArgsConstructor
public class AttendanceDay {

    @NonNull
    private LocalDate localDate; //工作日期
    @NonNull
    private DayType dayType;

    @Setter
    private Record startRecord; //上班时间
    @Setter
    private Record endRecord;  //下班时间


    private List<AttendanceInfo> attendanceInfoList = new LinkedList<>();


    public boolean addAttendanceInfo(AttendanceInfo attendanceInfo){
        return attendanceInfoList.add(attendanceInfo);
    }

}
