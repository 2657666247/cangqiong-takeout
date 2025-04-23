package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {


    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     */
    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 保存数据
     */
    @AutoFill(OperationType.INSERT)
    void save(Dish dish);
    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatchFlavors(List<DishFlavor> flavors);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(ArrayList<Long> ids);

    /**
     * 根据ids批量获取菜品
     * @param ids
     * @return
     */
    ArrayList<Dish> getByIdBatch(ArrayList<Long> ids);

    /**
     * 根据菜品id判断是否有菜品被套餐绑定
     * @param ids
     * @return
     */
    Integer countMealDish(ArrayList<Long> ids);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    /**
     * 批量删除口味
     * @param flavors
     */
    void deleteBatchFlavors(List<DishFlavor> flavors);

    /**
     * 根据菜品id获取菜品口味
     * @param id
     * @return
     */
    ArrayList<DishFlavor> getFlavorById(Long id);

    /**
     * 根据分类id获取菜品
     * @param categoryId
     * @return
     */

    ArrayList<Dish> getByCategoryId(Long categoryId,Integer status);

    /**
     * 根据id获取菜品
     * @return
     */
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
