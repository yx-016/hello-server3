package com.yx198.helloserver3.service.impl;

import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.common.ResultCode;
import com.yx198.helloserver3.dto.UserDTO;
import com.yx198.helloserver3.entity.User;
import com.yx198.helloserver3.mapper.UserMapper;
import com.yx198.helloserver3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    // 临时用 Map 模拟数据库（下节课再换成真正的 Mapper）
    private Map<String, String> userDb = new HashMap<>();

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 校验用户是否已存在
        if (userDb.containsKey(userDTO.getUsername())) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 存入模拟数据库
        userDb.put(userDTO.getUsername(), userDTO.getPassword());

        return Result.success("注册成功");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 校验用户是否存在
        if (!userDb.containsKey(userDTO.getUsername())) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 2. 校验密码是否正确
        String dbPassword = userDb.get(userDTO.getUsername());
        if (!dbPassword.equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 3. 生成 Token（模拟）
        String token = UUID.randomUUID().toString();
        return Result.success(token);
    }
}