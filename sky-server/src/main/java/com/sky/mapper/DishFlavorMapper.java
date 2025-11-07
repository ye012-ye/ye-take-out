package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据ID删除
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishID}")
    void deleteById(Long dishId);

    /**
     * 根据ID批量删除
     * @param dishIds
     */
    void deleteByIds(List<Long> dishIds);

    /**
     * 根据ID查询
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getById(Long dishId);
}
