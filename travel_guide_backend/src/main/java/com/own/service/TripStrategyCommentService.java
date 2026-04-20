package com.own.service;

import com.own.model.TripStrategyComment;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 攻略评论表
 * 业务层
 */
public interface TripStrategyCommentService extends IService<TripStrategyComment> {

    void addComment(TripStrategyComment entity);

    void delById(String id);

    TripStrategyComment getByIdPlus(String id);

    List<TripStrategyComment> getByTripStrategyId(String tripStrategyId);
}
