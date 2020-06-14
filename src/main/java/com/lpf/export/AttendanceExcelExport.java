package com.lpf.export;

import com.lpf.pojo.Config;
import com.lpf.pojo.Person;
import com.lpf.pojo.Record;
import com.lpf.visitor.impl.AttendanceVisitor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class AttendanceExcelExport {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceExcelExport.class);

    private Workbook wb;
    private CellStyle redCellStyle;
    private CellStyle generalCellStyle;

    private List<Person> personList;
    private Config config;

    private int mustWorkDay;

    public AttendanceExcelExport(List<Person> personList, int mustWorkDay, Config config) {
        this.wb = new XSSFWorkbook();
        this.personList = personList;
        this.mustWorkDay = mustWorkDay;
        this.config = config;

        init();
    }

    private void init() {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        cellStyle.setFont(font); // 背景色

        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        cellStyle.setBorderTop(BorderStyle.THIN);//上边框
        cellStyle.setBorderRight(BorderStyle.THIN);//右边框
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        this.redCellStyle = cellStyle;


        CellStyle generalCellStyle = wb.createCellStyle();
        generalCellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        generalCellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        generalCellStyle.setBorderTop(BorderStyle.THIN);//上边框
        generalCellStyle.setBorderRight(BorderStyle.THIN);//右边框
        generalCellStyle.setWrapText(true);
        generalCellStyle.setAlignment(HorizontalAlignment.CENTER);
        generalCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        this.generalCellStyle = generalCellStyle;
    }

    public void export() {
        exportTotal();

        LOGGER.info("完成考勤汇总sheet页！");

        for (Person person : personList) {
            exportDetail(person);
        }
        LOGGER.info("完成考勤个人sheet页！");


        try (OutputStream fileOut = new FileOutputStream(new File(config.getTotalExcelFileName()))) {
            wb.write(fileOut);
            wb.close();
        } catch (IOException e) {
            LOGGER.error("导出excel失败！", e);
            e.printStackTrace();
        }
        LOGGER.info("完成考勤excel导出！");
    }

    private void exportTotal() {
        Sheet sheet = wb.createSheet("汇总");
        drawTotalSheetHead(sheet);
        int i = 1;
        for (Person person : personList) {
            Row row = sheet.createRow(3 + i);
            row.setHeight((short) 700);

            AttendanceVisitor attendanceVisitor = new AttendanceVisitor(person, config);
            int workday = person.getAttendanceDays().size();

            drawCell(row, "" + i++, 0);
            drawCell(row, "厦门研发", 1);
            drawCell(row, person.getName(), 2);
            drawCell(row, "", 3);
            drawCell(row,  Integer.toString(mustWorkDay), 4);
            drawCell(row,  Integer.toString(workday - attendanceVisitor.getAbsenteeismSum()), 5);

            //本月加班累计小时数
            drawCellWithCommentNoZero(row, Integer.toString(attendanceVisitor.getWorkOvertimeSum()), attendanceVisitor.getWorkOvertimeDetail().toString(), 6);
            drawCell(row, "", 7);
            drawCell(row, "", 8);
            drawCell(row, "", 9);

            //旷工
            drawCellWithCommentNoZero(row, Integer.toString(attendanceVisitor.getAbsenteeismSum()), attendanceVisitor.getAbsenteeismDetail().toString(), 10);
            drawCell(row, "", 11);
            //迟到早退 10分钟以内
            drawCellWithCommentNoZero(row, Integer.toString(attendanceVisitor.getComeLateAndLeaveEarlyInTenCount()), attendanceVisitor.getComeLateAndLeaveEarlyInTenDetail(), 12);
            //迟到早退 10分钟-20分钟
            drawCellWithCommentNoZero(row, Integer.toString(attendanceVisitor.getComeLateAndLeaveEarlyInTwentyCount()), attendanceVisitor.getComeLateAndLeaveEarlyInTwentyDetail(), 13);
            //迟到早退 20分钟-30分钟
            drawCellWithCommentNoZero(row, Integer.toString(attendanceVisitor.getComeLateAndLeaveEarlyInThirtyCount()), attendanceVisitor.getComeLateAndLeaveEarlyInThirtyDetail(), 14);

            //本月未打卡
            drawCellWithCommentNoZero(row, Integer.toString(attendanceVisitor.getNoClockCount()), attendanceVisitor.getNoClockDetail().toString(), 15);
            drawCell(row, "", 16);
            drawCell(row, "", 17);
            //备注
            drawCellWithComment(row, attendanceVisitor.getRemarkCellValue(), attendanceVisitor.getRemarkCellDetail().toString(), 18);
        }

        sheet.autoSizeColumn(18);
    }


    private void exportDetail(Person person){
        Sheet sheet = wb.createSheet(person.getName());
        Row row = sheet.createRow(0);

        drawCell(row, "No", 0);
        drawCell(row, "Mchn", 1);
        drawCell(row, "EnNo", 2);
        drawCell(row, "Name", 3);
        drawCell(row, "Date", 4);
        drawCell(row, "Time", 5);

        List<Record> records = person.getRecords();
        int i = 1;
        for (Record record : records) {
            row = sheet.createRow(i++);
            drawCell(row, record.getNo(), 0);
            drawCell(row, record.getMchn(), 1);
            drawCell(row, record.getEnNo(), 2);
            drawCell(row, record.getName(), 3);

            if (record.isError()) {
                drawCellRedStyle(row, record.getDateOri(), 4);
                drawCellRedStyle(row, record.getTimeOri(), 5);
            } else {
                drawCell(row, record.getDateOri(), 4);
                drawCell(row, record.getTimeOri(), 5);
            }
        }

        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(4);
    }


    /**
     * 绘制汇总表的表头
     * @param sheet
     */
    private void drawTotalSheetHead(Sheet sheet) {
        Row row = sheet.createRow(0);
        drawCell(row, "月厦门研发中心考勤汇总", 1);

        row = sheet.createRow(2);
        row.setHeight((short) 800);
        String[] title = {"序号", "部门", "姓名", "入离职日期", "本月应出勤天数", "本月实际出勤天数", "本月加班累计小时数"};
        for (int i = 0; i < title.length; i++) {
            drawCell(row, title[i], i);
        }
        drawCell(row, "本月缺勤天数", 7);
        drawCell(row, "本月迟到早退次数", 12);

        title = new String[]{"本月未打卡", "本月使用倒休", "本月剩余调休天数", "备注"};
        for (int i = 0 ; i < title.length; i++) {
            drawCell(row, title[i], i + 15);
        }

        row = sheet.createRow(3);
        title = new String[]{"病假", "事假", "年假", "旷工", "其他假", "10分钟以内", "10分钟-20分钟", "20分钟-30分钟"};
        for (int i = 0; i  < title.length; i++) {
            drawCell(row, title[i], i + 7);
        }

        //表头 标题汇总
        setMergedRegionCell(new CellRangeAddress(0, 1, 1, 18), sheet);

        int[] cols = {0, 1, 2, 3, 4, 5, 6, 15, 16, 17, 18};
        for (int col : cols) {
            setMergedRegionCell(new CellRangeAddress(2, 3, col, col), sheet);
        }
        setMergedRegionCell(new CellRangeAddress(2, 2, 7, 11), sheet);
        setMergedRegionCell(new CellRangeAddress(2, 2, 12, 14), sheet);
    }


    private Cell drawCell(Row row, String value, int column){
        return drawCell(row, value, column, generalCellStyle);
    }

    private Cell drawCellRedStyle(Row row, String value, int column){
        return drawCell(row, value, column, redCellStyle);
    }

    private Cell drawCell(Row row, String value, int column, CellStyle cellStyle){
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }
    private Cell drawCellWithCommentNoZero(Row row, String value, String commentValue, int column){
        if (value != null && "0".equals(value.trim())){
            value = "";
        }
        return drawCellWithComment(row, value, commentValue, column);
    }

    private Cell drawCellWithComment(Row row, String value, String commentValue, int column){
        commentValue = commentValue.trim();
        Cell cell = drawCell(row, value, column);
        if (commentValue != null && !"".equals(commentValue)) {
            Drawing draw = row.getSheet().createDrawingPatriarch();
            Comment comment = draw.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short)6, 6));
            comment.setString(new XSSFRichTextString(commentValue));//设置批注内容
            cell.setCellComment(comment);
        }
        return cell;
    }


    /** 设置合并单元格的 边框
     * @param cra
     * @author liupf
     */
    private void setMergedRegionCell(CellRangeAddress cra, Sheet sheet){
        sheet.addMergedRegion(cra);

        RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
    }
}
