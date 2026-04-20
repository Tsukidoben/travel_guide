package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 攻略评论表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("trip_strategy_comment")
public class TripStrategyComment  extends Model<TripStrategyComment> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 攻略id
     */
    @TableField(value = "trip_strategy_id")
    private String tripStrategyId;
    /**
     * 评论详情
     */
    @TableField(value = "comment_detail")
    private String commentDetail;
    /**
     * 主评论id（所属的直接回复帖子的评论id，如果自身就是，则就是帖子id）
     */
    @TableField(value = "main_comment_id")
    private String mainCommentId;
    /**
     * 是否是主评论0否1是
     */
    @TableField(value = "main_comment")
    private Integer mainComment;
    /**
     * 回复的评论id，如果是主评论，则为null
     */
    @TableField(value = "replay_comment_id")
    private String replayCommentId;
    /**
     * 回复给的人的id
     */
    @TableField(value = "reply_id")
    private String replyId;
    /**
     * 回复给的人的名称
     */
    @TableField(value = "reply_name")
    private String replyName;
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
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 二级评论
     */
    @TableField(exist = false)
    List<TripStrategyComment> children;


    @TableField(exist = false)
    private String creatorHead;

    @TableField(exist = false)
    private String replyHead;
    /**
    * 关键字查询
    */
    @TableField(exist = false)
    private String keyword;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
