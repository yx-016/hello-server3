package com.yx198.helloserver3.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yx198.helloserver3.common.Result;
import com.yx198.helloserver3.common.ResultCode;
import com.yx198.helloserver3.dto.UserDTO;
import com.yx198.helloserver3.entity.User;
import com.yx198.helloserver3.entity.UserInfo;
import com.yx198.helloserver3.mapper.UserInfoMapper;
import com.yx198.helloserver3.mapper.UserMapper;
import com.yx198.helloserver3.service.UserService;
import com.yx198.helloserver3.vo.UserDetailVO;
import com.yx198.helloserver3.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final String CACHE_KEY_PREFIX = "user:detail:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

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
        // 1. 根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 校验密码是否正确
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 4. 生成 JWT 令牌
        String jwt = jwtUtil.generateToken(userDTO.getUsername());
        System.out.println("登录成功，生成 JWT: " + jwt);

        // 5. 返回 JWT
        return Result.success(jwt);
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

    // 分页查询
    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        List<User> allUsers = userMapper.selectList(null);
        long total = allUsers.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allUsers.size());

        List<User> records = allUsers.subList(start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("current", pageNum);
        result.put("size", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);

        return Result.success(result);
    }

    // 查询用户详情（多表联查 + Redis 缓存）
    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;

        // 1. 先查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isEmpty()) {
            try {
                UserDetailVO cacheVO = JSONUtil.toBean(json, UserDetailVO.class);
                System.out.println("从 Redis 缓存读取数据，userId: " + userId);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据异常，删除脏缓存
                redisTemplate.delete(key);
            }
        }

        // 2. 查数据库（多表联查）
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 写缓存（10分钟过期）
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(detail), 10, TimeUnit.MINUTES);
        System.out.println("从数据库查询并写入 Redis 缓存，userId: " + userId);

        return Result.success(detail);
    }

    // 更新用户扩展信息
    @Override
    @Transactional
    public Result<String> updateUserInfo(Long userId, UserDetailVO userDetailVO) {
        // 先检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 更新或插入 user_info
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userId);
        UserInfo existing = userInfoMapper.selectOne(queryWrapper);

        if (existing != null) {
            // 更新
            existing.setRealName(userDetailVO.getRealName());
            existing.setPhone(userDetailVO.getPhone());
            existing.setAddress(userDetailVO.getAddress());
            userInfoMapper.updateById(existing);
        } else {
            // 插入
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setRealName(userDetailVO.getRealName());
            userInfo.setPhone(userDetailVO.getPhone());
            userInfo.setAddress(userDetailVO.getAddress());
            userInfoMapper.insert(userInfo);
        }

        // 删除 Redis 缓存
        String key = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        System.out.println("更新数据，删除 Redis 缓存，userId: " + userId);

        return Result.success("更新成功");
    }

    // 删除用户
    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 删除用户
        userMapper.deleteById(userId);

        // 删除扩展信息
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userId);
        userInfoMapper.delete(queryWrapper);

        // 删除 Redis 缓存
        String key = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        System.out.println("删除用户，删除 Redis 缓存，userId: " + userId);

        return Result.success("删除成功");
    }
}