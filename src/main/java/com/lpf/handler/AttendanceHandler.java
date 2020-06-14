package com.lpf.handler;

import com.lpf.pojo.AttendanceDay;

public interface AttendanceHandler {

    void setNext(AttendanceHandler attendanceHandler);

    void handler(AttendanceDay attendanceDay);

}
