package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@Api("套餐相关接口")
@Slf4j
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐:{}", setmealPageQueryDTO);
        PageResult result = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(result);
    }
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("{}套餐，id={}", status == 1 ? "起售" : "停售",id);
        setmealService.startOrStop(status, id);

        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐信息")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐信息:{}",setmealDTO);
        setmealService.updateSetmeal(setmealDTO);

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getDishById(@PathVariable Long id) {
        log.info("根据套餐id查询套餐");
        SetmealVO setmealVO= setmealService.getDishById(id);

        return Result.success(setmealVO);
    }

    @DeleteMapping
    @ApiOperation("根据id批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result batchDeleteById(@RequestParam ArrayList<Long> ids){
        log.info("根据id批量删除套餐:{}",ids);
        setmealService.batchDeleteById(ids);

        return Result.success();
    }

    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    @PostMapping
    @ApiOperation("新增套餐")
    public Result insert(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐:{}",setmealDTO);
        setmealService.insert(setmealDTO);

        return Result.success();
    }
}
