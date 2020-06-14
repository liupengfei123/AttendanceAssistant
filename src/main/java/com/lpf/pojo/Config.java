package com.lpf.pojo;

import lombok.Data;

@Data
public class Config {
    //原始打卡记录文件
    private String recordUri;
    //日历文件
    private String calenderUri;
    //加班信息文件
    private String workOvertimeInfoUri;

    private String totalExcelFileName;

    //一天中考勤开始的时间
    private int offWordHourLimit = 6;
    //免费迟到最大时间
    private int comelateFreeMinute = 30;
    //免费迟到次数
    private int comelateFreeCount = 2;

    //免费打卡次数
    private int noClockFreeCount = 1;

    //早上最晚打卡时间
    private String comelateDeadline = "09:30";
}
