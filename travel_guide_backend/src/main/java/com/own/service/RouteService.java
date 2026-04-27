package com.own.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 路线服务接口
 */
public interface RouteService {

    /**
     * 获取当前景点推荐下一站景点
     *
     * @param attractionId 当前景点ID
     * @return 推荐的景点列表（最多3个）
     */
    List<Map<String, Object>> getNextAttraction(String attractionId);

    /**
     * 获取两景点交通方案
     *
     * @param fromAttractionId 起点景点ID
     * @param toAttractionId   终点景点ID
     * @return 交通方案信息
     */
    Map<String, Object> getTrafficInfo(String fromAttractionId, String toAttractionId);

    /**
     * 路线沿途小吃推荐
     *
     * @param fromAttractionId 起点景点ID
     * @param toAttractionId   终点景点ID
     * @return 沿途小吃店列表（最多4个）
     */
    List<Map<String, Object>> getNearFoodShop(String fromAttractionId, String toAttractionId);
}
