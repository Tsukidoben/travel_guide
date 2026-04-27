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
}
