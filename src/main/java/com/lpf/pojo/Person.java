package com.lpf.pojo;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 人员信息
 *
 * @author liupf
 * @date 2020-06-06 14:23
 */

@Getter
@RequiredArgsConstructor()
public class Person {
    @NonNull
    private String enNo;
    @NonNull
    private String name;


    @NonNull
    Map<LocalDate, DayType> workCalendar;

    @Setter
    private List<Record> records = new ArrayList<>();

    @Setter
    private List<AttendanceDay> attendanceDays = new ArrayList<>();
}
