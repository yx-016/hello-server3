package com.yx198.helloserver3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yx198.helloserver3.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 即可获得基本的 CRUD 方法
}