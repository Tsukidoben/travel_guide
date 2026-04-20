package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 景点评论表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("attraction_comment")
public class AttractionComment  extends Model<AttractionComment> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 景点id
     */
    @TableField(value = "attraction_id")
    private String attractionId;
    /**
     * 评论详情
     */
    @TableField(value = "comment_detail")
    private String commentDetail;
    /**
     * 评论图片
     */
    @TableField(value = "picture_url")
    private String pictureUrl;
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
    * 关键字查询
    */
    @TableField(exist = false)
    private String keyword;

    /**
     * 评论人头像
     */
    @TableField(exist = false)
    private String creatorHead;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
