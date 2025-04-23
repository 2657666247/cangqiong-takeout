package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public interface DishService {

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品和对应的口味数据
     * @param dishDTO
     */
    void saveWithFlaver(DishDTO dishDTO);

    /**
     * 删除菜品
     * @param ids
     */
    void deleteBatch(ArrayList<Long> ids);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 根据id获取数据
     * @param id
     */
    DishVO getById(Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    ArrayList<Dish> getByCategoryId(Long categoryId);

    /**
     * 起售或停售菜品
     * @param id
     * @param status
     */
    void startOrStop(Long id, Integer status);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);


}
