package com.yx198.helloserver3.service;

import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.dto.UserDTO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
}