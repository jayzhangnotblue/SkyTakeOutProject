package com.sky.controller.admin;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端订单管理接口
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "商家端订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单搜索
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> pageOrder(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("商家端订单搜索：{}",ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     */

    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> orderNum(){
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.getOrdernum();
        return Result.success(orderStatisticsVO);
    }


    /**
     * 查询订单详情
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> OrderDetail(@PathVariable Long id){
        log.info("查询订单详情,Id:{}",id);
        OrderVO orderVO = orderService.OrderdetailQuery(id);
        return Result.success(orderVO);
    }


    /**
     * 接单
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result ordersConfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("管理端接单,Id：{}",ordersConfirmDTO.getId());
        orderService.orderConfirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单，Id：{}",ordersRejectionDTO.getId());
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){


        log.info("取消订单,Id:{}",ordersCancelDTO.getId());
        orderService.cancelOrderAdmin(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliverOrder(@PathVariable Long id){
        log.info("派送订单，订单号为：{}",id);
        orderService.deliverOrder(id);
        return Result.success();
    }


    /**
     * 完成订单
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result competeOrder(@PathVariable Long id){
        log.info("完成订单，Id：{}",id);
        orderService.competeOrder(id);
        return Result.success();
    }
}
