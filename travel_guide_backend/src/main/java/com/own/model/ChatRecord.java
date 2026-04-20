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
 * 聊天记录表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("chat_record")
public class ChatRecord  extends Model<ChatRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 聊天室id
     */
    @TableField(value = "room_id")
    private String roomId;
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

    @TableField(exist = false)
    private String headPicUrl;
    /**
     * 消息类型（1 文字 2 图片 3 视频）
     */
    @TableField(value = "message_type")
    private Integer messageType;
    /**
     * 消息内容
     */
    @TableField(value = "message_info")
    private String messageInfo;
    /**
     * 发送时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 是否已读，1是 0否
     */
    @TableField(value = "is_read")
    private Integer isRead;

    /**
    * 关键字查询
    */
    @TableField(exist = false)
    private String keyword;

    @TableField(exist = false)
    private String teacherId;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
