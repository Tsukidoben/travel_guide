package com.own.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.FoodFavorite;
import cn.y8e.common.vo.QueryFilter;

/**
 * 小吃收藏表
 * 业务层
 */
public interface FoodFavoriteService extends IService<FoodFavorite> {

    String toggleFavorite(Long foodId);

    IPage<FoodFavorite> listPage(QueryFilter<FoodFavorite> queryFilter);
}
