package com.lpf.extract.impl;

import com.lpf.extract.DataExtract;
import com.lpf.pojo.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * 从txt文件中获取打卡记录
 *
 * @author liupf
 * @date 2020-06-06 13:27
 */
public class DataExtractByTXT implements DataExtract {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractByTXT.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Override
    public List<Record> getRecord(String uri) {
        File file = new File(uri);
        List<Record> result = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))){

                Stream<String> lines = br.lines();
                lines.forEach(s -> {
                    Record value = createRecord(s);
                    if (value != null) {
                        result.add(value);
                    }
                });


            } catch (FileNotFoundException e) {
                LOGGER.error("打开记录文件查找不到！", e);
            } catch (IOException e) {
                LOGGER.error("IO异常！", e);
            }
        }
        return result;
    }


    private Record createRecord(String str) {
        if (str.toLowerCase().contains("no") && str.toLowerCase().contains("name")) {
            return null;
        }

        String[] strs = str.split("\\s+");

        if (strs.length != 6) {
            LOGGER.warn("打开记录解析失败：{}， 解析长度为：{}", str, strs.length);
            return null;
        }

        String no = strs[0].trim();
        String mchn = strs[1].trim();
        String enNo = strs[2].trim();
        String name = strs[3].trim();
        String dateOri = strs[4].trim();
        String timeOri = strs[5].trim();
        String dateTime = dateOri + " " + timeOri;
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);

        return new Record(no, mchn, enNo, name, dateOri, timeOri, localDateTime);
    }
}
