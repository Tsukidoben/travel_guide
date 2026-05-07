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
 * 路线推荐记录表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("route_recommendation")
public class RouteRecommendation extends Model<RouteRecommendation> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 推荐方案名称
     */
    @TableField(value = "recommendation_name")
    private String recommendationName;

    /**
     * 旅行天数
     */
    @TableField(value = "days")
    private Integer days;

    /**
     * 总预算（元）
     */
    @TableField(value = "budget")
    private BigDecimal budget;

    /**
     * 起点经度
     */
    @TableField(value = "start_longitude")
    private java.math.BigDecimal startLongitude;

    /**
     * 起点纬度
     */
    @TableField(value = "start_latitude")
    private java.math.BigDecimal startLatitude;

    /**
     * 起点地址
     */
    @TableField(value = "start_address")
    private String startAddress;

    /**
     * 偏好景点分类ID列表（逗号分隔）
     */
    @TableField(value = "preference_types")
    private String preferenceTypes;

    /**
     * 推荐景点数量
     */
    @TableField(value = "total_attractions")
    private Integer totalAttractions;

    /**
     * 推荐酒店数量
     */
    @TableField(value = "total_hotels")
    private Integer totalHotels;

    /**
     * 预估总费用（元）
     */
    @TableField(value = "estimated_cost")
    private BigDecimal estimatedCost;

    /**
     * 总路程（千米）
     */
    @TableField(value = "total_distance")
    private BigDecimal totalDistance;

    /**
     * 总游玩时长（分钟）
     */
    @TableField(value = "total_duration")
    private Integer totalDuration;

    /**
     * 状态（1有效 0已删除）
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

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
