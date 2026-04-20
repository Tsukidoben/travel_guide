package com.own.service;

import com.own.model.TripStrategy;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 攻略管理表
 * 业务层
 */
public interface TripStrategyService extends IService<TripStrategy> {

    void addNew(TripStrategy tripStrategy);

    void updatePlus(TripStrategy tripStrategy);

    void agree(String id);

    void noAgree(TripStrategy tripStrategy);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<TripStrategy> listPage(QueryFilter<TripStrategy> queryFilter);

    TripStrategy getByIdPlus(String id);

    IPage<TripStrategy> listFront(QueryFilter<TripStrategy> queryFilter);

    IPage<TripStrategy> myTripStrategies(QueryFilter<TripStrategy> queryFilter);

    List<TripStrategy> recommend();

}
