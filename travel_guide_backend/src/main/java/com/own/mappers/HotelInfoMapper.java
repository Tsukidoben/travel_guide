package com.own.mappers;

import com.own.model.HotelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒店管理表
 * Mapper（Dao）持久层
 */
@Mapper
public interface HotelInfoMapper extends BaseMapper<HotelInfo> {

}
