package com.lpf;

import com.lpf.extract.DataExtract;
import com.lpf.extract.impl.DataExtractByTXT;
import com.lpf.handler.AttendanceHandler;
import com.lpf.handler.HandlerFactory;
import com.lpf.pojo.Config;
import com.lpf.pojo.DayType;
import com.lpf.utils.AttendanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 程序入口
 *
 * @author liupf
 * @date 2020-06-06 13:10
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length <= 0) {
            LOGGER.error("缺少启动参数，如:'calendar'、'attendance'");
            return;
        }
        String type = args[0];
        LOGGER.info(type);

        Yaml yaml = new Yaml();
        Config ret = null;
        try (InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("config.yml")){
            ret = yaml.loadAs(resourceAsStream, Config.class);
        } catch (IOException e) {
            LOGGER.error("解析配置文件出错", e);
        }

        if ("calendar".equals(type)) {
            String startData = args[1];
            String endData = args[2];
            AttendanceUtils.buildCalendar(startData, endData, ret.getCalenderUri());
        } else if ("attendance".equals(type)){
            HandlerFactory.deadline = LocalTime.parse(ret.getComelateDeadline(), DateTimeFormatter.ofPattern("HH:mm"));

            Map<LocalDate, DayType> workCalendar = AttendanceUtils.getCalendar(ret.getCalenderUri());
            ExecuteProcess executeProcess = new ExecuteProcess(workCalendar, ret);

            AttendanceHandler attendanceHandler = HandlerFactory.getHandler();
            executeProcess.setAttendanceHandler(attendanceHandler);

            DataExtract dataExtract= new DataExtractByTXT();
            executeProcess.setDataExtract(dataExtract);

            executeProcess.execute();
        }
    }




}
