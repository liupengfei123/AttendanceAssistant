package com.lpf.visitor;

import com.lpf.info.*;

public interface AttendanceInfoVisitor {

    void visitComeLate(ComeLate comeLate);

    void visitLeaveEarly(LeaveEarly leaveEarly);

    void visitNoClock(NoClock noClock);

    void visitOtherInfo(OtherInfo otherInfo);

    void visitWorkOvertime(WorkOvertime workOvertime);

    void visitorAbsenteeism(Absenteeism absenteeism);
}
