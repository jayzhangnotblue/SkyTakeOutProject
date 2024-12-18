package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * 采用PageHelper工具
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工账号状态
     * @param status
     * @param id
     */
    void switchStatus(Integer status, Long id);

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    Employee getByID(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void updateEmployee(EmployeeDTO employeeDTO);
}
