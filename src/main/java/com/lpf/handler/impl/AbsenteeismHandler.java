package com.lpf.handler.impl;

import com.lpf.handler.AbstractAttendanceHandler;
import com.lpf.info.Absenteeism;
import com.lpf.pojo.AttendanceDay;
import com.lpf.pojo.DayType;

/**
 * 处理旷工
 *
 * @author liupf
 * @date 2020-06-06 16:30
 */
public class AbsenteeismHandler extends AbstractAttendanceHandler {

    @Override
    public void handler(AttendanceDay attendanceDay) {
        if (attendanceDay.getDayType() != DayType.WEEKEND && attendanceDay.getStartRecord() == null && attendanceDay.getEndRecord() == null) {
            //旷工
            attendanceDay.addAttendanceInfo( Absenteeism.creatAttendanceInfo(attendanceDay.getLocalDate()));
        } else {
            //旷工了之后也就可以不用再判断了
            super.handler(attendanceDay);
        }
    }
}
