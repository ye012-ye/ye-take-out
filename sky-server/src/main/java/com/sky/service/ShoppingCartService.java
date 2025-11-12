package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空
     */
    void cleanShoppingCart();

    /**
     * 删除
     */
    void delete(ShoppingCartDTO shoppingCartDTO);
}
