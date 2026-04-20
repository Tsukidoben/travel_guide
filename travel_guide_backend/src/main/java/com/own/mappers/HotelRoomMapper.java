package com.own.mappers;

import com.own.model.HotelRoom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房间管理表
 * Mapper（Dao）持久层
 */
@Mapper
public interface HotelRoomMapper extends BaseMapper<HotelRoom> {

}
