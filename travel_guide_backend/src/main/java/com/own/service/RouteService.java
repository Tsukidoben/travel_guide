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

    /**
     * 获取两景点交通方案（支持按交通方式筛选）
     *
     * @param fromAttractionId 起点景点ID
     * @param toAttractionId   终点景点ID
     * @param transportType    交通方式（drive/bus/taxi/all）
     * @return 交通方案信息
     */
    Map<String, Object> getTrafficInfoByType(String fromAttractionId, String toAttractionId, String transportType);

    /**
     * 获取地图路线坐标数据
     *
     * @param fromAttractionId 起点景点ID
     * @param toAttractionId   终点景点ID
     * @param transportType    交通方式（drive/bus/taxi）
     * @return 路线坐标数据和节点标记
     */
    Map<String, Object> getMapRouteData(String fromAttractionId, String toAttractionId, String transportType);

    /**
     * 获取沿途小吃（支持分页、排序、筛选）
     *
     * @param fromAttractionId 起点景点ID
     * @param toAttractionId   终点景点ID
     * @param pageNum          页码
     * @param pageSize         每页数量
     * @param sortBy           排序字段（convenienceIndex/avgPrice/businessStatus）
     * @param sortOrder        排序方式（asc/desc）
     * @param businessStatus   营业状态筛选（营业中/休息中/null表示不过滤）
     * @return 分页后的小吃列表
     */
    Map<String, Object> getNearFoodShopWithPage(String fromAttractionId, String toAttractionId, 
                                                 Integer pageNum, Integer pageSize,
                                                 String sortBy, String sortOrder,
                                                 String businessStatus);

    /**
     * 获取路线周边小吃标记数据（一次性返回所有小吃坐标和基础信息）
     *
     * @param fromAttractionId 起点景点ID
     * @param toAttractionId   终点景点ID
     * @return 小吃标记数据列表
     */
    List<Map<String, Object>> getFoodMarkers(String fromAttractionId, String toAttractionId);
}
