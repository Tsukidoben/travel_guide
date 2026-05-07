package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.RouteGenerateCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 路线生成每日计数 Mapper
 */
@Mapper
public interface RouteGenerateCountMapper extends BaseMapper<RouteGenerateCount> {
}
