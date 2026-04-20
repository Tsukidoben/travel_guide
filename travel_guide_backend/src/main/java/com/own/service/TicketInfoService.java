package com.own.service;

import com.own.model.TicketInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 门票管理表
 * 业务层
 */
public interface TicketInfoService extends IService<TicketInfo> {

    void saveOrUpdatePlus(TicketInfo ticketInfo);

    void delById(String id);

    void delBatch(DelVo delVo);

    IPage<TicketInfo> listPage(QueryFilter<TicketInfo> queryFilter);

    TicketInfo getByIdPlus(String id);

    List<TicketInfo> getByAttractionId(String attractionId);
}
