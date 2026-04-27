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
 * 获取交通信息
 * @param {object} params - 请求参数
 */
export function getTrafficInfo(params) {
    return request({
        url: config.backHost + "/api/route/getTrafficInfo",
        method: "POST",
        data: params
    });
}

/**
 * 获取沿途特色小吃
 * @param {object} params - 请求参数
 */
export function getNearFoodShop(params) {
    return request({
        url: config.backHost + "/api/route/getNearFoodShop",
        method: "POST",
        data: params
    });
}
