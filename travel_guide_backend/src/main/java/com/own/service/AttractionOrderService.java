package com.own.service;

import com.own.model.AttractionOrder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 景点订单表
 * 业务层
 */
public interface AttractionOrderService extends IService<AttractionOrder> {

    String submit(AttractionOrder attractionOrder);

    String payById(String id);

    void cancel(String id);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<AttractionOrder> listPage(QueryFilter<AttractionOrder> queryFilter);

    AttractionOrder getByIdPlus(String id);

    AttractionOrder getByCaptcha(String captcha);

    void writeOff(String captcha);

    String aliNotify(HttpServletRequest request);

    IPage<AttractionOrder> myOrder(QueryFilter<AttractionOrder> queryFilter);
}
