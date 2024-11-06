package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FlavorMapper {
    /**
     * 批量新增口味
     * @param flavors
     */

    void insertFlavor(List<DishFlavor> flavors);
}
