package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 小吃评论表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("food_comment")
public class FoodComment extends Model<FoodComment> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 小吃ID
     */
    @TableField(value = "food_id")
    private Long foodId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 评分1-5
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 评价内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 评价图片
     */
    @TableField(value = "images")
    private String images;

    /**
     * 0待审核 1通过 2驳回
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户头像（非数据库字段）
     */
    @TableField(exist = false)
    private String userHeadPicUrl;

    /**
     * 小吃名称（非数据库字段）
     */
    @TableField(exist = false)
    private String foodName;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
