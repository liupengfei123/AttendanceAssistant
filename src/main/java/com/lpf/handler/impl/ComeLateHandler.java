package com.lpf.handler.impl;

import com.lpf.handler.AbstractAttendanceHandler;
import com.lpf.info.ComeLate;
import com.lpf.pojo.AttendanceDay;
import com.lpf.pojo.DayType;
import com.lpf.pojo.Record;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * 处理迟到
 *
 * @author liupf
 * @date 2020-06-06 16:30
 */
public class ComeLateHandler extends AbstractAttendanceHandler {

    private LocalTime deadline;

    public ComeLateHandler(LocalTime deadline) {
        this.deadline = deadline;
    }

    @Override
    public void handler(AttendanceDay attendanceDay) {
        if (attendanceDay.getDayType() == DayType.WEEKEND) {
            super.handler(attendanceDay);
            return;
        }

        Record startRecord = attendanceDay.getStartRecord();
        LocalTime recordTime = startRecord.getDataTime().toLocalTime();
        //最迟上班时间到 打卡时间之间的分钟数
        long between = ChronoUnit.MINUTES.between(deadline, recordTime);
        if (between > 0) {
            attendanceDay.addAttendanceInfo(ComeLate.creatAttendanceInfo((int) between, startRecord.getDataTime()));

            //这边可以给 record 加详情的
            startRecord.setError(true);
        }
        super.handler(attendanceDay);
    }
}
