package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface ReportMapper {
    /**
     * 统计营业额
     *
     * @param map
     * @return
     */
    @Select("select sum(amount) from orders where status=#{status} and checkout_time>#{begin} and checkout_time<#{end}")
    Double sumByMap(HashMap<String, Object> map);

    /**
     * 用户统计
     *
     * @param map
     * @return
     */
    @Select("select count(id) from user where create_time>#{begin} and create_time<#{end}")
    Integer sumUserByDay(HashMap<String, Object> map);

    @Select("select count(*) from user where create_time<#{end}")
    Integer sumUser(HashMap<String, Object> map);

    /**
     * 订单统计
     * 当天产生的订单总数
     * @param map
     * @return
     */
    @Select("select count(*) from orders where order_time>#{begin} and order_time<#{end}")
    Integer sumNewOrder(HashMap<String, Object> map);

    /**
     * 当天产生的有效订单总数
     * @param map
     * @return
     */
    @Select("select count(*) from orders where checkout_time>#{begin} and checkout_time<#{end} and status=#{status}")
    Integer sumOrder(HashMap<String, Object> map);

    /**
     * 销量前十
     * @param map
     * @return
     */
    @Select("select od.name name,sum(od.number) number from order_detail od,orders o where od.order_id=o.id and status=#{status} and o.order_time>#{begin} and o.order_time<#{end} " +
            "group by name order by number desc")
    ArrayList<HashMap<String, Object>> salesTop10Report(HashMap<String, Object> map);
}
