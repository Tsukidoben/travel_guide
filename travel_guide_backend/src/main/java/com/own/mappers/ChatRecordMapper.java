package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.ChatRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天记录表
 * Mapper（Dao）持久层
 */
@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

}
