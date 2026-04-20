package com.own.service;

import com.own.model.CarouselImage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 轮播图管理表
 * 业务层
 */
public interface CarouselImageService extends IService<CarouselImage> {

    void saveOrUpdatePlus(CarouselImage carouselImage);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<CarouselImage> listPage(QueryFilter<CarouselImage> queryFilter);

    CarouselImage getByIdPlus(String id);

    List<CarouselImage> listFront();
}
