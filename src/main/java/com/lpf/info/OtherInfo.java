package com.lpf.info;

import com.lpf.pojo.AttendanceType;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OtherInfo implements AttendanceInfo {

    private LocalDateTime dateTime;

    private AttendanceType attendanceType = AttendanceType.otherInfo;

    public static AttendanceInfo creatAttendanceInfo(LocalDateTime dateTime){
        return new OtherInfo(dateTime);
    }

    private OtherInfo(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public void accept(AttendanceInfoVisitor visitor) {
        visitor.visitOtherInfo(this);
    }
}
