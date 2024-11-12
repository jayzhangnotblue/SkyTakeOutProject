package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端店铺操作接口
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺操作接口(管理端)")
@Slf4j
public class StatusController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setShopStatus(@PathVariable Integer status){
        log.info("设置营业状态：{}",status==1?"营业中":"已打样");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    /**
     * 管理端营业状态**
     */
    @GetMapping("/status")
    @ApiOperation("管理端营业状态")
    public Result<Integer> getAdminStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取管理端营业状态：{}",status==1?"营业中":"已打样");
        return Result.success(status);
    }

}
