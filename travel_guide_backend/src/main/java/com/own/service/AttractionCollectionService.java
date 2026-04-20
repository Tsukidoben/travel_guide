package com.own.service;

import com.own.model.AttractionCollection;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 景点收藏表
 * 业务层
 */
public interface AttractionCollectionService extends IService<AttractionCollection> {

    void collect(String attractionId);

    void noCollect(String attractionId);

    void delById(String id);

    void delByBatch(DelVo delVo);

    List<AttractionCollection> myCollect();

}
