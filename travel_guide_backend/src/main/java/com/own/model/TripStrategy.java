package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 攻略管理表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("trip_strategy")
public class TripStrategy  extends Model<TripStrategy> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 攻略图片
     */
    @TableField(value = "strategy_pic")
    private String strategyPic;

    /**
     * 攻略内容
     */
    @TableField(value = "strategy_content")
    private String strategyContent;

    /**
     * 攻略状态 10 待审核 80 审核通过 -2 审核不通过
     */
    @TableField(value = "status")
    private String status;

    /**
     * 审核意见
     */
    @TableField(value = "review_reason")
    private String reviewReason;

    /**
     * 审核时间
     */
    @TableField(value = "review_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reviewTime;

    /**
     * 创建人
     */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    /**
     * 创建人
     */
    @TableField(value = "create_name", fill = FieldFill.INSERT)
    private String createName;

    @TableField(exist = false)
    private String createHead;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 景点id
     */
    @TableField(value = "attraction_id")
    private String attractionId;

    /**
    * 关键字查询
    */
    @TableField(exist = false)
    private String keyword;

    /**
     * 景点名称
     */
    @TableField(exist = false)
    private String attractionName;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
