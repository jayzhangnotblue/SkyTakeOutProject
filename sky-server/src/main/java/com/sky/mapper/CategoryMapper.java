package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.Autofill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类相关接口
 */
@Mapper
public interface CategoryMapper {
    /**
     * 新增分类
     * @param category
     */

    @Autofill(OperationType.INSERT)
    @Insert("insert into category " +
            "(id,type, name, sort, status, create_time, update_time, create_user, update_user) VALUES " +
            "(#{id},#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    @Autofill(OperationType.UPDATE)
    void update(Category category);

    /**
     * 分类分页查询
     * @param name
     * @param type
     * @return
     */
    Page<Category> selectByTypeAndName(String name, Integer type);

    /**
     * 根据ID删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteByID(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> selectByType(Integer type);
}
