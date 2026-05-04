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
 * 酒店管理表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("hotel_info")
public class HotelInfo  extends Model<HotelInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 酒店图片
     */
    @TableField(value = "htoel_pic")
    private String htoelPic;

    /**
     * 酒店名称
     */
    @TableField(value = "htoel_name")
    private String htoelName;

    /**
     * 酒店介绍
     */
    @TableField(value = "hotel_desc")
    private String hotelDesc;

    /**
     * 酒店描述
     */
    @TableField(value = "hotel_detail")
    private String hotelDetail;

    /**
     * 酒店服务
     */
    @TableField(value = "hotel_service")
    private String hotelService;

    /**
     * 酒店地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 经度
     */
    @TableField(value = "longitude")
    private java.math.BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField(value = "latitude")
    private java.math.BigDecimal latitude;

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
    private List<HotelRoom> roomList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
