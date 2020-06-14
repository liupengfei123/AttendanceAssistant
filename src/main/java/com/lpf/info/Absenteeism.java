package com.lpf.info;

import com.lpf.pojo.AttendanceType;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Absenteeism implements AttendanceInfo {
    private LocalDate date;

    private AttendanceType attendanceType = AttendanceType.absenteeism;

    public static AttendanceInfo creatAttendanceInfo(LocalDate date){
        return new Absenteeism(date);
    }

    private Absenteeism(LocalDate date) {
        this.date = date;
    }

    @Override
    public void accept(AttendanceInfoVisitor visitor) {
        visitor.visitorAbsenteeism(this);
    }
}
