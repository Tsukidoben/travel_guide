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
 * 路线推荐详情表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "route_recommendation_detail", autoResultMap = true)
public class RouteRecommendationDetail extends Model<RouteRecommendationDetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 推荐记录ID
     */
    @TableField(value = "recommendation_id")
    private String recommendationId;

    /**
     * 第几天
     */
    @TableField(value = "day_num")
    private Integer dayNum;

    /**
     * 排序序号
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 项目类型（attraction/food/hotel/route）
     */
    @TableField(value = "item_type")
    private String itemType;

    /**
     * 统一名称(景点名/店铺名/路线起点-终点)
     */
    @TableField(value = "name")
    private String name;

    /**
     * 耗时/游玩时长(分钟)
     */
    @TableField(value = "duration")
    private Integer duration;

    /**
     * 费用(门票/餐费/交通费)
     */
    @TableField(value = "cost")
    private BigDecimal cost;

    /**
     * 经度(点位中心或路线起点)
     */
    @TableField(value = "longitude")
    private java.math.BigDecimal longitude;

    /**
     * 纬度(点位中心或路线起点)
     */
    @TableField(value = "latitude")
    private java.math.BigDecimal latitude;

    /**
     * 扩展信息(JSON格式，存储各类特有字段)
     */
    @TableField(value = "extra_info", typeHandler = com.own.handler.FastJsonTypeHandler.class)
    private com.alibaba.fastjson.JSONObject extraInfo;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
