package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入
     */
    void insert(Orders orders);

    /**
     * 分页
     */
    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查看订单详细
     */
    Orders getById(Long id);

    /**
     * 接单
     */
    @Update("update orders set status = #{status} where id = #{id}")
    void updateStatusById(OrdersConfirmDTO orderId);

    /**
     * 修改
     * @param orders
     */
    void updateById(Orders orders);

    /**
     * 各个订单统计
     */
    OrderStatisticsVO getStatusCount();

    /**
     * page查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> getOrdersPage(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where status = #{status} and order_time < #{ordersTime}")
    List<Orders> getByStatusAndOrdersTimeLT(Integer status, LocalDateTime ordersTime);


    Double sumByMap(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
