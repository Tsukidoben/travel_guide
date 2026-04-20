package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天室表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("chat_room")
public class ChatRoom  extends Model<ChatRoom> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 用户名
     */
    @TableField(exist = false)
    private String userName;
    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String userHead;
    /**
     * 酒店id
     */
    @TableField(value = "hotel_id")
    private String hotelId;
    /**
     * 酒店图片
     */
    @TableField(exist = false)
    private String hotelPic;
    /**
     * 酒店名称
     */
    @TableField(exist = false)
    private String hotelName;
    /**
     * 状态10正常 20 未激活
     */
    @TableField(value = "room_state")
    private String roomState;
    /**
     * 第一条消息的发起时间
     */
    @TableField(value = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField(value = "recent_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recentTime;

    @TableField(exist = false)
    private Integer recentMsgType;

    @TableField(exist = false)
    private String recentMsgInfo;
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 创建人id
     */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;
    /**
     * 创建人姓名
     */
    @TableField(value = "create_name", fill = FieldFill.INSERT)
    private String createName;

    /**
     * 关键字查询
     */
    @TableField(exist = false)
    private String keyword;

    @TableField(exist = false)
    private Integer msgCount;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
