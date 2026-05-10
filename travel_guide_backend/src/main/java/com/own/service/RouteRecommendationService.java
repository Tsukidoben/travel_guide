package com.own.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.own.model.RouteRecommendation;
import cn.y8e.common.vo.QueryFilter;
import com.own.model.vo.RouteRecommendationRequest;
import com.own.model.vo.RouteRecommendationResponse;

import java.util.List;

/**
 * 路线推荐服务接口
 */
public interface RouteRecommendationService {

    /**
     * 生成路线推荐（不保存到数据库）
     *
     * @param request 推荐请求参数
     * @return 推荐结果
     */
    RouteRecommendationResponse generateRecommendation(RouteRecommendationRequest request);

    /**
     * 生成路线推荐（不增加计数，用于保存时重新生成）
     *
     * @param request 推荐请求参数
     * @return 推荐结果
     */
    RouteRecommendationResponse generateRecommendationWithoutCount(RouteRecommendationRequest request);

    /**
     * 手动保存路线推荐
     *
     * @param request 推荐请求参数
     * @return 保存后的推荐记录ID
     */
    String saveRecommendationManually(RouteRecommendationRequest request);

    /**
     * 查询推荐历史
     *
     * @param queryFilter 查询条件
     * @return 分页结果
     */
    IPage<RouteRecommendation> getHistory(QueryFilter<RouteRecommendation> queryFilter);

    /**
     * 查询推荐详情
     *
     * @param recommendationId 推荐记录ID
     * @return 推荐详情
     */
    RouteRecommendationResponse getDetail(String recommendationId);

    /**
     * 删除推荐记录
     *
     * @param recommendationId 推荐记录ID
     */
    void deleteRecommendation(String recommendationId);

    /**
     * 批量删除推荐记录
     *
     * @param ids 推荐记录ID列表
     * @return 删除数量
     */
    int batchDeleteRecommendation(List<String> ids);

    /**
     * 收藏推荐方案（预留接口）
     *
     * @param recommendationId 推荐记录ID
     */
    void favoriteRecommendation(String recommendationId);

    /**
     * 获取用户今日已使用生成次数
     *
     * @return 今日生成次数
     */
    Integer getDailyCount();
}
