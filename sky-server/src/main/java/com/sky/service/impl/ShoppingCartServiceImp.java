package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * C端
 */
@Service
public class ShoppingCartServiceImp implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private FlavorMapper flavorMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void saveDishOrSetmeal(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //判断表中有没有已存在的菜品或者套餐，有则改数量
        List<ShoppingCart> list = shoppingCartMapper.selectById(shoppingCart);
        if(list!=null&&list.size()==1){
            //存在重复的菜品或者套餐
            ShoppingCart shoppingCart1 = list.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updatenum(shoppingCart1);
        }else {
            //不存在菜品和套餐即插入数据
            Long setmealId = shoppingCart.getSetmealId();
            if(setmealId!=null){
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
            }else {
                Dish dish = dishMapper.selectById(shoppingCart.getDishId());
                List<DishFlavor> list1 = flavorMapper.selectById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart build = ShoppingCart
                .builder().userId(currentId).build();
        List<ShoppingCart> list = shoppingCartMapper.selectById(build);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShopping() {
        Long currentId = BaseContext.getCurrentId();
        shoppingCartMapper.delete(currentId);
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(currentId)
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .build();
        //判断表中存在的菜品或者套餐数量是否为1，1则删除，其余数量减一
        List<ShoppingCart> list = shoppingCartMapper.selectById(shoppingCart);
        ShoppingCart shoppingCart1 = list.get(0);
        Integer number = shoppingCart1.getNumber();
        if(number==1){
            shoppingCartMapper.deleteByDishidOrSetmealId(shoppingCart);
        }else {
            shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            shoppingCartMapper.updatenum(shoppingCart1);
        }

    }
}
