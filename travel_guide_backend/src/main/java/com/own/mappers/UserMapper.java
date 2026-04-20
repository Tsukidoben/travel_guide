package com.own.mappers;

import com.own.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息表
 * Mapper（Dao）持久层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
