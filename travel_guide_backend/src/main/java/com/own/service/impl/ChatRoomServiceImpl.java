package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.convert.ConvertUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.common.vo.UserContext;
import com.own.mappers.ChatRoomMapper;
import com.own.model.*;
import com.own.service.ChatRecordService;
import com.own.service.ChatRoomService;
import com.own.service.HotelInfoService;
import com.own.service.HotelOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天室表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoom> implements ChatRoomService {

    @Override
    public String getRoomId(String hotelId) {
        UserContext currentUser = ContextUtil.getCurrentUser();
        ChatRoom chatRoom = this.lambdaQuery()
                .eq(ChatRoom::getHotelId, hotelId)
                .eq(ChatRoom::getUserId, currentUser.getUserId())
                .one();
        if (ObjectUtil.isEmpty(chatRoom)) {
            chatRoom = new ChatRoom();
            chatRoom.setHotelId(hotelId)
                    .setUserId(currentUser.getUserId())
                    .setRoomState("20");
            this.save(chatRoom);
        }

        return chatRoom.getId();
    }

    @Override
    public String getRoomByOrderId(String orderId) {
        HotelOrder hotelOrder = SpringUtil.getBean(HotelOrderService.class)
                .getById(orderId);

        if(ObjectUtil.isEmpty(hotelOrder)){
            throw new BaseException("订单不存在");
        }

        ChatRoom chatRoom = this.lambdaQuery()
                .eq(ChatRoom::getHotelId, hotelOrder.getHotelId())
                .eq(ChatRoom::getUserId, hotelOrder.getUserId())
                .one();
        if (ObjectUtil.isEmpty(chatRoom)) {
            chatRoom = new ChatRoom();
            chatRoom.setHotelId(hotelOrder.getHotelId())
                    .setUserId(hotelOrder.getUserId())
                    .setRoomState("20");
            this.save(chatRoom);
        }

        return chatRoom.getId();
    }

    @Override
    public ChatRoom getByIdPlus(String roomId) {
        ChatRoom chatRoom = this.getById(roomId);

        if(ObjectUtil.isNotEmpty(chatRoom)){
            this.convert(chatRoom);
        }
        return chatRoom;
    }

    @Override
    public List<ChatRoom> myRoom() {
        // 查询我是否已有正在聊天的聊天室
        List<ChatRoom> chatRoomList = this.lambdaQuery()
                .eq(ChatRoom::getUserId, ContextUtil.getCurrentUserId())
                .eq(ChatRoom::getRoomState, 10)
                .orderByDesc(ChatRoom::getRecentTime)
                .list();

        if(CollUtil.isEmpty(chatRoomList)){
            return chatRoomList;
        }
        this.convert(chatRoomList);

        // 查询每个聊天室未读消息和最近的消息记录
        List<String> roomIds = chatRoomList.stream().map(ChatRoom::getId).toList();
        ChatRecordService recordService = SpringUtil.getBean(ChatRecordService.class);

        List<ChatRecord> recordList = recordService.lambdaQuery()
                .in(ChatRecord::getRoomId, roomIds)
                .ne(ChatRecord::getUserId, ContextUtil.getCurrentUserId())
                .eq(ChatRecord::getIsRead, 0)
                .list();

        Map<String, List<ChatRecord>> recordMap = recordList.stream().collect(Collectors.groupingBy(ChatRecord::getRoomId));

        chatRoomList.forEach(chatRoom -> {
            List<ChatRecord> chatRecordList = recordMap.get(chatRoom.getId());
            ChatRecord record = recordService.lambdaQuery()
                    .eq(ChatRecord::getRoomId, chatRoom.getId())
                    .orderByDesc(ChatRecord::getCreateTime)
                    .last("limit 1")
                    .one();
            if(ObjectUtil.isNotEmpty(record)){
                chatRoom.setRecentMsgType(record.getMessageType())
                        .setRecentMsgInfo(record.getMessageInfo());
            }

            if (CollUtil.isEmpty(chatRecordList)) {
                chatRoom.setMsgCount(0);
            } else {
                chatRoom.setMsgCount(chatRecordList.size());
            }
        });

        return chatRoomList;
    }

    @Override
    public List<ChatRoom> myRoomManage() {
        // 查询我负责的酒店
        List<HotelInfo> hotelInfoList = SpringUtil.getBean(HotelInfoService.class)
                .lambdaQuery()
                .eq(HotelInfo::getCreator, ContextUtil.getCurrentUserId())
                .list();
        if (CollUtil.isEmpty(hotelInfoList)) {
            return Collections.emptyList();
        }

        List<String> hotelIds = hotelInfoList.stream().map(HotelInfo::getId).toList();

        // 查询我是否已有正在聊天的聊天室
        List<ChatRoom> chatRoomList = this.lambdaQuery()
                .eq(ChatRoom::getRoomState, 10)
                .in(ChatRoom::getHotelId, hotelIds)
                .orderByDesc(ChatRoom::getRecentTime)
                .list();

        if(CollUtil.isEmpty(chatRoomList)){
            return chatRoomList;
        }
        this.convert(chatRoomList);

        // 查询每个聊天室未读消息和最近的消息记录
        List<String> roomIds = chatRoomList.stream().map(ChatRoom::getId).toList();
        ChatRecordService recordService = SpringUtil.getBean(ChatRecordService.class);

        List<ChatRecord> recordList = recordService.lambdaQuery()
                .in(ChatRecord::getRoomId, roomIds)
                .ne(ChatRecord::getUserId, ContextUtil.getCurrentUserId())
                .eq(ChatRecord::getIsRead, 0)
                .list();

        Map<String, List<ChatRecord>> recordMap = recordList.stream().collect(Collectors.groupingBy(ChatRecord::getRoomId));

        chatRoomList.forEach(chatRoom -> {
            List<ChatRecord> chatRecordList = recordMap.get(chatRoom.getId());
            ChatRecord record = recordService.lambdaQuery()
                    .eq(ChatRecord::getRoomId, chatRoom.getId())
                    .orderByDesc(ChatRecord::getCreateTime)
                    .last("limit 1")
                    .one();
            if(ObjectUtil.isNotEmpty(record)){
                chatRoom.setRecentMsgType(record.getMessageType())
                        .setRecentMsgInfo(record.getMessageInfo());
            }

            if (CollUtil.isEmpty(chatRecordList)) {
                chatRoom.setMsgCount(0);
            } else {
                chatRoom.setMsgCount(chatRecordList.size());
            }
        });

        return chatRoomList;
    }

    @Override
    public Integer myWaitCount() {
        // 查询我的聊天室
        List<ChatRoom> roomList = this.lambdaQuery()
                .eq(ChatRoom::getUserId, ContextUtil.getCurrentUserId())
                .eq(ChatRoom::getRoomState, "10")
                .list();
        if(CollUtil.isEmpty(roomList)){
            return 0;
        }

        List<String> roomIds = roomList.stream().map(ChatRoom::getId).toList();

        List<ChatRecord> recordList = SpringUtil.getBean(ChatRecordService.class)
                .lambdaQuery()
                .in(ChatRecord::getRoomId, roomIds)
                .ne(ChatRecord::getUserId, ContextUtil.getCurrentUserId())
                .eq(ChatRecord::getIsRead, 0)
                .list();
        return recordList.size();
    }

    @Override
    public Integer manageWaitCount() {
        // 查询我负责的酒店
        List<HotelInfo> hotelInfoList = SpringUtil.getBean(HotelInfoService.class)
                .lambdaQuery()
                .eq(HotelInfo::getCreator, ContextUtil.getCurrentUserId())
                .list();
        if (CollUtil.isEmpty(hotelInfoList)) {
            return 0;
        }

        List<String> hotelIds = hotelInfoList.stream().map(HotelInfo::getId).toList();

        // 查询我是否已有正在聊天的聊天室
        List<ChatRoom> roomList = this.lambdaQuery()
                .eq(ChatRoom::getRoomState, 10)
                .in(ChatRoom::getHotelId, hotelIds)
                .list();

        if(CollUtil.isEmpty(roomList)){
            return 0;
        }

        List<String> roomIds = roomList.stream().map(ChatRoom::getId).toList();

        List<ChatRecord> recordList = SpringUtil.getBean(ChatRecordService.class)
                .lambdaQuery()
                .in(ChatRecord::getRoomId, roomIds)
                .ne(ChatRecord::getUserId, ContextUtil.getCurrentUserId())
                .eq(ChatRecord::getIsRead, 0)
                .list();
        return recordList.size();
    }

    private void convert(List<ChatRoom> list) {
        // 字段转换
        if(CollUtil.isNotEmpty(list)){
            ConvertUtil.of(list,ChatRoom.class)
                    .lambdaAdd(ChatRoom::getUserId, User::getId,ChatRoom::getUserName,User::getUserName,User.class)
                    .lambdaAdd(ChatRoom::getUserId, User::getId,ChatRoom::getUserHead,User::getHeadPicUrl,User.class)
                    .lambdaAdd(ChatRoom::getHotelId, HotelInfo::getId,ChatRoom::getHotelName,HotelInfo::getHtoelName,HotelInfo.class)
                    .lambdaAdd(ChatRoom::getHotelId, HotelInfo::getId,ChatRoom::getHotelPic,HotelInfo::getHtoelPic,HotelInfo.class)
                    .done()
                    .convert();
        }
    }

    private void convert(ChatRoom entity) {
        List<ChatRoom> list = new ArrayList<>();
        list.add(entity);

        this.convert(list);
    }
}
