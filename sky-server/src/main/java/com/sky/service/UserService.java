package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * C端-用户接口
 */
public interface UserService {
    User login(UserLoginDTO userLoginDTO);
}
