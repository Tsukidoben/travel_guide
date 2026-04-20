package com.own.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.convert.ConvertUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.mappers.ChatRecordMapper;
import com.own.model.ChatRecord;
import com.own.model.ChatRoom;
import com.own.model.User;
import com.own.model.vo.DelVo;
import com.own.service.ChatRecordService;
import com.own.service.ChatRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 聊天记录表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {

    /**
     * 用于存储哪些聊天室已经回复了
     */
    private static Map<String,Boolean> respMap = new HashMap<>();

    @Override
    public void chat(ChatRecord entity) {
        if (ObjectUtil.isEmpty(entity)) {
            throw new BaseException("参数不全");
        }

        if (ObjectUtil.isEmpty(entity.getRoomId())) {
            throw new BaseException("聊天室id不能为空");
        }

        if (ObjectUtil.isEmpty(entity.getMessageType())) {
            throw new BaseException("消息类型不能为空");
        }
        if (ObjectUtil.isEmpty(entity.getMessageInfo())) {
            throw new BaseException("消息内容不能为空");
        }
        entity.setIsRead(0)
                .setUserId(ContextUtil.getCurrentUserId());

        this.save(entity);

        ChatRoomService chatRoomService = SpringUtil.getBean(ChatRoomService.class);
        ChatRoom chatRoom = chatRoomService.getById(entity.getRoomId());

        // 修改聊天室状态
        chatRoom.setRoomState("10")
                .setRecentTime(new Date());

        chatRoomService.updateById(chatRoom);
    }

    @Override
    public void delById(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要删除的数据");
        }

        this.removeById(id);
    }

    @Override
    public void delByBatch(DelVo delVo) {
        if(ObjectUtil.isEmpty(delVo.getIds())){
            throw new BaseException("请选择要删除的数据");
        }

        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public List<ChatRecord> getMessageByRoomId(String roomId) {
        List<ChatRecord> list = this.lambdaQuery()
                .eq(ChatRecord::getRoomId, roomId)
                .orderByAsc(ChatRecord::getCreateTime)
                .list();

        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        this.convert(list);

        return list;
    }

    @Override
    public List<ChatRecord> getMessageByTime(ChatRecord chatRecord) {
        List<ChatRecord> list = this.lambdaQuery()
                .eq(ChatRecord::getRoomId, chatRecord.getRoomId())
                .gt(ChatRecord::getCreateTime, chatRecord.getCreateTime())
                .orderByAsc(ChatRecord::getCreateTime)
                .list();

        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        this.convert(list);

        return list;
    }

    private void convert(List<ChatRecord> list) {
        // 字段转换
        ConvertUtil.of(list,ChatRecord.class)
                .lambdaAdd(ChatRecord::getUserId, User::getId,ChatRecord::getUserName,User::getUserName,User.class)
                .lambdaAdd(ChatRecord::getUserId, User::getId,ChatRecord::getHeadPicUrl,User::getHeadPicUrl,User.class)
                .done()
                .convert();

        List<ChatRecord> chatRecordList = list.stream()
                .filter(item -> !item.getUserId().equals(ContextUtil.getCurrentUserId()))
                .peek(item -> item.setIsRead(1))
                .toList();

        this.updateBatchById(chatRecordList);
    }

    private void convert(ChatRecord entity) {
        List<ChatRecord> list = new ArrayList<>();
        list.add(entity);

        this.convert(list);
    }
}
