package com.own.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 行程项VO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 序列化时忽略null字段
public class ItineraryItem {

    /**
     * 类型：attraction/food/hotel/route
     */
    private String type;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    // ========== 景点相关字段 ==========
    /**
     * 景点ID
     */
    private String attractionId;

    /**
     * 景点名称
     */
    private String attractionName;

    /**
     * 游玩时长（分钟）
     */
    private Integer playDuration;

    /**
     * 门票价格
     */
    private BigDecimal ticketPrice;

    /**
     * 景点图片
     */
    private String images;

    /**
     * 景点描述
     */
    private String description;

    // ========== 小吃相关字段 ==========
    /**
     * 小吃店铺ID
     */
    private Long foodShopId;

    /**
     * 小吃信息ID
     */
    private Long foodInfoId;

    /**
     * 小吃名称
     */
    private String foodName;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 小吃价格
     */
    private BigDecimal foodPrice;

    /**
     * 顺路指数（1-5星）
     */
    private Integer convenienceIndex;

    // ========== 酒店相关字段 ==========
    /**
     * 酒店ID
     */
    private String hotelId;

    /**
     * 酒店名称
     */
    private String hotelName;

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 房间价格
     */
    private BigDecimal roomPrice;

    // ========== 路线相关字段 ==========
    /**
     * 起点
     */
    private String fromLocation;

    /**
     * 终点
     */
    private String toLocation;

    /**
     * 交通方式
     */
    private String transportType;

    /**
     * 距离（千米）
     */
    private BigDecimal distance;

    /**
     * 耗时（分钟）
     */
    private Integer duration;

    /**
     * 交通费用
     */
    private BigDecimal cost;

    /**
     * 路线坐标串
     */
    private String routePolyline;

    // ========== 路线起点终点坐标 ==========
    /**
     * 起点经度
     */
    private BigDecimal fromLongitude;

    /**
     * 起点纬度
     */
    private BigDecimal fromLatitude;

    /**
     * 终点经度
     */
    private BigDecimal toLongitude;

    /**
     * 终点纬度
     */
    private BigDecimal toLatitude;

    // ========== 位置信息 ==========
    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;
}
