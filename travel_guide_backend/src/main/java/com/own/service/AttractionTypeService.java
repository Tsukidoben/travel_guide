package com.own.service;

import com.own.model.AttractionType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 分类管理表
 * 业务层
 */
public interface AttractionTypeService extends IService<AttractionType> {

    void saveOrUpdatePlus(AttractionType attractionType);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<AttractionType> listPage(QueryFilter<AttractionType> queryFilter);

    AttractionType getByIdPlus(String id);

    List<AttractionType> listFront();

}
