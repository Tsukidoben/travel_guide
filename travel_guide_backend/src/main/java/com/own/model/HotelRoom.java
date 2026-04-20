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
 * 房间管理表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("hotel_room")
public class HotelRoom  extends Model<HotelRoom> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 所属酒店
     */
    @TableField(value = "hotel_id")
    private String hotelId;

    /**
     * 房间图片
     */
    @TableField(value = "room_pic")
    private String roomPic;

    /**
     * 房间名称
     */
    @TableField(value = "room_name")
    private String roomName;

    /**
     * 房间介绍
     */
    @TableField(value = "room_desc")
    private String roomDesc;

    /**
     * 房间价格
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 房间数量
     */
    @TableField(value = "room_count")
    private Integer roomCount;

    /**
     * 可住人数
     */
    @TableField(value = "people_count")
    private Integer peopleCount;

    /**
     * 房间楼层
     */
    @TableField(value = "room_floor")
    private Integer roomFloor;


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
