package com.lpf.info;

import com.lpf.pojo.AttendanceType;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NoClock implements AttendanceInfo {

    private LocalDate date;
    private AttendanceType attendanceType = AttendanceType.noClock;

    public static AttendanceInfo creatAttendanceInfo(LocalDate date){
        return new NoClock(date);
    }

    private NoClock(LocalDate date) {
        this.date = date;
    }

    @Override
    public void accept(AttendanceInfoVisitor visitor) {
        visitor.visitNoClock(this);
    }
}
