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
 * 小吃店铺表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("food_shop")
public class FoodShop extends Model<FoodShop> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 店铺名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 店铺地址
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
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 营业时间
     */
    @TableField(value = "business_hours")
    private String businessHours;

    /**
     * 营业状态（营业中/休息中）
     */
    @TableField(value = "business_status")
    private String businessStatus;

    /**
     * 人均消费
     */
    @TableField(value = "avg_price")
    private BigDecimal avgPrice;

    /**
     * 店铺图片
     */
    @TableField(value = "images")
    private String images;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

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
