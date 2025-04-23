package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = new PageResult();
        //开始分页查询
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> pageQuery = categoryMapper.pageQuery(categoryPageQueryDTO);
        //查询结果
        pageResult.setRecords(pageQuery.getResult());
        pageResult.setTotal(pageQuery.getTotal());

        return pageResult;
    }

    /**
     * 根据分类类型查询分类
     * @param type
     * @return
     */
    public List<Category> query(Integer type) {
        ArrayList<Category> result;
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setType(type);
        result = (ArrayList<Category>) categoryMapper.query(categoryDTO);

        return result;

    }

    /**
     * 删除分类
     *
     * @param id
     */
    public void delete(Long id) {
        categoryMapper.delete(id);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.updateCategory(category);
    }

    /**
     * 启用禁用分类
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        categoryMapper.updateCategory(category);
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);


        categoryMapper.save(category);
    }
}
