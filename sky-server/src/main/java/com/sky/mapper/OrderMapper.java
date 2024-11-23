package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * C端-订单接口
 */
@Mapper
public interface OrderMapper {

    /**
     * 用户下单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 查询订单
     * @param build
     * @return
     */
    Page<OrderVO> select(Orders build);

    /**
     *根据Id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders selectById(Long id);



    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 管理端订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> selectContionalByAdmin(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计不同状态下的订单数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer selectBystatus(Integer status);

    /**
     * 更改订单状态
     * @param ordersConfirmDTO
     */
    @Update("update orders set status = #{status} where id = #{id}")
    void updateStatus(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 根据订单状态和时间选择订单
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> selectByTimeAndStatus(Integer status, LocalDateTime time);
}
