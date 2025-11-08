package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品，参数：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理缓存
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页请求参数：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品ID：{}",ids);
        dishService.deleteBatch(ids);

        //将所有菜品缓存清理 所有以dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 根据Id查询
     */
    @GetMapping("/{id}")
    public Result<DishVO> selectById(@PathVariable Long id){
        log.info("id：{}",id);
        DishVO dishVO = dishService.getByWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品和对应的口味
     * @return
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品和对应的口味 : {}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //将所有菜品缓存清理 所有以dish_开头的key
        cleanCache("dish_*");

        return Result.success();

    }
    /**
     * 根据categoryId查询
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        log.info("套餐ID：{}",categoryId);
        List<DishVO> dishVO = dishService.getBYWithFlavorCategoryID(categoryId);
        return Result.success(dishVO);
    }

    /**
     * 修改当前菜品状态
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public Result updateByStatus(@PathVariable Integer status,Long id){
        log.info("当前状态：{}",status);
        dishService.updateByStatus(status,id);
        return Result.success();
    }



    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
