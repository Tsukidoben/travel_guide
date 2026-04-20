package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 景点信息表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("attraction_info")
public class AttractionInfo  extends Model<AttractionInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 景点名称
     */
    @TableField(value = "attraction_name")
    private String attractionName;

    /**
     * 景点图片
     */
    @TableField(value = "attraction_pic")
    private String attractionPic;

    /**
     * 景点分类
     */
    @TableField(value = "type_id")
    private String typeId;

    /**
     * 景点简介
     */
    @TableField(value = "attraction_desc")
    private String attractionDesc;

    /**
     * 景点描述
     */
    @TableField(value = "attraction_detail")
    private String attractionDetail;

    /**
     * 景点位置
     */
    @TableField(value = "attraction_place")
    private String attractionPlace;

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

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
    * 关键字查询
    */
    @TableField(exist = false)
    private String keyword;

    @TableField(exist = false)
    private Integer isCollect;

    @TableField(exist = false)
    private List<TicketInfo>  ticketInfoList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
