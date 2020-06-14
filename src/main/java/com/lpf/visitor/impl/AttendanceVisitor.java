package com.lpf.visitor.impl;

import com.lpf.info.*;
import com.lpf.pojo.AttendanceDay;
import com.lpf.pojo.Config;
import com.lpf.pojo.Person;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceVisitor implements AttendanceInfoVisitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceVisitor.class);

    private Person person;
    private Config config;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    //加班
    @Getter   private int workOvertimeSum;
    @Getter   private StringBuilder workOvertimeDetail = new StringBuilder();

    //未打卡
    @Getter   private int noClockCount;
    @Getter   private StringBuilder noClockDetail = new StringBuilder();

    //旷工
    @Getter   private int absenteeismSum;
    @Getter   private StringBuilder absenteeismDetail = new StringBuilder();

    //迟到免费次数
    @Getter   private int noClockFreeCount;
    @Getter   private int comelateFreeCount;
    @Getter   private StringBuilder remarkCellDetail = new StringBuilder();

    //迟到 10分钟内
    private int comeLateInTenCount;
    private StringBuilder comeLateInTenDetail = new StringBuilder();
    //早退 10分钟内
    private int leaveEarlyInTenCount;
    private StringBuilder leaveEarlyInTenDetail = new StringBuilder();

    //迟到 10分钟-20分钟
    private int comeLateInTwentyCount;
    private StringBuilder comeLateInTwentyDetail = new StringBuilder();
    //早退 10分钟-20分钟
    private int leaveEarlyInTwentyCount;
    private StringBuilder leaveEarlyInTwentyDetail = new StringBuilder();

    //迟到 20分钟到30分钟
    private int comeLateInThirtyCount;
    private StringBuilder comeLateInThirtyDetail = new StringBuilder();
    //早退 20分钟到30分钟
    private int leaveEarlyInThirtyCount;
    private StringBuilder leaveEarlyInThirtyDetail = new StringBuilder();


    public AttendanceVisitor(Person person, Config config) {
        this.person = person;
        this.config = config;

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

    public String getRemarkCellValue(){
        StringBuilder sb = new StringBuilder();

        if (this.comelateFreeCount > 0) {
            sb.append("已用").append(this.comelateFreeCount).append("次迟到");
        }
        if (this.noClockFreeCount > 0) {
            sb.append("已用").append(this.noClockFreeCount).append("次忘打卡");
        }
        return sb.toString();
    }

    public int getComeLateAndLeaveEarlyInTenCount() {
        return comeLateInTenCount + leaveEarlyInTenCount;
    }

    public String getComeLateAndLeaveEarlyInTenDetail() {
        return comeLateInTenDetail.toString() + "\r\n" + leaveEarlyInTenDetail.toString();
    }

    public int getComeLateAndLeaveEarlyInTwentyCount() {
        return comeLateInTwentyCount + leaveEarlyInTwentyCount;
    }

    public String getComeLateAndLeaveEarlyInTwentyDetail() {
        return comeLateInTwentyDetail.toString() + "\r\n" + leaveEarlyInTwentyDetail.toString();
    }

    public int getComeLateAndLeaveEarlyInThirtyCount() {
        return comeLateInThirtyCount + leaveEarlyInThirtyCount;
    }

    public String getComeLateAndLeaveEarlyInThirtyDetail() {
        return comeLateInThirtyDetail.toString() + "\r\n" + leaveEarlyInThirtyDetail.toString();
    }

    @Override
    public void visitComeLate(ComeLate comeLate) {
        if (comelateFreeCount < config.getComelateFreeCount()) {
            comelateFreeCount++;
            remarkCellDetail.append("迟到：").append(comeLate.getDateTime().format(dateTimeFormatter)).append("\r\n");
            return;
        }
        StringBuilder sb = null;
        switch (comeLate.getAttendanceType()) {
            case comeLateInTen:
                comeLateInTenCount++;
                sb = comeLateInTenDetail;
                break;
            case comeLateInTwenty:
                comeLateInTwentyCount++;
                sb = comeLateInTwentyDetail;
                break;
            case comeLateInThirty:
                comeLateInThirtyCount++;
                sb = comeLateInThirtyDetail;
                break;
        }
        sb.append(comeLate.getDateTime().format(dateTimeFormatter)).append(" 迟到 ").append(comeLate.getComelateMinute()).append("分").append("\r\n");
    }

    @Override
    public void visitLeaveEarly(LeaveEarly leaveEarly) {
        StringBuilder sb = null;
        switch (leaveEarly.getAttendanceType()) {
            case leaveEarlyInTen:
                leaveEarlyInTenCount++;
                sb = leaveEarlyInTenDetail;
                break;
            case leaveEarlyInTwenty:
                leaveEarlyInTwentyCount++;
                sb = leaveEarlyInTwentyDetail;
                break;
            case leaveEarlyInThirty:
                leaveEarlyInThirtyCount++;
                sb = leaveEarlyInThirtyDetail;
                break;
        }
        LOGGER.debug(leaveEarly.toString());
        sb.append(leaveEarly.getDateTime().format(dateTimeFormatter)).append(" 早退 ").append(leaveEarly.getLeaveEarlyMinute()).append("分").append("\r\n");
    }

    @Override
    public void visitNoClock(NoClock noClock) {
        if (noClockFreeCount < config.getNoClockFreeCount()) {
            noClockFreeCount++;
            remarkCellDetail.append("未打卡").append(noClock.getDate()).append("\r\n");
            return;
        }
        this.noClockCount++;
        noClockDetail.append(noClock.getDate()).append("\r\n");
    }

    @Override
    public void visitOtherInfo(OtherInfo otherInfo) {
    }

    @Override
    public void visitWorkOvertime(WorkOvertime workOvertime) {
        long duration = workOvertime.getDuration();
        this.workOvertimeSum += duration;

        LocalDateTime startTime = workOvertime.getStartTime();
        LocalDateTime endTime = workOvertime.getEndTime();

        this.workOvertimeDetail.append(startTime.format(dateTimeFormatter)).append("-");

        if (endTime.toLocalDate().equals(startTime.toLocalDate())) {
            this.workOvertimeDetail.append(endTime.toLocalTime().format(timeFormatter));
        } else {
            this.workOvertimeDetail.append(endTime.format(dateTimeFormatter));
        }
        this.workOvertimeDetail.append("  ").append(duration).append("h");
        this.workOvertimeDetail.append("\r\n");
    }

    @Override
    public void visitorAbsenteeism(Absenteeism absenteeism) {
        absenteeismSum++;
        absenteeismDetail.append(absenteeism.getDate()).append("\r\n");
    }
}
