package com.yx198.helloserver3.controller;

import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.dto.UserDTO;
import com.yx198.helloserver3.service.UserService;
import com.yx198.helloserver3.vo.UserDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // 登录
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // 根据 ID 查询用户
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    // 分页查询
    @GetMapping("/page")
    public Result<Object> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return userService.getUserPage(pageNum, pageSize);
    }

    // 查询用户详情（多表联查 + Redis 缓存）
    @GetMapping("/{id}/detail")
    public Result<UserDetailVO> getUserDetail(@PathVariable("id") Long userId) {
        return userService.getUserDetail(userId);
    }

    // 更新用户扩展信息
    @PutMapping("/{id}/info")
    public Result<String> updateUserInfo(@PathVariable("id") Long userId, @RequestBody UserDetailVO userDetailVO) {
        return userService.updateUserInfo(userId, userDetailVO);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long userId) {
        return userService.deleteUser(userId);
    }
}