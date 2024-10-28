package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * 分类相关接口
 */

public interface CategoryService {
    /**
     * 新增分类
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 修改分类
     * @param categoryDTO
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult getByTypeAndName(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用或禁用分类
     * @param status
     */
    void switchCategoryStatus(Integer status,Long id);

    /**
     * 根据ID删除分类
     * @param id
     */
    void deleteByID(Long id);

    /**
     * 根据类型查询分类
     * @return
     */
    List<Category> selectByType(Integer type);
}
