package com.lpf.handler;

import com.lpf.pojo.AttendanceDay;

/**
 * 考勤处理
 *
 * @author liupf
 * @date 2020-06-06 16:23
 */
public abstract class AbstractAttendanceHandler implements AttendanceHandler{
    private AttendanceHandler next;

    @Override
    public void handler(AttendanceDay attendanceDay) {
        if (next != null) {
            next.handler(attendanceDay);
        }
    }

    @Override
    public void setNext(AttendanceHandler next) {
        this.next = next;
    }
}
