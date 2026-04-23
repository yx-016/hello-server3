package com.yx198.helloserver3.service;

import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.dto.UserDTO;
import com.yx198.helloserver3.vo.UserDetailVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
    Result<UserDetailVO> getUserDetail(Long userId);  // 新增
    Result<String> updateUserInfo(Long userId, UserDetailVO userDetailVO);  // 新增
    Result<String> deleteUser(Long userId);  // 新增
}