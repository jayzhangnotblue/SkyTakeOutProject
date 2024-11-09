package com.sky.mapper;

import com.sky.dto.SetmealDTO;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ID查询是否有关联的套餐
     * @param ids
     * @return
     */
    List<Long> selectByDishID(Long[] ids);

    /**
     * 加入套餐
     * @param setmealDishes
     */

    void insert(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐Id删除对应的套餐菜品关联关系
     * @param ids
     */
    void deleteBySetmealId(Long[] ids);

    /**
     * 根据套餐Id查询对应的套餐菜品关联关系
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> selectBySetmealId(Long id);


}
