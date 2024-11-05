package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类相关接口
 */
@Service
public class CategoryServiceImp implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        //复制DTO至entity
        BeanUtils.copyProperties(categoryDTO,category);

        //设置默认状态禁用
        category.setStatus(StatusConstant.DISABLE);

        //设置创建时间
        //category.setCreateTime(LocalDateTime.now());

        //设置更新时间
       // category.setUpdateTime(LocalDateTime.now());

        //设置创建人
        //category.setCreateUser(BaseContext.getCurrentId());

        //设置修改人
       // category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.insert(category);

    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        //复制DTO至entity
        BeanUtils.copyProperties(categoryDTO,category);

        //设置更新时间
        //category.setUpdateTime(LocalDateTime.now());

        //设置修改人
        //category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult getByTypeAndName(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        String name = categoryPageQueryDTO.getName();
        Integer type = categoryPageQueryDTO.getType();
        Page<Category> page = categoryMapper.selectByTypeAndName(name,type);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 启用或禁用分类
     * @param status
     */
    @Override
    public void switchCategoryStatus(Integer status,Long id) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);
        //category.setUpdateTime(LocalDateTime.now());
        //category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    /**
     * 根据ID删除分类
     * @param id
     */
    @Override
    public void deleteByID(Long id) {
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.deleteByID(id);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Override
    public List<Category> selectByType(Integer type) {
        List<Category> list = categoryMapper.selectByType(type);
        return list;
    }
}
