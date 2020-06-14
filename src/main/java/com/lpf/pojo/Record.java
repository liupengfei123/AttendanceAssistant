package com.lpf.pojo;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 考勤原始记录
 *
 * @author liupf
 * @date 2020-06-06 13:12
 */
@Data
@RequiredArgsConstructor
public class Record {
    //递增的主键
    @NonNull
    private String no;

    //不知道这个字段干嘛的 打卡机导出的
    @NonNull
    private String mchn;

    //人员编号
    @NonNull
    private String enNo;

    //姓名
    @NonNull
    private String name;

    @NonNull
    private String dateOri;
    @NonNull
    private String timeOri;

    //打卡时间
    @NonNull
    private LocalDateTime dataTime;


    private boolean error = false;
}
