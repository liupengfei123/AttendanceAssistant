package com.lpf.handler.impl;

import com.lpf.handler.AbstractAttendanceHandler;
import com.lpf.info.WorkOvertime;
import com.lpf.pojo.AttendanceDay;
import com.lpf.pojo.DayType;
import com.lpf.utils.AttendanceUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * 处理未打卡
 *
 * @author liupf
 * @date 2020-06-06 16:30
 */
public class WorkOvertimeHandler extends AbstractAttendanceHandler {

    @Override
    public void handler(AttendanceDay attendanceDay) {

        LocalDateTime startTime = attendanceDay.getStartRecord().getDataTime();
        LocalDateTime endTime = attendanceDay.getEndRecord().getDataTime();

        if (attendanceDay.getDayType() != DayType.WEEKEND) {
            LocalTime workOvertimeStartTime = AttendanceUtils.getWorkOvertimeStartTime(startTime.toLocalTime());

            startTime = LocalDateTime.of(startTime.toLocalDate(), workOvertimeStartTime);
        }

        //下班打卡时间 到 下班时间 早了多少分钟数
        long between = ChronoUnit.HOURS.between(startTime, endTime);
        if (between > 0) {
            attendanceDay.addAttendanceInfo(WorkOvertime.creatAttendanceInfo(between, startTime, endTime));
        }

        super.handler(attendanceDay);
    }
}
