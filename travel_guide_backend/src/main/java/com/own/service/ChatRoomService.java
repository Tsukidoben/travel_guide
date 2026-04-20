package com.own.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.ChatRoom;

import java.util.List;

/**
 * 聊天室表
 * 业务层
 */
public interface ChatRoomService extends IService<ChatRoom> {
    String getRoomId(String hotelId);

    String getRoomByOrderId(String orderId);

    ChatRoom getByIdPlus(String roomId);

    List<ChatRoom> myRoom();

    List<ChatRoom> myRoomManage();

    Integer myWaitCount();

    Integer manageWaitCount();

}
