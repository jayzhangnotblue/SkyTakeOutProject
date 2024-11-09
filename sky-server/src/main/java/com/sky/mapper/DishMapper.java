package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.Autofill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @Autofill(OperationType.INSERT)
    void insertDish(Dish dish);

    /**
     * 菜品分页查询
     * @param categoryId
     * @param name
     * @param status
     * @return
     */
    Page<DishVO> selectByCateidNameAndStatus(Integer categoryId, String name, Integer status);

    /**
     * 菜品修改
     * @param dish
     */
    @Autofill(OperationType.UPDATE)
    void updateDish(Dish dish);

    /**
     * 批量删除菜品
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 根据Id选择菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish selectById(Long id);

    /**
     * 根据分类Id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<DishVO> selectByCategoryId(Long categoryId);

    /**
     * 菜品起售、停售
     * @param status
     */
    @Update("update dish set status = #{status} where id=#{id}")
    void updateDishStatus(Long id,Integer status);
}
