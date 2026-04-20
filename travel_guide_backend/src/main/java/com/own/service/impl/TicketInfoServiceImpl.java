package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.mappers.TicketInfoMapper;
import com.own.model.TicketInfo;
import com.own.service.TicketInfoService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import kotlin.jvm.internal.Lambda;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.ArrayList;

/**
 * 门票管理表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TicketInfoServiceImpl extends ServiceImpl<TicketInfoMapper, TicketInfo> implements TicketInfoService {

    @Override
    public void saveOrUpdatePlus(TicketInfo ticketInfo) {
        if (ObjectUtil.isEmpty(ticketInfo)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(ticketInfo.getAttractionId())) {
            throw new BaseException("所属景点不能为空");
        }
        if (ObjectUtil.isEmpty(ticketInfo.getTicketName())) {
            throw new BaseException("门票名称不能为空");
        }
        if (ObjectUtil.isEmpty(ticketInfo.getTicketPrice())) {
            throw new BaseException("门票价格不能为空");
        }
        if (ObjectUtil.isEmpty(ticketInfo.getUseScope())) {
            throw new BaseException("使用范围不能为空");
        }
        if (ObjectUtil.isEmpty(ticketInfo.getStatus())) {
            throw new BaseException("门票状态不能为空");
        }
        if(ObjectUtil.isEmpty(ticketInfo.getId())){
            this.save(ticketInfo);
        }else{
            this.updateById(ticketInfo);
        }
    }

    @Override
    public void delById(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要删除的数据");
        }
        this.removeById(id);
    }

    @Override
    public void delBatch(DelVo delVo) {
        if(ObjectUtil.isEmpty(delVo.getIds())){
            throw new BaseException("请选择要删除的数据");
        }
        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public IPage<TicketInfo> listPage(QueryFilter<TicketInfo> queryFilter) {
        // 获取分页条件
        IPage<TicketInfo> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        TicketInfo params = CommonUtil.getParams(queryFilter, TicketInfo.class);
        LambdaQueryWrapper<TicketInfo> wrapper = new LambdaQueryWrapper<>();

        wrapper
            .like(ObjectUtil.isNotEmpty(params.getTicketName()), TicketInfo::getTicketName, params.getTicketName())
            .eq(ObjectUtil.isNotEmpty(params.getAttractionId()), TicketInfo::getAttractionId, params.getAttractionId())
;

        // 排序
        wrapper.orderByDesc(TicketInfo::getCreateTime);

        IPage<TicketInfo> resp = this.page(page, wrapper);
        if(ObjectUtil.isNotEmpty(resp.getRecords())){
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public TicketInfo getByIdPlus(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要查看的数据");
        }
        TicketInfo entity = this.getById(id);
        if(ObjectUtil.isNotEmpty(entity)){
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public List<TicketInfo> getByAttractionId(String attractionId) {
        return this.lambdaQuery()
                .eq(TicketInfo::getAttractionId, attractionId)
                .orderByAsc(TicketInfo::getTicketPrice)
                .list();
    }

    private void convert(List<TicketInfo> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, TicketInfo.class)
                    .done()
                    .convert();
        }
    }

    private void convert(TicketInfo entity) {
        List<TicketInfo> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
