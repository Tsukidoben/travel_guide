import request from "@/utils/request";
import config from "@/config/config";

/**
 * 获取下一个推荐景点
 * @param {number} attractionId - 当前景点ID
 */
export function getNextAttraction(attractionId) {
    return request({
        url: config.backHost + "/api/route/getNextAttraction",
        method: "POST",
        data: {
            attractionId: attractionId
        }
    });
}

/**
 * 获取交通信息（支持按交通方式筛选）
 * @param {object} params - 请求参数 {fromAttractionId, toAttractionId, transportType}
 */
export function getTrafficInfo(params) {
    return request({
        url: config.backHost + "/api/route/getTrafficInfoByType",
        method: "POST",
        data: params
    });
}

/**
 * 获取沿途特色小吃（基础版）
 * @param {object} params - 请求参数
 */
export function getNearFoodShop(params) {
    return request({
        url: config.backHost + "/api/route/getNearFoodShop",
        method: "POST",
        data: params
    });
}

/**
 * 获取沿途小吃（分页+排序+筛选）
 * @param {object} params - 请求参数 {fromAttractionId, toAttractionId, pageNum, pageSize, sortBy, sortOrder, businessStatus}
 */
export function getNearFoodShopWithPage(params) {
    return request({
        url: config.backHost + "/api/route/getNearFoodShopWithPage",
        method: "POST",
        data: params
    });
}

/**
 * 计算预订总费用
 * @param {object} params - 请求参数 {fromAttractionId, toAttractionId, personCount, transportType}
 */
export function calculateTotalCost(params) {
    return request({
        url: config.backHost + "/api/route/calculateTotalCost",
        method: "POST",
        data: params
    });
}

/**
 * 获取路线周边小吃标记数据（用于地图）
 * @param {object} params - 请求参数 {fromAttractionId, toAttractionId}
 */
export function getFoodMarkers(params) {
    return request({
        url: config.backHost + "/api/route/getFoodMarkers",
        method: "POST",
        data: params
    });
}

/**
 * 获取地图路线坐标数据
 * @param {object} params - 请求参数 {fromAttractionId, toAttractionId, transportType}
 */
export function getMapRouteData(params) {
    return request({
        url: config.backHost + "/api/route/getMapRouteData",
        method: "POST",
        data: params
    });
}
