package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 菜品相关接口
 */
public interface DishService {
    /**
     * 新增菜品
     */
    void saveDish(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteByArra(Long[] ids);

    /**
     * 根据ID查询菜品
     * @param id
     * @return
     */
    DishVO selectByid(Long id);

    /**
     * 根据CategoryId查询菜品
     * @return
     */
    List<DishVO> selectByCategory(Long categoryId);

    /**
     * 菜品起售、停售
     * @param status
     */
    void switchStatus(Long id,Integer status);
}
