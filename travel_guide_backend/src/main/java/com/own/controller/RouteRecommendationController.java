package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import cn.y8e.common.vo.QueryFilter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.own.common.annotation.IgnoreAuth;
import com.own.model.RouteRecommendation;
import com.own.service.RouteRecommendationService;
import com.own.model.vo.BatchDeleteVo;
import com.own.model.vo.RouteRecommendationRequest;
import com.own.model.vo.RouteRecommendationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 路线推荐控制器
 */
@RestController
@RequestMapping("/api/routeRecommendation")
public class RouteRecommendationController {

    @Autowired
    private RouteRecommendationService routeRecommendationService;

    /**
     * 生成路线推荐（不保存到数据库）
     *
     * @param request 推荐请求参数
     * @return 推荐结果
     */
    @PostMapping("/generate")
    @IgnoreAuth
    public String generateRecommendation(@RequestBody RouteRecommendationRequest request) {
        RouteRecommendationResponse response = routeRecommendationService.generateRecommendation(request);
        return ResultUtil.successWithData(response);
    }

    /**
     * 手动保存路线推荐
     *
     * @param request 推荐请求参数
     * @return 保存后的推荐记录ID
     */
    @PostMapping("/save")
    public String saveRecommendation(@RequestBody RouteRecommendationRequest request) {
        String recommendationId = routeRecommendationService.saveRecommendationManually(request);
        return ResultUtil.successWithData(recommendationId);
    }

    /**
     * 查询推荐历史
     *
     * @param queryFilter 查询条件
     * @return 分页结果
     */
    @PostMapping("/history")
    public String getHistory(@RequestBody(required = false) QueryFilter<RouteRecommendation> queryFilter) {
        IPage<RouteRecommendation> page = routeRecommendationService.getHistory(queryFilter);
        return ResultUtil.<RouteRecommendation>returnPages(page);
    }

    /**
     * 查询推荐详情
     *
     * @param recommendationId 推荐记录ID
     * @return 推荐详情
     */
    @PostMapping("/detail/{recommendationId}")
    @IgnoreAuth
    public String getDetail(@PathVariable("recommendationId") String recommendationId) {
        RouteRecommendationResponse response = routeRecommendationService.getDetail(recommendationId);
        return ResultUtil.successWithData(response);
    }

    /**
     * 删除推荐记录
     *
     * @param recommendationId 推荐记录ID
     * @return 操作结果
     */
    @PostMapping("/delete/{recommendationId}")
    public String deleteRecommendation(@PathVariable("recommendationId") String recommendationId) {
        routeRecommendationService.deleteRecommendation(recommendationId);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除推荐记录
     *
     * @param batchDeleteVo 批量删除请求参数
     * @return 操作结果
     */
    @PostMapping("/batchDelete")
    public String batchDelete(@RequestBody BatchDeleteVo batchDeleteVo) {
        if (batchDeleteVo == null || batchDeleteVo.getIds() == null || batchDeleteVo.getIds().isEmpty()) {
            return ResultUtil.error("请选择要删除的行程");
        }
        
        int count = routeRecommendationService.batchDeleteRecommendation(batchDeleteVo.getIds());
        return ResultUtil.success("已成功删除 " + count + " 条行程");
    }

    /**
     * 收藏推荐方案
     *
     * @param recommendationId 推荐记录ID
     * @return 操作结果
     */
    @PostMapping("/favorite/{recommendationId}")
    public String favoriteRecommendation(@PathVariable("recommendationId") String recommendationId) {
        routeRecommendationService.favoriteRecommendation(recommendationId);
        return ResultUtil.success("收藏成功");
    }
}
