package com.lpf.info;

import com.lpf.pojo.AttendanceType;
import com.lpf.visitor.AttendanceInfoVisitor;
import lombok.NonNull;

/**
 * 工作日的考勤信息
 *
 * @author liupf
 * @date 2020-06-06 16:42
 */

public interface AttendanceInfo {

    AttendanceType getAttendanceType();

    void accept(AttendanceInfoVisitor visitor);
}
