package com.own.mappers;

import com.own.model.HotelOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒店订单表
 * Mapper（Dao）持久层
 */
@Mapper
public interface HotelOrderMapper extends BaseMapper<HotelOrder> {

}
