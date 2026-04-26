package com.own.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.FoodCategory;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 小吃分类表
 * 业务层
 */
public interface FoodCategoryService extends IService<FoodCategory> {

    void saveOrUpdatePlus(FoodCategory foodCategory);

    void delById(Long id);

    void delBatch(DelVo delVo);

    IPage<FoodCategory> listPage(QueryFilter<FoodCategory> queryFilter);

    List<FoodCategory> listAll();
}
