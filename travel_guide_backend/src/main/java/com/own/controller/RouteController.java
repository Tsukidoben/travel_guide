package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.service.RouteService;
import cn.y8e.common.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 路线控制器
 */
@RestController
@RequestMapping("/api/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    /**
     * 获取当前景点推荐下一站景点
     *
     * @param params 请求参数，包含attractionId
     * @return 推荐的景点列表
     */
    @PostMapping("/getNextAttraction")
    @IgnoreAuth
    public String getNextAttraction(@RequestBody Map<String, String> params) {
        String attractionId = params.get("attractionId");
        List<Map<String, Object>> result = routeService.getNextAttraction(attractionId);
        return ResultUtil.successWithData(result);
    }

    /**
     * 获取两景点交通方案
     *
     * @param params 请求参数，包含fromAttractionId和toAttractionId
     * @return 交通方案信息
     */
    @PostMapping("/getTrafficInfo")
    @IgnoreAuth
    public String getTrafficInfo(@RequestBody Map<String, String> params) {
        String fromAttractionId = params.get("fromAttractionId");
        String toAttractionId = params.get("toAttractionId");
        Map<String, Object> result = routeService.getTrafficInfo(fromAttractionId, toAttractionId);
        return ResultUtil.successWithData(result);
    }

    /**
     * 路线沿途小吃推荐
     *
     * @param params 请求参数，包含fromAttractionId和toAttractionId
     * @return 沿途小吃店列表
     */
    @PostMapping("/getNearFoodShop")
    @IgnoreAuth
    public String getNearFoodShop(@RequestBody Map<String, String> params) {
        String fromAttractionId = params.get("fromAttractionId");
        String toAttractionId = params.get("toAttractionId");
        
        List<Map<String, Object>> result = routeService.getNearFoodShop(fromAttractionId, toAttractionId);
        return ResultUtil.successWithData(result);
    }

    /**
     * 获取两景点交通方案（支持按交通方式筛选）
     *
     * @param params 请求参数，包含fromAttractionId、toAttractionId、transportType
     * @return 交通方案信息
     */
    @PostMapping("/getTrafficInfoByType")
    @IgnoreAuth
    public String getTrafficInfoByType(@RequestBody Map<String, String> params) {
        String fromAttractionId = params.get("fromAttractionId");
        String toAttractionId = params.get("toAttractionId");
        String transportType = params.get("transportType"); // drive/bus/taxi/all
        
        Map<String, Object> result = routeService.getTrafficInfoByType(fromAttractionId, toAttractionId, transportType);
        return ResultUtil.successWithData(result);
    }

    /**
     * 获取地图路线坐标数据
     *
     * @param params 请求参数，包含fromAttractionId、toAttractionId、transportType
     * @return 路线坐标数据和节点标记
     */
    @PostMapping("/getMapRouteData")
    @IgnoreAuth
    public String getMapRouteData(@RequestBody Map<String, String> params) {
        String fromAttractionId = params.get("fromAttractionId");
        String toAttractionId = params.get("toAttractionId");
        String transportType = params.get("transportType"); // drive/bus/taxi
        
        Map<String, Object> result = routeService.getMapRouteData(fromAttractionId, toAttractionId, transportType);
        return ResultUtil.successWithData(result);
    }

    /**
     * 获取沿途小吃（支持分页、排序、筛选）
     *
     * @param params 请求参数
     * @return 分页后的小吃列表
     */
    @PostMapping("/getNearFoodShopWithPage")
    @IgnoreAuth
    public String getNearFoodShopWithPage(@RequestBody Map<String, Object> params) {
        String fromAttractionId = (String) params.get("fromAttractionId");
        String toAttractionId = (String) params.get("toAttractionId");
        Integer pageNum = params.get("pageNum") != null ? ((Number) params.get("pageNum")).intValue() : 1;
        Integer pageSize = params.get("pageSize") != null ? ((Number) params.get("pageSize")).intValue() : 10;
        String sortBy = (String) params.get("sortBy"); // convenienceIndex/avgPrice/distance
        String sortOrder = (String) params.get("sortOrder"); // asc/desc
        String businessStatus = (String) params.get("businessStatus"); // 营业中/休息中
        
        Map<String, Object> result = routeService.getNearFoodShopWithPage(
            fromAttractionId, toAttractionId, pageNum, pageSize, sortBy, sortOrder, businessStatus);
        return ResultUtil.successWithData(result);
    }

    /**
     * 获取路线周边小吃标记数据
     *
     * @param params 请求参数，包含fromAttractionId和toAttractionId
     * @return 小吃标记数据列表
     */
    @PostMapping("/getFoodMarkers")
    @IgnoreAuth
    public String getFoodMarkers(@RequestBody Map<String, String> params) {
        String fromAttractionId = params.get("fromAttractionId");
        String toAttractionId = params.get("toAttractionId");
        
        List<Map<String, Object>> result = routeService.getFoodMarkers(fromAttractionId, toAttractionId);
        return ResultUtil.successWithData(result);
    }
}
