package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品相关接口
 */
@Service
public class DishServiceImp implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    FlavorMapper flavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insertDish(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() != 0) {
            Long id = dish.getId();
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);
            }
            flavorMapper.insertFlavor(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        String name = dishPageQueryDTO.getName();
        Integer status = dishPageQueryDTO.getStatus();
        Integer categoryId = dishPageQueryDTO.getCategoryId();
        Page<DishVO> page = dishMapper.selectByCateidNameAndStatus(categoryId, name, status);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        Long[] dishFlavors = new Long[1];
        //先删除后添加
        dishFlavors[0]=dishDTO.getId();
        flavorMapper.delete(dishFlavors);
        if (flavors != null && flavors.size() != 0) {
            //添加
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            flavorMapper.insertFlavor(flavors);
        }
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByArra(Long[] ids) {
        //判断删除的菜品是不是在售状态
        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断删除的菜品是否关联套餐
        List<Long> list = setmealDishMapper.selectByDishID(ids);
        if (list != null && list.size() != 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
        dishMapper.delete(ids);

        //删除菜品带的口味
        flavorMapper.delete(ids);
    }

    /**
     * 根据id菜品查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public DishVO selectByid(Long id) {
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish, dishVO);
        List<DishFlavor> list = flavorMapper.selectById(id);
        dishVO.setFlavors(list);
        return dishVO;
    }

    /**
     * 根据分类ID查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> selectByCategory(Long categoryId) {
        List<DishVO> list = dishMapper.selectByCategoryId(categoryId);
        return list;
    }

    /**
     * 菜品起售、停售
     */
    @Override
    public void switchStatus(Long id,Integer status) {
        dishMapper.updateDishStatus(id,status);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = flavorMapper.selectById(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


}
