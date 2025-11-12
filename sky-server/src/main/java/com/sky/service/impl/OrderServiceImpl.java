package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.Websocket.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常（地址薄为空，购物车为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //插入数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orders.setAddress(addressBook.getDetail());

        orderMapper.insert(orders);

        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        //先订单详细批量插入数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBach(orderDetailList);

        shoppingCartMapper.deleteByUserId(userId);

        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {

        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        Page<Orders> ordersPage = orderMapper.page(ordersPageQueryDTO);

        List<Orders> result = ordersPage.getResult();

        for (Orders orders : result) {
            List<String> orderDetailNameList = new ArrayList<>();
            ArrayList<OrderDetail> orderDetails = orderDetailMapper.getByOrdersId(orders.getId());
            for (OrderDetail orderDetail : orderDetails) {
                orderDetailNameList.add(orderDetail.getName() + "×" + orderDetail.getNumber());
            }
            orders.setOrderDishes(StringUtils.join(orderDetailNameList,","));
        }
        return new PageResult(ordersPage.getTotal(),result);
    }

    /**
     * 查看订单详细
     */
    @Override
    public Orders detailsById(Long id) {
        Orders orders = orderMapper.getById(id);
        ArrayList<OrderDetail> orderDetails = orderDetailMapper.getByOrdersId(orders.getId());
        orders.setOrderDetailList(orderDetails);
        return orders;
    }

    /**
     * 接单
     */
    @Override
    public void confirmOrders(OrdersConfirmDTO orderId) {
        orderId.setStatus(3);
        orderMapper.updateStatusById(orderId);
    }

    /**
     * 派送
     */
    @Override
    public void delivery(Long id) {
        OrdersConfirmDTO ordersConfirmDTO = new  OrdersConfirmDTO();
        ordersConfirmDTO.setId(id);
        ordersConfirmDTO.setStatus(4);
        orderMapper.updateStatusById(ordersConfirmDTO);
    }

    /**
     * 取消订单
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersCancelDTO,orders);
        orders.setStatus(6);
        orderMapper.updateById(orders);
    }

    /**
     * 拒单
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersRejectionDTO,orders);
        orders.setStatus(6);
        orderMapper.updateById(orders);
    }

    /**
     * 完成
     */
    @Override
    public void complete(Long id) {
        Orders orders = new Orders();
        orders.setStatus(5);
        orders.setId(id);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(orders);
    }

    /**
     * 各个订单统计
     */
    @Override
    public OrderStatisticsVO statistics() {

        return orderMapper.getStatusCount();
    }

    /**
     * 历史订单查询
     */
    @Override
    public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.getOrdersPage(ordersPageQueryDTO);
        List<Orders> result = page.getResult();

        for (Orders orders : result) {
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrdersId(orders.getId());
            orders.setOrderDetailList(orderDetailList);
        }
        return new PageResult(page.getTotal(),result);
    }

    /**
     * 再来一单
     */
    @Transactional
    @Override
    public void repetition(Long id) {
        Orders orders = orderMapper.getById(id);
        ArrayList<OrderDetail> orderDetails = orderDetailMapper.getByOrdersId(orders.getId());

        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setCheckoutTime(null);
        orders.setCancelReason(null);
        orders.setRejectionReason(null);
        orders.setCancelTime(null);
        //在原时间上加上一个小时
        orders.setEstimatedDeliveryTime(orders.getOrderTime().plusHours(1));
        orders.setDeliveryTime(null);


        orderMapper.insert(orders);

        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrderId(orders.getId());
        }
        orderDetailMapper.insertBach(orderDetails);
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


        Map map = new HashMap();
         map.put("type",2);
         map.put("orderId",id);
         map.put("content","订单号:"+ orders.getNumber());



         webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
