package com.lpf.pojo;

public enum AttendanceType {
    //迟到 10分钟内
    comeLateInTen,
    //迟到 10分钟-20分钟
    comeLateInTwenty,
    //迟到 20分钟到30分钟
    comeLateInThirty,

    //早退 10分钟内
    leaveEarlyInTen,
    //早退 10分钟-20分钟
    leaveEarlyInTwenty,
    //早退 20分钟到30分钟
    leaveEarlyInThirty,

    //旷工
    absenteeism,

    //未打卡
    noClock,

    //加班
    workOvertime,

    otherInfo,
}
