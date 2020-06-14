package com.lpf.handler.impl;

import com.lpf.handler.AbstractAttendanceHandler;
import com.lpf.info.NoClock;
import com.lpf.pojo.AttendanceDay;
import com.lpf.pojo.DayType;
import com.lpf.pojo.Record;

/**
 * 处理未打卡
 *
 * @author liupf
 * @date 2020-06-06 16:30
 */
public class NoClockHandler extends AbstractAttendanceHandler {

    @Override
    public void handler(AttendanceDay attendanceDay) {
        //默认早上和下午未打卡 就是矿工 就已经过滤了
        if (attendanceDay.getStartRecord() == null || attendanceDay.getEndRecord() == null) {
            if (attendanceDay.getDayType() != DayType.WEEKEND) {
                Record record = attendanceDay.getStartRecord();
                record = record != null ? record : attendanceDay.getEndRecord();
                record.setError(true);

                attendanceDay.addAttendanceInfo(NoClock.creatAttendanceInfo(attendanceDay.getLocalDate()));
            }
        } else {
            super.handler(attendanceDay);
        }
    }
}
