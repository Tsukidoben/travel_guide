package com.own.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路线推荐响应VO
 */
@Data
public class RouteRecommendationResponse {

    /**
     * 推荐记录ID
     */
    private String recommendationId;

    /**
     * 推荐方案名称
     */
    private String recommendationName;

    /**
     * 摘要信息
     */
    private RecommendationSummary summary;

    /**
     * 行程列表
     */
    private List<ItineraryDay> itinerary;

    /**
     * 推荐摘要
     */
    @Data
    public static class RecommendationSummary {
        private Integer days;
        private BigDecimal budget;
        private BigDecimal estimatedCost;
        private Integer totalAttractions;
        private Integer totalHotels;
        private BigDecimal totalDistance;
        private Integer totalDuration;
    }
}
