package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.ChatRoom;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天室表
 * Mapper（Dao）持久层
 */
@Mapper
public interface ChatRoomMapper extends BaseMapper<ChatRoom> {

}
