package com.lpf.export;


import com.lpf.pojo.Person;
import com.lpf.visitor.impl.WorkOvertimeVisitor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class WorkOvertimeExcelExport {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOvertimeExcelExport.class);

    /**
     *  需要和 WorkOvertimeVisitor 中的 dateTimeFormatter相同
     *  @see com.lpf.visitor.impl.WorkOvertimeVisitor
     */
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy/M/d HH:mm:ss");

    private Workbook wb;
    private CellStyle generalCellStyle;
    private CellStyle dateCellStyle;

    private Person person;

    public WorkOvertimeExcelExport(Person person) {
        this.wb = new XSSFWorkbook();
        this.person = person;

        init();
    }

    private void init() {
        CellStyle generalCellStyle = wb.createCellStyle();
        generalCellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        generalCellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        generalCellStyle.setBorderTop(BorderStyle.THIN);//上边框
        generalCellStyle.setBorderRight(BorderStyle.THIN);//右边框
        generalCellStyle.setWrapText(true);
        generalCellStyle.setAlignment(HorizontalAlignment.CENTER);
        generalCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        this.generalCellStyle = generalCellStyle;


        CellStyle dateCellStyle = wb.createCellStyle();
        dateCellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        dateCellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        dateCellStyle.setBorderTop(BorderStyle.THIN);//上边框
        dateCellStyle.setBorderRight(BorderStyle.THIN);//右边框
        dateCellStyle.setWrapText(true);
        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //yyyy/m/d h:mm
        dateCellStyle.setDataFormat((short) 0x16);

        this.dateCellStyle = dateCellStyle;
    }

    public void export() {
        exportDetail(person);

        File file = new File("加班");
        if (!file.exists()) {
            file.mkdir();
        }

        try (OutputStream fileOut = new FileOutputStream(new File("加班\\" + person.getName() + ".xlsx"))) {
            wb.write(fileOut);
            wb.close();
        } catch (IOException e) {
            LOGGER.error("导出excel失败！", e);
            e.printStackTrace();
        }
    }

    private void exportDetail(Person person){
        Sheet sheet = wb.createSheet();
        drawTotalSheetHead(sheet);

        WorkOvertimeVisitor workOvertimeVisitor = new WorkOvertimeVisitor(person);

        List<String> workOvertimeStart = workOvertimeVisitor.getWorkOvertimeStart();
        List<String> workOvertimeEnd = workOvertimeVisitor.getWorkOvertimeEnd();

        for (int i = 0; i < workOvertimeStart.size(); i++) {
            Row row = sheet.createRow(i + 2);
            drawCellDate(row, workOvertimeStart.get(i), 0);
            drawCellDate(row, workOvertimeEnd.get(i), 1);
            drawCell(row, "", 2);
        }
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 5000);
    }


    /**
     * 绘制汇总表的表头
     * @param sheet
     */
    private void drawTotalSheetHead(Sheet sheet) {
        Row row = sheet.createRow(0);
        drawCell(row, "导入模板", 0);

        row = sheet.createRow(1);
        String[] title = {"加班时间起", "加班时间止", "加班内容"};
        for (int i = 0; i < title.length; i++) {
            drawCell(row, title[i], i);
        }
        //表头 标题汇总
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
    }

    private Cell drawCell(Row row, String value, int column){
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(generalCellStyle);
        return cell;
    }

    private Cell drawCellDate(Row row, String value, int column){
        Cell cell = row.createCell(column);
        try {
            cell.setCellValue(dateTimeFormatter.parse(value));
        } catch (ParseException e) {
            LOGGER.error("导出excel失败！", e);
            e.printStackTrace();
        }
        cell.setCellStyle(dateCellStyle);
        return cell;
    }
}
