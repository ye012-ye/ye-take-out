package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {


    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查看订单详细
     */
    Orders detailsById(Long id);

    /**
     * 接单
     */
    void confirmOrders(OrdersConfirmDTO orderId);

    /**
     * 派送
     */
    void delivery(Long id);

    /**
     * 取消订单
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 拒单
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 完成
     */
    void complete(Long id);

    /**
     * 各个订单统计
     */
    OrderStatisticsVO statistics();

    /**
     * 历史订单查询
     */
    PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 再来一单
     */
    void repetition(Long id);

    /**
     * 催单
     * @param id
     */
    void reminder(Long id);
}
