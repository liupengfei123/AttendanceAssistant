package com.lpf.info;

import com.lpf.pojo.AttendanceType;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class LeaveEarly implements AttendanceInfo {
    private int leaveEarlyMinute;
    private LocalDateTime dateTime;
    private AttendanceType attendanceType;

    public static AttendanceInfo creatAttendanceInfo(int leaveEarlyMinute, LocalDateTime dateTime){
        AttendanceInfo attendanceInfo;
        if (leaveEarlyMinute <= 10) {
            attendanceInfo = new LeaveEarly(leaveEarlyMinute, dateTime, AttendanceType.leaveEarlyInTen);
        } else if (leaveEarlyMinute <= 20) {
            attendanceInfo = new LeaveEarly(leaveEarlyMinute, dateTime, AttendanceType.leaveEarlyInTwenty);
        } else if (leaveEarlyMinute <= 30) {
            attendanceInfo = new LeaveEarly(leaveEarlyMinute, dateTime, AttendanceType.leaveEarlyInThirty);
        }  else {
            //早退30分钟算 旷工
            attendanceInfo = Absenteeism.creatAttendanceInfo(dateTime.toLocalDate());
        }
        return attendanceInfo;
    }

    private LeaveEarly(int leaveEarlyMinute, LocalDateTime dateTime, AttendanceType attendanceType) {
        this.leaveEarlyMinute = leaveEarlyMinute;
        this.dateTime = dateTime;
        this.attendanceType = attendanceType;
    }

    @Override
    public void accept(AttendanceInfoVisitor visitor) {
        visitor.visitLeaveEarly(this);
    }
}
