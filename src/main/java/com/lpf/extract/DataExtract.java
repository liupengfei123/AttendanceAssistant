package com.lpf.extract;

import com.lpf.pojo.Record;

import java.util.List;

public interface DataExtract {

    List<Record> getRecord(String uri);

}
