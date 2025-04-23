package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据套餐id查询该套餐菜品
     *
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> list = setmealMapper.pageQuery(setmealPageQueryDTO);

        //处理查询结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(list.getTotal());
        pageResult.setRecords(list.getResult());

        return pageResult;
    }

    /**
     * 起售或停售套餐
     * @param status
     * @param id
     */
    public void startOrStop(Integer status,Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        //更改套餐的起售或停售状态
        setmealMapper.updateSetmeal(setmeal);

    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     */
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改套餐信息
        setmealMapper.updateSetmeal(setmeal);
        //修改套餐的菜品信息
        //通过套餐id删除绑定在套餐上的菜品
        setmealMapper.deleteSetmealDish(setmealDTO.getId());
        //批量插入套餐绑定的菜品
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        dishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
        setmealMapper.insertBatchSetmealDish(dishes);
    }

    /**
     * 根据套餐id获取套餐
     *
     * @param id
     * @return
     */
    public SetmealVO getDishById(Long id) {
        //获取套餐信息
        Setmeal setmeal = setmealMapper.getBySetmealId(id);
        //获取套餐绑定的菜品
        List<SetmealDish> setmealDishes = setmealMapper.getSetmealDishById(id);

        //构建返回对象
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 根据套餐id批量删除套餐
     * @param ids
     */
    public void batchDeleteById(ArrayList<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getBySetmealId(id);
            if (StatusConstant.ENABLE == setmeal.getStatus()) {
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        ids.forEach(setmealId -> {
            //删除套餐表中的数据
            ArrayList<Long> setmealIds = new ArrayList<>();
            setmealIds.add(setmealId);
            setmealMapper.batchDeleteSetmeal(setmealIds);
            //删除套餐菜品关系表中的数据
            setmealMapper.deleteSetmealDish(setmealId);
        });

    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    public void insert(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //新增套餐绑定的菜品
        setmealMapper.insertBatchSetmealDish(setmealDTO.getSetmealDishes());
        //插入套餐信息
        setmealMapper.insertSetmeal(setmeal);
    }
}
