package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        int page = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();
        //开始分页查询
        PageHelper.startPage(page, pageSize);
        Page<Dish> pageResult = dishMapper.pageQuery(dishPageQueryDTO);
        //取出查询总数和查询结果
        long total = pageResult.getTotal();
        List<Dish> dishes = pageResult.getResult();
        //构建查询结果
        PageResult pageResult1 = new PageResult();
        pageResult1.setTotal(total);
        pageResult1.setRecords(dishes);

        return pageResult1;
    }

    /**
     * 新增菜品和口味
     *
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlaver(DishDTO dishDTO) {
        //向菜品表添加一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.ENABLE);

        dishMapper.save(dish);
        //向口味表添加n条数据
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishMapper.insertBatchFlavors(flavors);

        }
    }

    /**
     * 删除菜品
     *
     * @param ids
     */
    public void deleteBatch(ArrayList<Long> ids) {

        //判断菜品是否在起售，起售期间不能被删除
        ArrayList<Dish> dishs = dishMapper.getByIdBatch(ids);
        for (Dish dish : dishs) {
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否被套餐绑定，绑定期间不能被删除
        Integer count = dishMapper.countMealDish(ids);
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deleteBatch(ids);

    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Transactional
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dish.getId());
        });

        //更新菜品
        dishMapper.updateDish(dish);
        //更新菜品对应的口味
        dishMapper.deleteBatchFlavors(flavors);
        dishMapper.insertBatchFlavors(flavors);

    }

    /**
     * 根据id获取菜品数据
     *
     * @param id
     */
    public DishVO getById(Long id) {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(id);
        //根据id获取菜品
        Dish dish = dishMapper.getByIdBatch(ids).get(0);

        //根据菜品id获取该菜品的口味
        ArrayList<DishFlavor> dishFlavors = dishMapper.getFlavorById(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;

    }

    /**
     * 根据分类id获取菜品
     *
     * @param categoryId
     * @return
     */
    public ArrayList<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId, null);
    }

    /**
     * 起售或停售菜品
     *
     * @param id
     * @param status
     */
    public void startOrStop(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);

        dishMapper.updateDish(dish);
    }

    /**
     * 根据菜品分类和起售状态查询菜品
     *
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        //根据分类id获取菜品
        ArrayList<Dish> dishes = dishMapper.getByCategoryId(dish.getCategoryId(), dish.getStatus());
        ArrayList<DishVO> dishVOS = new ArrayList<>();
        for (int i = 0; i < dishes.size(); i++) {
            //构建DishVO
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dishes.get(i), dishVO);
            //根据菜品id获取菜品口味
            ArrayList<DishFlavor> flavorById = dishMapper.getFlavorById(dishes.get(i).getId());
            dishVO.setFlavors(flavorById);
            dishVOS.add(dishVO);
        }

        return dishVOS;
    }



}
