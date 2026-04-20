package com.own.service;

import com.own.model.HotelInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 酒店管理表
 * 业务层
 */
public interface HotelInfoService extends IService<HotelInfo> {

    void saveOrUpdatePlus(HotelInfo hotelInfo);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<HotelInfo> listPage(QueryFilter<HotelInfo> queryFilter);

    HotelInfo getByIdPlus(String id);

    List<HotelInfo> recommend();

}
