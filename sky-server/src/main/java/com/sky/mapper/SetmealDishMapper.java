package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     *
     * @return
     */
    List<Long> getSetmealIdByDishId(List<Long> dishIds);


    /**
     * 新增
     * @param setmealDishes
     */
    void insertSetmealDishs(List<SetmealDish> setmealDishes);

    /**
     * setmealId
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getById(Long setmealId);

    /**
     * 根据BySetmealId删除
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 批量删除
     * @param ids
     */
    void batchByIds(List<Long> ids);
}
