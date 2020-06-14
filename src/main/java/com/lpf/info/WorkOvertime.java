package com.lpf.info;

import com.lpf.pojo.AttendanceType;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WorkOvertime implements AttendanceInfo {

    private long duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AttendanceType attendanceType = AttendanceType.workOvertime;

    public static AttendanceInfo creatAttendanceInfo(long duration, LocalDateTime startTime, LocalDateTime endTime){
        return new WorkOvertime(duration, startTime, endTime);
    }

    private WorkOvertime(long duration, LocalDateTime startTime, LocalDateTime endTime) {
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void accept(AttendanceInfoVisitor visitor) {
        visitor.visitWorkOvertime(this);
    }
}
