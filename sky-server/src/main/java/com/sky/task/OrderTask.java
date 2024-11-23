package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Configuration
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;


    /**
     * 订单支付超时处理
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void orderTimeOut(){
        //每分钟执行一次查询订单支付超时操作
        log.info("订单支付超时处理：{}",new Date());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> list = orderMapper.selectByTimeAndStatus(Orders.PENDING_PAYMENT,time);

        //判断是否存在超时订单，存在即修改
        if(list!=null && list.size()!=0){
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 订单一直派送处理，深夜1点将12点还在派送的订单处理
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void deliveryTimeOutOrder(){
        //查询还有没有订单在派送
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList = orderMapper.selectByTimeAndStatus(Orders.DELIVERY_IN_PROGRESS, time);

        //判断是否存在，存在即修改订单状态
        if(ordersList!=null&&ordersList.size()!=0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
