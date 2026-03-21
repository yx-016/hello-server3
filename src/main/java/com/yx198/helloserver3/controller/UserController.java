package com.yx198.helloserver3.controller;

import com.yx198.helloserver3.entity.User;
import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.common.ResultCode;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 模拟数据库
    private Map<Long, User> userDatabase = new HashMap<>();
    private Long currentId = 1L;

    // 1. 获取用户信息（查）
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable("id") Long id) {
        User user = userDatabase.get(id);
        if (user == null) {
            return Result.error(ResultCode.ERROR);
        }
        return Result.success(user);
    }

    // 2. 新增用户（增）
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        user.setId(currentId++);
        userDatabase.put(user.getId(), user);
        return Result.success(user);
    }

    // 3. 全量更新用户信息（改）
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        if (!userDatabase.containsKey(id)) {
            return Result.error(ResultCode.ERROR);
        }
        user.setId(id);
        userDatabase.put(id, user);
        return Result.success(user);
    }

    // 4. 删除用户（删）
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        if (!userDatabase.containsKey(id)) {
            return Result.error(ResultCode.ERROR);
        }
        userDatabase.remove(id);

        String data = "删除成功，已移除 ID 为 " + id + " 的用户";
        return Result.success(data);
    }
}