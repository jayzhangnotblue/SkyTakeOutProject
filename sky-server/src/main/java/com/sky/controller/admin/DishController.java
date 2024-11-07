package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品相关接口
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    DishService dishService;
    /**
     * 新增菜品带口味
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result saveDishWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveDish(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改菜品
     */
    @PutMapping()
    @ApiOperation("修改菜品")
    public Result updataDishWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("修改菜品，参数为：{}",dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }
    /**
     * 批量删除菜品
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result deleteDish(Long[] ids){
        log.info("批量删除菜品：{}",ids);
        dishService.deleteByArra(ids);
        return Result.success();
    }

    /**
     * 根据ID查询菜品
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜品")
    public Result queryByid(@PathVariable Long id){
        log.info("根据id查询菜品id；{}",id);
        DishVO dishVO = dishService.selectByid(id);
        return Result.success(dishVO);
    }
}
