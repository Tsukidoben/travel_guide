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
 * 小吃信息表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("food_info")
public class FoodInfo extends Model<FoodInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 小吃名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 分类ID
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 小吃图片
     */
    @TableField(value = "images")
    private String images;

    /**
     * 综合评分
     */
    @TableField(value = "score")
    private BigDecimal score;

    /**
     * 人均价格
     */
    @TableField(value = "avg_price")
    private BigDecimal avgPrice;

    /**
     * 小吃介绍
     */
    @TableField(value = "description")
    private String description;

    /**
     * 推荐理由
     */
    @TableField(value = "recommend_reason")
    private String recommendReason;

    /**
     * 是否推荐 1是 0否
     */
    @TableField(value = "is_recommend")
    private Integer isRecommend;

    /**
     * 1上架 0下架
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

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 店铺名称（非数据库字段）
     */
    @TableField(exist = false)
    private String shopName;

    /**
     * 店铺信息（非数据库字段）
     */
    @TableField(exist = false)
    private FoodShop foodShop;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
