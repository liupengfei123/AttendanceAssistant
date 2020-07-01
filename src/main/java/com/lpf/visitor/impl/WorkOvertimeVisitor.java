package com.lpf.visitor.impl;

import com.lpf.info.*;
import com.lpf.pojo.AttendanceDay;
import com.lpf.pojo.Person;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WorkOvertimeVisitor implements AttendanceInfoVisitor {

    private Person person;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Getter  private List<String> workOvertimeStart = new ArrayList<>();
    @Getter  private List<String> workOvertimeEnd = new ArrayList<>();

    public WorkOvertimeVisitor(Person person) {
        this.person = person;

        dealInfoData();
    }

    private void dealInfoData() {
        List<AttendanceDay> attendanceDays = person.getAttendanceDays();

        for (AttendanceDay attendanceDay : attendanceDays) {
            List<AttendanceInfo> attendanceInfoList = attendanceDay.getAttendanceInfoList();
            for (AttendanceInfo attendanceInfo : attendanceInfoList) {
                attendanceInfo.accept(this);
            }
        }
    }

    @Override
    public void visitComeLate(ComeLate comeLate) {}

    @Override
    public void visitLeaveEarly(LeaveEarly leaveEarly) {}

    @Override
    public void visitNoClock(NoClock noClock) {}

    @Override
    public void visitOtherInfo(OtherInfo otherInfo) {}

    @Override
    public void visitWorkOvertime(WorkOvertime workOvertime) {
        LocalDateTime startTime = workOvertime.getStartTime();
        LocalDateTime endTime = workOvertime.getEndTime();

        workOvertimeStart.add(startTime.format(dateTimeFormatter));
        workOvertimeEnd.add(endTime.format(dateTimeFormatter));
    }

    @Override
    public void visitorAbsenteeism(Absenteeism absenteeism) {}
}
