package com.yx198.helloserver3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yx198.helloserver3.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}