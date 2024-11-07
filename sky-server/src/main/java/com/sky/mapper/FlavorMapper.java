package com.sky.mapper;

import com.sky.entity.DishFlavor;
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

    /**
     * 修改口味
     * @param flavors
     */
    void updateFlavoers(List<DishFlavor> flavors);

    /**
     * 删除菜品对应的口味
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 根据菜品ID选择对应的口味
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> selectById(Long id);

}
