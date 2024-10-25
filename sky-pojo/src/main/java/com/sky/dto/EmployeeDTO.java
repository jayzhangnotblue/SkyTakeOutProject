package com.sky.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "新增员工传入进来的数据")
public class EmployeeDTO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("用户名/账号")
    private String username;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("电话号码")
    private String phone;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("身份证号")
    private String idNumber;

}
