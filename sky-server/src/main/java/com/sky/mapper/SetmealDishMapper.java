package com.sky.mapper;

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
}
