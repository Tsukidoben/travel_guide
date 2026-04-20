package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import com.own.model.ChatRoom;
import com.own.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 聊天室表
 * 控制层
 */
@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController {

    @Autowired
    private ChatRoomService baseService;

    /**
     * 通过酒店id获取聊天室信息，用户端使用，有则返回，没有则创建，状态等待激活
     */
    @PostMapping("getRoomByHotelId/{hotelId}")
    public String getRoomByHotelId(@PathVariable("hotelId") String hotelId) {
        return ResultUtil.<String>successWithData(baseService.getRoomId(hotelId));
    }

    /**
     * 通过酒店订单id获取聊天室信息，用户端使用，有则返回，没有则创建，状态等待激活
     */
    @PostMapping("getRoomByOrderId/{orderId}")
    public String getRoomByOrderId(@PathVariable("orderId") String orderId) {
        return ResultUtil.<String>successWithData(baseService.getRoomByOrderId(orderId));
    }

    /**
     * 根据id查询聊天室信息
     */
    @PostMapping("getById/{roomId}")
    public String getById(@PathVariable("roomId")String roomId) {
        return ResultUtil.<ChatRoom>successWithData(baseService.getByIdPlus(roomId));
    }

    /**
     * 查询我的聊天室记录，包含最后一条消息的时间，未读消息条数
     */
    @PostMapping("myRoom")
    public String myRoom() {
        return ResultUtil.<List<ChatRoom>>successWithData(baseService.myRoom());
    }

    /**
     * 查询我的聊天室记录,管理端，包含最后一条消息的时间，未读消息条数
     */
    @PostMapping("myRoomManage")
    public String myRoomManage() {
        return ResultUtil.<List<ChatRoom>>successWithData(baseService.myRoomManage());
    }

    /**
     * 我的待读消息数量
     */
    @PostMapping("myWaitCount")
    public String myWaitCount() {
        return ResultUtil.<Integer>successWithData(baseService.myWaitCount());
    }

    /**
     * 我的待读消息数量,管理端
     */
    @PostMapping("manageWaitCount")
    public String manageWaitCount() {
        return ResultUtil.<Integer>successWithData(baseService.manageWaitCount());
    }
}
