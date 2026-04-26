package com.own.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.FoodShop;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

/**
 * 小吃店铺表
 * 业务层
 */
public interface FoodShopService extends IService<FoodShop> {

    void saveOrUpdatePlus(FoodShop foodShop);

    void delById(Long id);

    void delBatch(DelVo delVo);

    IPage<FoodShop> listPage(QueryFilter<FoodShop> queryFilter);

    FoodShop getByIdPlus(Long id);

    java.util.List<FoodShop> listSimple();
}
