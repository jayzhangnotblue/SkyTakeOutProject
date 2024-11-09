package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.Autofill;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setmeal
     */
    @Autofill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param categoryId
     * @param name
     * @param status
     * @return
     */
    Page<SetmealVO> pageSelect(Integer categoryId, String name, Integer status);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deletebyid(Long[] ids);

    /**
     * 根据ID查询套餐
     *
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);

    /**
     * 根据Id查询套餐，包括分类信息中的名字
     * @param id
     * @return
     */
    SetmealVO selectSetmealVOByIdWithCategoryname(Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 起售停售套餐
     * @param id
     * @param status
     */
    @Update("update setmeal set status = #{status} where id = #{id}")
    void updateStatus(Long id, Integer status);
}
