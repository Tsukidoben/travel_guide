package com.own.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路线推荐请求VO
 */
@Data
public class RouteRecommendationRequest {

    /**
     * 旅行天数（必填）
     */
    private Integer days;

    /**
     * 总预算（可选）
     */
    private BigDecimal budget;

    /**
     * 起点经度（可选，与address二选一）
     */
    private BigDecimal longitude;

    /**
     * 起点纬度（可选，与address二选一）
     */
    private BigDecimal latitude;

    /**
     * 起点地址（可选，与longitude/latitude二选一）
     */
    private String address;

    /**
     * 偏好景点分类ID列表（可选）
     */
    private List<String> preferenceTypes;

    /**
     * 行程名称（可选，不填则自动生成）
     */
    private String recommendationName;

    /**
     * 交通偏好：drive/bus/taxi（可选，默认drive）
     */
    private String transportPreference;
}
