package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
public class OrdersTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单定时任务
     */
    @Scheduled(cron = "0 * * * * ?")//
    public void processTimeoutOrdes(){
        log.info("处理超时订单定时任务:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> ordersList = orderMapper.getByStatusAndOrdersTimeLT(Orders.PENDING_PAYMENT, time);

        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.updateById(orders);
            }
        }
    }

    /**
     * 处理一直派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点
    public void processDeliveryOrder(){
        log.info("处理一直派送中的订单:{}",LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> ordersList = orderMapper.getByStatusAndOrdersTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.updateById(orders);
            }
        }
    }

}
