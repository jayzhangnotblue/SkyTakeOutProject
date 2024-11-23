package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态查询添加购物车时是否已存在菜品或者套餐
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> selectById(ShoppingCart shoppingCart);

    /**
     * 修改套餐或者菜品数量
     * @param shoppingCart1
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updatenum(ShoppingCart shoppingCart1);

    /**
     * 添加套餐或者菜品
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     * @param currentId
     */
    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void delete(Long currentId);

    /**
     * 删除购物车中一个商品
     * @param shoppingCart
     */
    void deleteByDishidOrSetmealId(ShoppingCart shoppingCart);

    /**
     * 批量加入购物车
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
