package com.own.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.ChatRecord;
import com.own.model.vo.DelVo;

import java.util.List;

/**
 * 聊天记录表
 * 业务层
 */
public interface ChatRecordService extends IService<ChatRecord> {

    void chat(ChatRecord entity);

    void delById(String id);

    void delByBatch(DelVo delVo);

    List<ChatRecord> getMessageByRoomId(String roomId);

    List<ChatRecord> getMessageByTime(ChatRecord chatRecord);
}
