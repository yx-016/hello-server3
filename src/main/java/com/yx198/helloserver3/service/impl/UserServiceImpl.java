package com.yx198.helloserver3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.common.ResultCode;
import com.yx198.helloserver3.dto.UserDTO;
import com.yx198.helloserver3.entity.User;
import com.yx198.helloserver3.mapper.UserMapper;
import com.yx198.helloserver3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 注册
    @Override
    public Result<String> register(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);

        return Result.success("注册成功!");
    }

    // 登录
    @Override
    public Result<String> login(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        String token = UUID.randomUUID().toString();
        return Result.success(token);
    }

    // 根据 ID 查询用户
    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        String data = "查询成功，用户：" + user.getUsername();
        return Result.success(data);
    }

    // 分页查询用户列表（新增）
    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        // 1. 创建分页对象
        Page<User> pageParam = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询（第二个参数为 null 表示无条件查询所有）
        Page<User> resultPage = userMapper.selectPage(pageParam, null);

        // 3. 返回结果（包含 records、total、pages 等）
        return Result.success(resultPage);
    }
}