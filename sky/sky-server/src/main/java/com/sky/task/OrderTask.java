package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    OrderMapper orderMapper;

    /**
     * 处理订单超时的方法,每分钟触发一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOrderTask() {
        log.info("处理订单超时:{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        ArrayList<Orders> ordersList = (ArrayList<Orders>) orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if (ordersList != null && ordersList.size() > 0) {
            for (int i = 0; i < ordersList.size(); i++) {
                ordersList.get(i).setStatus(Orders.CANCELLED);
                ordersList.get(i).setCancelReason("订单超时");
            }
            orderMapper.updateBatchStatus(ordersList,Orders.CANCELLED);
        }
    }

    /**
     * 处理一直处于派送中状态的订单,每天凌晨1点触发一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("定时处理处于派送中的订单:{}", LocalDateTime.now());

        ArrayList<Orders> ordersList = (ArrayList<Orders>) orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,null);
        if (ordersList != null && ordersList.size() > 0) {
            for (int i = 0; i < ordersList.size(); i++) {
                ordersList.get(i).setStatus(Orders.COMPLETED);
            }
            orderMapper.updateBatchStatus(ordersList,Orders.COMPLETED);
        }
    }


}
