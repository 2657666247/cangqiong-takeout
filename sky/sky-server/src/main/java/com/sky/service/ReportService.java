package com.sky.service;

import com.sky.result.Result;
import com.sky.vo.*;
import org.apache.http.HttpResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    /**
     * 统计营业额
     * @param begin
     * @param end
     * @return
     */
    Result<TurnoverReportVO> getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO orderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量排行前十
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO salesTop10Report(LocalDate begin, LocalDate end);

    /**
     *
     * 导出excel表格
     */
    void export(HttpServletResponse httpResponse) throws IOException, InvalidFormatException;
}
