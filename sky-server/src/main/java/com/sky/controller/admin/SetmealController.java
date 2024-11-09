package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SermealService;
import com.sky.service.impl.SetmealServiceImp;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 套餐相关接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    SermealService sermealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result saveSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新建套餐:{}",setmealDTO);
        sermealService.saveWithDishes(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询：{}",setmealPageQueryDTO);
        PageResult pageResult = sermealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteBatch(Long[] ids){
        log.info("批量删除套餐:{}",ids);
        sermealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据ID查询套餐
     */
    @GetMapping("/{id}")
    @ApiOperation("根据Id查询套餐")
    public Result<SetmealVO> selectById(@PathVariable Long id){
        log.info("根据Id查询套餐：{}",id);
        SetmealVO setmealVO =sermealService.selectSetmealVOById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result updateSetmealWithDish(@RequestBody SetmealDTO setmealDTO ){
        log.info("修改套餐：{}",setmealDTO);
        sermealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 起售停售套餐
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售套餐")
    public Result swichStatus(Long id ,@PathVariable Integer status){
        log.info("起售停售套餐,id:{},status:{}",id,status);
        sermealService.swtchStatus(id,status);
        return Result.success();
    }
}
