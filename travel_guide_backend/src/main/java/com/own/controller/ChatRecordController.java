package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import com.own.model.ChatRecord;
import com.own.model.vo.DelVo;
import com.own.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天记录表
 * 控制层
 */
@RestController
@RequestMapping("/chatRecord")
public class ChatRecordController {

    @Autowired
    private ChatRecordService baseService;

    /**
     * 聊天
     */
    @PostMapping("chat")
    public String chat(@RequestBody ChatRecord entity) {
        baseService.chat(entity);
        return ResultUtil.success("操作成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        baseService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        baseService.delByBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 根据聊天室id查询对话信息
     */
    @PostMapping("getMessageByRoomId/{roomId}")
    public String getMessageByRoomId(@PathVariable("roomId") String roomId) {
        return ResultUtil.<List<ChatRecord>>successWithData(baseService.getMessageByRoomId(roomId));
    }

    /**
     * 根据时间查询聊天记录
     */
    @PostMapping("getMessageByTime")
    public String getMessageByTime(@RequestBody ChatRecord chatRecord) {
        return ResultUtil.<List<ChatRecord>>successWithData(baseService.getMessageByTime(chatRecord));
    }


}
