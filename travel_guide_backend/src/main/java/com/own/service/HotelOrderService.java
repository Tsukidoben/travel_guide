package com.own.service;

import com.own.model.HotelOrder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 酒店订单表
 * 业务层
 */
public interface HotelOrderService extends IService<HotelOrder> {

    String submit(HotelOrder hotelOrder);

    void cancel(String id);

    String payById(String id);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<HotelOrder> listPage(QueryFilter<HotelOrder> queryFilter);

    HotelOrder getByIdPlus(String id);

    String aliNotify(HttpServletRequest request);

    HotelOrder getByCaptcha(String captcha);

    void writeOff(String captcha);

    IPage<HotelOrder> myOrder(QueryFilter<HotelOrder> queryFilter);
}
