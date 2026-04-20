package com.own.service;

import com.own.model.AttractionInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 景点信息表
 * 业务层
 */
public interface AttractionInfoService extends IService<AttractionInfo> {

    void saveOrUpdatePlus(AttractionInfo attractionInfo);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<AttractionInfo> listPage(QueryFilter<AttractionInfo> queryFilter);

    AttractionInfo getByIdPlus(String id);

    List<AttractionInfo> recommend();


}
