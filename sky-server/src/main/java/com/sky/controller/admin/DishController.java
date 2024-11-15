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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品相关接口
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

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

        cleanCache("dish_"+dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询:{}",dishPageQueryDTO);
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
        //删除所有dish开头的缓存
        cleanCache("dish*");
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
        //删除所有dish开头的缓存
        cleanCache("dish*");
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

    /**
     * 根据分类ID查询菜品
     */
    @GetMapping("/list")
    @ApiOperation("根据分类ID查询菜品")
    public Result<List<DishVO>> queryByCategoryId(Long categoryId){
        log.info("根据分类ID查询菜品:{}",categoryId);
        List<DishVO> list = dishService.selectByCategory(categoryId);
        return Result.success(list);
    }
    /**
     * 菜品起售、停售
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售")
    public Result switchStatus( Long id,@PathVariable Integer status){
        log.info("菜品起售、停售,id:{},status:{}",id,status);
        dishService.switchStatus(id,status);
        //删除所有dish开头的缓存
        cleanCache("dish*");
        return Result.success();
    }

    /**
     * 删除缓存
     */
    public void cleanCache(String key){
        Set keys = redisTemplate.keys(key);
        redisTemplate.delete(keys);
    }
}
