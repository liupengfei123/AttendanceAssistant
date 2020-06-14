package com.lpf.info;

import com.lpf.pojo.AttendanceType;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ComeLate implements AttendanceInfo {

    private int comelateMinute;
    private LocalDateTime dateTime;
    private AttendanceType attendanceType;

    public static AttendanceInfo creatAttendanceInfo(int leaveEarlyMinute, LocalDateTime dateTime){
        AttendanceInfo attendanceInfo;
        if (leaveEarlyMinute <= 10) {
            attendanceInfo = new ComeLate(leaveEarlyMinute, dateTime, AttendanceType.comeLateInTen);
        } else if (leaveEarlyMinute <= 20) {
            attendanceInfo = new ComeLate(leaveEarlyMinute, dateTime, AttendanceType.comeLateInTwenty);
        } else if (leaveEarlyMinute <= 30) {
            attendanceInfo = new ComeLate(leaveEarlyMinute, dateTime, AttendanceType.comeLateInThirty);
        }  else {
            //迟到30分钟算 旷工
            attendanceInfo = Absenteeism.creatAttendanceInfo(dateTime.toLocalDate());
        }
        return attendanceInfo;
    }

    private ComeLate(int comelateMinute, LocalDateTime dateTime, AttendanceType attendanceType) {
        this.comelateMinute = comelateMinute;
        this.dateTime = dateTime;
        this.attendanceType = attendanceType;
    }


    @Override
    public void accept(AttendanceInfoVisitor visitor) {
        visitor.visitComeLate(this);
    }
}
