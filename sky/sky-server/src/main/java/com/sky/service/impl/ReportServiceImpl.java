package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.properties.ReportExcelProperties;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportMapper reportMapper;
    @Autowired
    ReportExcelProperties reportExcelProperties;
    @Autowired
    WorkspaceServiceImpl workspaceService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<TurnoverReportVO> getTurnoverStatistics(LocalDate begin, LocalDate end) {
        LocalDate p = begin;
        ArrayList<LocalDate> dateTimes = new ArrayList<>();
        while (!p.equals(end)) {
            dateTimes.add(p);
            p = p.plusDays(1);
        }
        ArrayList<Double> count = new ArrayList<>();
        for (LocalDate dateTime : dateTimes) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("status", 5);
            map.put("begin", LocalDateTime.of(dateTime, LocalTime.MIN));
            map.put("end", LocalDateTime.of(dateTime, LocalTime.MAX));
            Double sum = reportMapper.sumByMap(map);
            if (sum == null) {
                sum = 0D;
            }
            count.add(sum);
        }

        return Result.success(TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateTimes, ","))
                .turnoverList(StringUtils.join(count, ","))
                .build());
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        LocalDate p = begin;
        ArrayList<LocalDate> dateTimes = new ArrayList<>();
        while (!p.equals(end)) {
            dateTimes.add(p);
            p = p.plusDays(1);
        }
        ArrayList<Integer> user = new ArrayList<>();
        ArrayList<Integer> newUser = new ArrayList<>();
        for (LocalDate dateTime : dateTimes) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("begin", LocalDateTime.of(dateTime, LocalTime.MIN));
            map.put("end", LocalDateTime.of(dateTime, LocalTime.MAX));
            Integer sum = reportMapper.sumUserByDay(map);
            Integer sumUser = reportMapper.sumUser(map);
            if (sum == null) {
                sum = 0;
            }
            if (sumUser == null) {
                sumUser = 0;
            }
            newUser.add(sum);
            user.add(sumUser);
        }


        return UserReportVO.builder()
                .dateList(StringUtils.join(dateTimes, ","))
                .totalUserList(StringUtils.join(user, ","))
                .newUserList(StringUtils.join(newUser, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        LocalDate p = begin;
        ArrayList<LocalDate> dateTimes = new ArrayList<>();
        while (!p.equals(end)) {
            dateTimes.add(p);
            p = p.plusDays(1);
        }
        ArrayList<Integer> order = new ArrayList<>();
        ArrayList<Integer> newOrder = new ArrayList<>();
        for (LocalDate dateTime : dateTimes) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("begin", LocalDateTime.of(dateTime, LocalTime.MIN));
            map.put("end", LocalDateTime.of(dateTime, LocalTime.MAX));
            map.put("status", Orders.COMPLETED);
            Integer sum = reportMapper.sumNewOrder(map);
            Integer sumOrder = reportMapper.sumOrder(map);
            if (sum == null) {
                sum = 0;
            }
            if (sumOrder == null) {
                sumOrder = 0;
            }
            order.add(sumOrder);
            newOrder.add(sum);
        }
        Double rate;
        if (sumArrayList(order) == 0) {
            rate = 1.0;
        } else {
            rate = sumArrayList(newOrder) / sumArrayList(order) * 1.0;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateTimes, ","))
                .orderCountList(StringUtils.join(newOrder, ","))
                .validOrderCountList(StringUtils.join(order, ","))
                .totalOrderCount(sumArrayList(newOrder))
                .validOrderCount(sumArrayList(order))
                .orderCompletionRate(rate)
                .build();
    }

    /**
     * 销量排行前十
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO salesTop10Report(LocalDate begin, LocalDate end) {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<Integer> number = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<HashMap<String, Object>> result;
        map.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("end", LocalDateTime.of(end, LocalTime.MAX));
        map.put("status", Orders.COMPLETED);
        result = reportMapper.salesTop10Report(map);
        for (HashMap<String, Object> hashMap : result) {
            name.add((String) hashMap.get("name"));
            number.add(((BigDecimal) hashMap.get("number")).intValue());
        }


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(name, ","))
                .numberList(StringUtils.join(number, ","))
                .build();
    }

    /**
     * 导出excel表格
     */
    @Override
    public void export(HttpServletResponse httpResponse) throws IOException, InvalidFormatException {
        File file = new File(reportExcelProperties.getFilePath());
        OPCPackage opcPackage = OPCPackage.open(file);
        //获取工作薄
        XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
        String s = reportExcelProperties.getSheet()[0];
        //获取工作表
        XSSFSheet sheet = workbook.getSheet(s);
        //填写日期
        XSSFRow row = sheet.getRow(1);
        XSSFCellStyle dataStyle = workbook.createCellStyle();
        //设置日期的字体
        XSSFFont font = workbook.createFont();
        font.setFontHeight(16);
        font.setFontName("宋体");
        dataStyle.setAlignment(HorizontalAlignment.RIGHT);
        XSSFCell cell0 = row.getCell(1);
        dataStyle.setFont(font);
        cell0.setCellStyle(dataStyle);


        //获取营业概览数据
        LocalDateTime begin = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        cell0.setCellValue(begin.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")) + "——" + end.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")));
        BusinessDataVO businessData = workspaceService.getBusinessData(begin, end);
        XSSFRow row3 = sheet.getRow(3);
        //营业额
        XSSFCell cell = row3.getCell(2);
        cell.setCellValue(businessData.getTurnover());
        //订单完成率
        XSSFCell cell1 = row3.getCell(4);
        cell1.setCellValue(businessData.getOrderCompletionRate());
        //新增用户数
        XSSFCell cell2 = row3.getCell(6);
        cell2.setCellValue(businessData.getNewUsers());
        //有效订单
        XSSFRow row1 = sheet.getRow(4);
        XSSFCell cell3 = row1.getCell(2);
        cell3.setCellValue(businessData.getValidOrderCount());
        //平均客单价
        XSSFCell cell4 = row1.getCell(4);
        cell4.setCellValue(businessData.getValidOrderCount());
        int dayOfMonth = Period.between(begin.toLocalDate(), end.toLocalDate()).getDays();
        System.out.println("dayOfMonth:" + dayOfMonth);
        //获取明细数据

        for (int i = 0; i < dayOfMonth; i++) {
            XSSFRow row2 = sheet.getRow(i + 7);
            LocalDateTime localDateTimeBegin = begin.plusDays(i);
            LocalDateTime localDateTimeEnd = localDateTimeBegin.with(LocalTime.MAX);
            BusinessDataVO data = workspaceService.getBusinessData(localDateTimeBegin, localDateTimeEnd);
            //设置日期
            row2.getCell(1).setCellValue(localDateTimeBegin.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
            //营业额
            row2.getCell(2).setCellValue(data.getTurnover());
            //有效订单
            row2.getCell(3).setCellValue(data.getValidOrderCount());
            //订单完成率
            row2.getCell(4).setCellValue(data.getOrderCompletionRate());
            //平均客单价
            row2.getCell(5).setCellValue(data.getUnitPrice());
            //新增用户数
            row2.getCell(6).setCellValue(data.getNewUsers());
        }

        //输出数据
        ServletOutputStream outputStream = httpResponse.getOutputStream();
        httpResponse.reset();
        httpResponse.setContentType("application/vnd.ms-excel");
        httpResponse.addHeader("Content-disposition", "attachment;filename=template.xlsx");
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

    }

    private Integer sumArrayList(ArrayList<Integer> arrayList) {
        Integer sum = 0;
        for (Integer integer : arrayList) {
            sum += integer;
        }
        return sum;
    }
}
