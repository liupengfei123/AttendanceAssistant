package com.lpf.handler;

import com.lpf.handler.impl.*;

import java.time.LocalTime;


/**
 * 处理器工厂
 *
 * @author liupf
 * @date 2020-06-13 15:43
 */
public class HandlerFactory {

    public static LocalTime deadline;

    public static AttendanceHandler getHandler() {
        AttendanceHandler absenteeismHandler = new AbsenteeismHandler();
        AttendanceHandler noClockHandler = new NoClockHandler();
        absenteeismHandler.setNext(noClockHandler);

        AttendanceHandler comeLateHandler = new ComeLateHandler(deadline);
        noClockHandler.setNext(comeLateHandler);

        AttendanceHandler leaveEarlyHandler = new LeaveEarlyHandler();
        comeLateHandler.setNext(leaveEarlyHandler);

        AttendanceHandler workOvertimeHandler = new WorkOvertimeHandler();
        leaveEarlyHandler.setNext(workOvertimeHandler);

        return absenteeismHandler;
    }


}
