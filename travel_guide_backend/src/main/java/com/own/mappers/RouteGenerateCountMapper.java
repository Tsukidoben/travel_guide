package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.RouteGenerateCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 路线生成每日计数 Mapper
 */
@Mapper
public interface RouteGenerateCountMapper extends BaseMapper<RouteGenerateCount> {
    
    /**
     * 查询用户今日生成次数
     *
     * @param userId 用户ID
     * @param today 日期
     * @return 生成次数，如果不存在返回0
     */
    @Select("SELECT COALESCE(count, 0) FROM route_generate_count WHERE user_id = #{userId} AND generate_date = #{today}")
    Integer getDailyCount(@Param("userId") String userId, @Param("today") LocalDate today);
}
