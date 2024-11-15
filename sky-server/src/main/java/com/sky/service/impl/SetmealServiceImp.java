package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐相关接口
 */
@Service
public class SetmealServiceImp implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveWithDishes(SetmealDTO setmealDTO) {
        //存储到setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);

        //存储到setmealdish表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        Long id = setmeal.getId();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Integer categoryId = setmealPageQueryDTO.getCategoryId();
        String name = setmealPageQueryDTO.getName();
        Integer status = setmealPageQueryDTO.getStatus();
        Page<SetmealVO> page = setmealMapper.pageSelect(categoryId,name,status);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(Long[] ids) {
        //起售中的套餐不能删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if(setmeal.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //先删除关联菜品表SetmealDish
        setmealDishMapper.deleteBySetmealId(ids);

        //再删除setmeal表
        setmealMapper.deletebyid(ids);
    }


    /**
     * 根据ID查询套餐
     */
    @Override
    public SetmealVO selectSetmealVOById(Long id) {
        //先查询setmeal表
        SetmealVO setmealVO = setmealMapper
                .selectSetmealVOByIdWithCategoryname(id);

        //再查询setmealdish表
        List<SetmealDish> list = setmealDishMapper.selectBySetmealId(id);
        setmealVO.setSetmealDishes(list);
        return setmealVO;

    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        //先修改Setmeal表
        setmealMapper.updateSetmeal(setmealDTO);

        //再修改SetmealDish表
        Long id = setmealDTO.getId();
        Long[] ids = {id};
        //先删除Dish
        setmealDishMapper.deleteBySetmealId(ids);
        //再添加Dish
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        Long setmealId = setmealDTO.getId();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 起售停售套餐
     * @param id
     * @param status
     */
    @Override
    public void swtchStatus(Long id, Integer status) {
        //起售套餐时判断有没有菜品停售
        if(status == StatusConstant.ENABLE){
            List<SetmealDish> list = setmealDishMapper.selectBySetmealId(id);
            List<Long> dishId = new ArrayList<>();
            for (SetmealDish setmealDish : list) {
                dishId.add(setmealDish.getDishId());
            }
            for (Long aLong : dishId) {
                Integer dishStatus = dishMapper.selectById(aLong).getStatus();
                if(dishStatus == StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        //修改状态Status数据
        setmealMapper.updateStatus(id,status);
    }


    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
