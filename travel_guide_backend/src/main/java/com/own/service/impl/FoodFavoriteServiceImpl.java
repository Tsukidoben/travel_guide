package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.CommonUtil;
import com.own.common.utils.ContextUtil;
import com.own.mappers.FoodFavoriteMapper;
import com.own.model.FoodFavorite;
import com.own.model.FoodInfo;
import com.own.model.FoodShop;
import com.own.service.FoodFavoriteService;
import com.own.service.FoodInfoService;
import com.own.service.FoodShopService;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小吃收藏表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FoodFavoriteServiceImpl extends ServiceImpl<FoodFavoriteMapper, FoodFavorite> implements FoodFavoriteService {

    @Override
    public String toggleFavorite(Long foodId) {
        if (ObjectUtil.isEmpty(foodId)) {
            throw new BaseException("小吃ID不能为空");
        }

        Long userId = Long.parseLong(ContextUtil.getCurrentUserId());

        // 查询是否已收藏
        FoodFavorite existing = this.lambdaQuery()
                .eq(FoodFavorite::getFoodId, foodId)
                .eq(FoodFavorite::getUserId, userId)
                .one();

        if (ObjectUtil.isNotEmpty(existing)) {
            // 已收藏，取消收藏
            this.removeById(existing.getId());
            return "取消收藏成功";
        } else {
            // 未收藏，添加收藏（唯一索引会防止重复）
            FoodFavorite favorite = new FoodFavorite();
            favorite.setFoodId(foodId);
            favorite.setUserId(userId);
            this.save(favorite);
            return "收藏成功";
        }
    }

    @Override
    public IPage<FoodFavorite> listPage(QueryFilter<FoodFavorite> queryFilter) {
        IPage<FoodFavorite> page = CommonUtil.getPage(queryFilter);
        LambdaQueryWrapper<FoodFavorite> wrapper = new LambdaQueryWrapper<>();

        // 只查询当前用户的收藏
        wrapper.eq(FoodFavorite::getUserId, Long.parseLong(ContextUtil.getCurrentUserId()));

        // 排序
        wrapper.orderByDesc(FoodFavorite::getCreateTime);

        IPage<FoodFavorite> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            this.convert(resp.getRecords());
        }
        return resp;
    }

    private void convert(List<FoodFavorite> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 获取所有小吃ID
            List<Long> foodIds = list.stream().map(FoodFavorite::getFoodId).distinct().collect(Collectors.toList());

            // 批量查询小吃信息
            Map<Long, FoodInfo> foodMap = SpringUtil.getBean(FoodInfoService.class)
                    .listByIds(foodIds)
                    .stream()
                    .collect(Collectors.toMap(FoodInfo::getId, f -> f));

            // 获取所有店铺ID
            List<Long> shopIds = foodMap.values().stream()
                    .map(FoodInfo::getShopId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询店铺信息
            Map<Long, String> shopMap = SpringUtil.getBean(FoodShopService.class)
                    .listByIds(shopIds)
                    .stream()
                    .collect(Collectors.toMap(FoodShop::getId, FoodShop::getName));

            // 填充小吃名称、图片和店铺名称
            list.forEach(favorite -> {
                FoodInfo food = foodMap.get(favorite.getFoodId());
                if (food != null) {
                    favorite.setFoodName(food.getName());
                    favorite.setFoodImages(food.getImages());
                    favorite.setShopName(shopMap.get(food.getShopId()));
                }
            });
        }
    }
}
