package com.lpf.handler.impl;

import com.lpf.handler.AbstractAttendanceHandler;
import com.lpf.info.LeaveEarly;
import com.lpf.pojo.AttendanceDay;
import com.lpf.pojo.DayType;
import com.lpf.pojo.Record;
import com.lpf.utils.AttendanceUtils;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * 处理早退
 *
 * @author liupf
 * @date 2020-06-06 16:30
 */
public class LeaveEarlyHandler extends AbstractAttendanceHandler {

    @Override
    public void handler(AttendanceDay attendanceDay) {
        if (attendanceDay.getDayType() == DayType.WEEKEND) {
            super.handler(attendanceDay);
            return;
        }

        Record startRecord = attendanceDay.getStartRecord();
        Record endRecord = attendanceDay.getEndRecord();
        LocalTime recordTime = endRecord.getDataTime().toLocalTime();
        LocalTime deadline = AttendanceUtils.getOffworkTime(startRecord.getDataTime().toLocalTime());

        //下班打卡时间 到 下班时间 早了多少分钟数
        long between = ChronoUnit.MINUTES.between(recordTime, deadline);
        if (between > 0) {
            attendanceDay.addAttendanceInfo(LeaveEarly.creatAttendanceInfo((int) between, endRecord.getDataTime()));
            endRecord.setError(true);
        }
        super.handler(attendanceDay);
    }
}
