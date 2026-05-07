package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.RouteRecommendation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 路线推荐记录表 Mapper
 */
@Mapper
public interface RouteRecommendationMapper extends BaseMapper<RouteRecommendation> {
}
