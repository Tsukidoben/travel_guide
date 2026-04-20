package com.own.service;

import com.own.model.HotelRoom;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 房间管理表
 * 业务层
 */
public interface HotelRoomService extends IService<HotelRoom> {

    void saveOrUpdatePlus(HotelRoom hotelRoom);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<HotelRoom> listPage(QueryFilter<HotelRoom> queryFilter);

    HotelRoom getByIdPlus(String id);

    List<HotelRoom> getByHotelId(String hotelId);
}
