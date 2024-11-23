package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * C端-订单接口
 */
@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //判断地址是否存在
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //判断购物车是否为空
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.selectById(shoppingCart);
        if(list==null||list.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //插入订单表
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setNumber(String.valueOf(LocalDateTime.now()));
        orders.setUserId(currentId);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setStatus(Orders.PENDING_PAYMENT);
        //调用mapper接口插入
        orderMapper.insert(orders);

        //订单明细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        //向明细表插入n条数据
        orderDetailMapper.insertBatch(orderDetailList);

        //删除购物车
        shoppingCartMapper.delete(currentId);

        //返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        /*//调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );*/
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }


    /**
     * 历史订单查询-C端
      * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Integer status = ordersPageQueryDTO.getStatus();

        //先获取订单里的主键Id
        Long currentId = BaseContext.getCurrentId();
        Orders build = Orders.builder().userId(currentId)
                .status(status)
                .build();
        Page<OrderVO> orderVO =  orderMapper.select(build);

        if (orderVO != null && orderVO.getTotal() > 0){
            //将OrderDetail数据封装到OrderVO
            for (OrderVO orders : orderVO) {
                Long id = orders.getId();
                List<OrderDetail> list = orderDetailMapper.selectByOrderId(id);
                orders.setOrderDetailList(list);
            }
        }
        return new PageResult(orderVO.getTotal(),orderVO.getResult());
    }

    /**
     * 订单详情查询
     * @param id
     * @return
     */
    @Override
    public OrderVO OrderdetailQuery(Long id) {
        //先查询订单表
        Orders orders = orderMapper.selectById(id);
        //再查询订单详情表
        List<OrderDetail> list = orderDetailMapper.selectByOrderId(id);
        //封装到OrderVo
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancelOrder(Long id) {

        //先确定订单在不在
        Orders selectById = orderMapper.selectById(id);
        if(selectById==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //再确认订单状态
        Integer status = selectById.getStatus();
        if(status>2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


        // 更新订单状态、取消原因、取消时间
        Orders orders = new Orders();
        orders.setId(selectById.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setPayStatus(Orders.REFUND);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repeatOrder(Long id) {
        //查询当前用户Id
        Long id1 = BaseContext.getCurrentId();
        //将新订单添加至购物车
        List<OrderDetail> orderDetaillist = orderDetailMapper.selectByOrderId(id);
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetaillist) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart,"id");
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(id1);
            shoppingCartList.add(shoppingCart);
        }

        shoppingCartMapper.insertBatch(shoppingCartList);

    }

    /**
     * 管理端订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        //先根据条件查询订单
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.selectContionalByAdmin(ordersPageQueryDTO);
        
        //添加订单包含的菜品，以字符串形式展示
        for (OrderVO orderVO : page) {
            Long id = orderVO.getId();
            List<OrderDetail> list = orderDetailMapper.selectByOrderId(id);
            String orderdishes="";
            for (OrderDetail orderDetail : list) {
                orderdishes =orderdishes + orderDetail.getName()+"数量:"+orderDetail.getNumber()+" ";
            }
            orderVO.setOrderDishes(orderdishes);
        }
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO getOrdernum() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        //分别获得各种情况下的状态码
        Integer toBeConfirmed = Orders.TO_BE_CONFIRMED;
        Integer confirmed = Orders.CONFIRMED;
        Integer deliveryInProgress = Orders.DELIVERY_IN_PROGRESS;

        //调用mapper查询
        Integer toBeConfirmedres = orderMapper.selectBystatus(toBeConfirmed);
        Integer confirmedres = orderMapper.selectBystatus(confirmed);
        Integer deliveryInProgressres = orderMapper.selectBystatus(deliveryInProgress);
        orderStatisticsVO.setConfirmed(confirmedres);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmedres);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgressres);
        return orderStatisticsVO;
    }


    /**
     * 接单
     */
    @Override
    public void orderConfirm(OrdersConfirmDTO ordersConfirmDTO) {
        //修改状态为接单
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        //调用mapper更改状态
        orderMapper.updateStatus(ordersConfirmDTO);
    }

    /**
     * 拒单
     */
    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        //先判断订单是否为待接单状态
        Orders orders = orderMapper.selectById(ordersRejectionDTO.getId());
        Integer status = orders.getStatus();
        if(orders==null||status!=Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders ordersUpdate = new Orders();

        //如果订单已支付则退款
        if(orders.getPayStatus()==Orders.PAID){
            //调用whachatUtil取消订单代码略

            ordersUpdate.setPayStatus(Orders.REFUND);
        }
        //修改订单状态
        ordersUpdate.setStatus(Orders.CANCELLED);
        ordersUpdate.setId(ordersRejectionDTO.getId());
        ordersUpdate.setCancelReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(ordersUpdate);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrderAdmin(OrdersCancelDTO ordersCancelDTO) {

        // 根据id查询订单
        Orders ordersDB = orderMapper.selectById(ordersCancelDTO.getId());

        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == 1) {
            //用户已支付，需要退款
        }

        //直接修改订单
        Long id = ordersCancelDTO.getId();
        String cancelReason = ordersCancelDTO.getCancelReason();
        Orders orders = Orders.builder().id(id)
                .cancelReason(cancelReason)
                .cancelTime(LocalDateTime.now())
                .status(Orders.CANCELLED).build();
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void deliverOrder(Long id) {
        //判断订单是否已经处于待派送状态
        Orders orders = orderMapper.selectById(id);
        Integer status = orders.getStatus();
        if(status!=Orders.CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //修改订单状态
        Orders ordersUpdate = new Orders();
        ordersUpdate.setStatus(Orders.DELIVERY_IN_PROGRESS);
        ordersUpdate.setId(id);
        orderMapper.update(ordersUpdate);
    }

    /**
     * 完成订单
     */
    @Override
    public void competeOrder(Long id) {
        Orders orders = orderMapper.selectById(id);
        if(orders.getStatus()!=Orders.DELIVERY_IN_PROGRESS){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders ordersUpdate = new Orders();
        ordersUpdate.setStatus(Orders.COMPLETED);
        ordersUpdate.setId(id);
        orderMapper.update(ordersUpdate);
    }
}
