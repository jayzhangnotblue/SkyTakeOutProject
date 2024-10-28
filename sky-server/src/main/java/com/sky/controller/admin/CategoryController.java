package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类相关接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result saveCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类，参数为：{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}",categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询参数：{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.getByTypeAndName(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用分类
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用分类")
    public Result turnOnOrBanCategory(@PathVariable Integer status,Long id){
        log.info("启用或禁用分类，status：{}",status);
        categoryService.switchCategoryStatus(status,id);
        return Result.success();
    }

    /**
     * 根据ID删除分类
     */
    @DeleteMapping()
    @ApiOperation("根据Id删除分类")
    public Result deleteByID(Long id){
        log.info("根据ID删除分类，ID：{}",id);
        categoryService.deleteByID(id);
        return Result.success();
    }
    /**
     * 根据类型查询分类
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> selectByType(Integer type){
        log.info("根据类型查询分类,类型Type：{}",type);
        List<Category> list = categoryService.selectByType(type);
        return Result.success(list);
    }
}
