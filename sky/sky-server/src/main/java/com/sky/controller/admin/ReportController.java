package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController()
@RequestMapping("/admin/report")
@Slf4j
@Api("营业额相关接口")
public class ReportController {

    @Autowired
    ReportService reportService;

    /**
     * 营业额相关接口
     *
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("营业额相关接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计:{},{}", begin, end);
        return reportService.getTurnoverStatistics(begin, end);
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("用户统计")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计:{},{}", begin, end);

        return Result.success(reportService.userStatistics(begin, end));
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计{},{}", begin, end);

        return Result.success(reportService.orderStatistics(begin, end));
    }

    /**
     * 销量排名前十
     *
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("销量排行前十")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> salesTop10Report(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销售排名前10:{},{}", begin, end);

        return Result.success(reportService.salesTop10Report(begin, end));
    }

    /**
     * 导出excel表格
     *
     * @return
     */
    @ApiOperation("导出excel表格")
    @GetMapping("/export")
    public Result export(HttpServletResponse httpResponse) throws IOException, InvalidFormatException {
        log.info("导出Excel表格");
        reportService.export(httpResponse);

        return Result.success("OK");
    }

}