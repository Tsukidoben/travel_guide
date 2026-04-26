package com.own.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.FoodInfo;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

/**
 * 小吃信息表
 * 业务层
 */
public interface FoodInfoService extends IService<FoodInfo> {

    void saveOrUpdatePlus(FoodInfo foodInfo);

    void delById(Long id);

    void delBatch(DelVo delVo);

    IPage<FoodInfo> listPage(QueryFilter<FoodInfo> queryFilter);

    FoodInfo getByIdPlus(Long id);

    void updateStatus(Long id, Integer status);

    void updateRecommend(Long id, Integer isRecommend);
}
