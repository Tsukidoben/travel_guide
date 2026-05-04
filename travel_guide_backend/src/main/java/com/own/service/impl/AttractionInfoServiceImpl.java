package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.mappers.AttractionInfoMapper;
import com.own.model.AttractionCollection;
import com.own.model.AttractionInfo;
import com.own.model.TicketInfo;
import com.own.service.AttractionCollectionService;
import com.own.service.AttractionInfoService;
import com.own.service.GeoCodeService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import com.own.service.TicketInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 景点信息表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttractionInfoServiceImpl extends ServiceImpl<AttractionInfoMapper, AttractionInfo> implements AttractionInfoService {

    @Autowired
    private GeoCodeService geoCodeService;

    @Override
    public void saveOrUpdatePlus(AttractionInfo attractionInfo) {
        if (ObjectUtil.isEmpty(attractionInfo)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(attractionInfo.getAttractionPic())) {
            throw new BaseException("景点图片不能为空");
        }
        if (ObjectUtil.isEmpty(attractionInfo.getAttractionName())) {
            throw new BaseException("景点名称不能为空");
        }
        if (ObjectUtil.isEmpty(attractionInfo.getTypeId())) {
            throw new BaseException("景点分类不能为空");
        }
        if (ObjectUtil.isEmpty(attractionInfo.getAttractionDesc())) {
            throw new BaseException("景点简介不能为空");
        }
        if (ObjectUtil.isEmpty(attractionInfo.getAttractionDetail())) {
            throw new BaseException("景点描述不能为空");
        }
        if (ObjectUtil.isEmpty(attractionInfo.getAttractionPlace())) {
            throw new BaseException("景点位置不能为空");
        }
        
        // 自动解析地址为经纬度
        if (ObjectUtil.isNotEmpty(attractionInfo.getAttractionPlace())) {
            Map<String, Object> geoResult = geoCodeService.geoCode(attractionInfo.getAttractionPlace());
            if (ObjectUtil.isNotEmpty(geoResult) && geoResult.containsKey("longitude") && geoResult.containsKey("latitude")) {
                attractionInfo.setLongitude(new java.math.BigDecimal(geoResult.get("longitude").toString()));
                attractionInfo.setLatitude(new java.math.BigDecimal(geoResult.get("latitude").toString()));
            }
        }
        
        if(ObjectUtil.isEmpty(attractionInfo.getId())){
            this.save(attractionInfo);
        }else{
            this.updateById(attractionInfo);
        }
    }

    @Override
    public void delById(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要删除的数据");
        }
        this.removeById(id);

        // 删除对应门票
        SpringUtil.getBean(TicketInfoService.class)
                .lambdaUpdate()
                .eq(TicketInfo::getAttractionId, id)
                .remove();
        // 删除收藏记录
        SpringUtil.getBean(AttractionCollectionService.class)
                .lambdaUpdate()
                .eq(AttractionCollection::getAttractionId, id)
                .remove();
    }

    @Override
    public void delBatch(DelVo delVo) {
        if(ObjectUtil.isEmpty(delVo.getIds())){
            throw new BaseException("请选择要删除的数据");
        }
        this.removeBatchByIds(delVo.getIds());

        // 删除对应门票
        SpringUtil.getBean(TicketInfoService.class)
                .lambdaUpdate()
                .in(TicketInfo::getAttractionId, delVo.getIds())
                .remove();
        // 删除收藏记录
        SpringUtil.getBean(AttractionCollectionService.class)
                .lambdaUpdate()
                .in(AttractionCollection::getAttractionId, delVo.getIds())
                .remove();
    }

    @Override
    public IPage<AttractionInfo> listPage(QueryFilter<AttractionInfo> queryFilter) {
        // 获取分页条件
        IPage<AttractionInfo> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        AttractionInfo params = CommonUtil.getParams(queryFilter, AttractionInfo.class);
        LambdaQueryWrapper<AttractionInfo> wrapper = new LambdaQueryWrapper<>();

        wrapper
            .eq(ObjectUtil.isNotEmpty(params.getTypeId()), AttractionInfo::getTypeId, params.getTypeId());

        // 排序
        wrapper.orderByDesc(AttractionInfo::getCreateTime);

        // 关键字模糊搜索
        if(ObjectUtil.isNotEmpty(params.getKeyword())){
            wrapper.and(w ->
                        w.like(AttractionInfo::getAttractionName, params.getKeyword())
                        .or().like(AttractionInfo::getAttractionDesc, params.getKeyword())
            );
        }

        IPage<AttractionInfo> resp = this.page(page, wrapper);
        if(ObjectUtil.isNotEmpty(resp.getRecords())){
            // 部分需要转译的文字
            this.convert(resp.getRecords());
            
            // 批量填充最低票价
            fillMinTicketPrice(resp.getRecords());
        }
        return resp;
    }

    @Override
    public AttractionInfo getByIdPlus(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要查看的数据");
        }
        AttractionInfo entity = this.getById(id);
        if(ObjectUtil.isNotEmpty(entity)){
            // 转换数据
            this.convert(entity);
            
            // 查询该景点的门票列表（只查询正常状态的门票）
            List<TicketInfo> ticketList = SpringUtil.getBean(TicketInfoService.class)
                    .lambdaQuery()
                    .eq(TicketInfo::getAttractionId, id)
                    .eq(TicketInfo::getStatus, "1")  // 只查询正常状态的门票
                    .orderByAsc(TicketInfo::getTicketPrice)  // 按价格升序排序
                    .list();
            entity.setTicketInfoList(ticketList);
        }

        AttractionCollection attractionCollection = SpringUtil.getBean(AttractionCollectionService.class)
                .lambdaQuery()
                .eq(AttractionCollection::getAttractionId, id)
                .eq(AttractionCollection::getUserId, ContextUtil.getCurrentUserId())
                .one();

        if(ObjectUtil.isNotEmpty(attractionCollection)){
            entity.setIsCollect(1);
        }else{
            entity.setIsCollect(0);
        }

        return entity;
    }

    @Override
    public List<AttractionInfo> recommend() {
        List<AttractionInfo> list = this.list();
        if(ObjectUtil.isNotEmpty(list)){
            this.convert(list);
        }
        return RandomUtil.randomEleList(list,5);
    }

    private void convert(List<AttractionInfo> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, AttractionInfo.class)
                    .done()
                    .convert();
        }
    }

    /**
     * 批量填充景点的最低票价
     */
    private void fillMinTicketPrice(List<AttractionInfo> attractionList) {
        if (CollUtil.isEmpty(attractionList)) {
            return;
        }
        
        // 提取所有景点 ID
        List<String> attractionIds = attractionList.stream()
                .map(AttractionInfo::getId)
                .collect(java.util.stream.Collectors.toList());
        
        // 批量查询每个景点的最低票价
        List<TicketInfo> tickets = SpringUtil.getBean(TicketInfoService.class)
                .lambdaQuery()
                .in(TicketInfo::getAttractionId, attractionIds)
                .eq(TicketInfo::getStatus, "1")  // 只查询正常状态的门票
                .select(TicketInfo::getAttractionId, TicketInfo::getTicketPrice)
                .list();
        
        // 按景点 ID 分组，计算每个景点的最低票价
        Map<String, java.math.BigDecimal> minPriceMap = new java.util.HashMap<>();
        tickets.stream()
                .collect(java.util.stream.Collectors.groupingBy(TicketInfo::getAttractionId))
                .forEach((attractionId, ticketList) -> {
                    java.math.BigDecimal minPrice = ticketList.stream()
                            .map(TicketInfo::getTicketPrice)
                            .filter(ObjectUtil::isNotEmpty)
                            .min(java.math.BigDecimal::compareTo)
                            .orElse(null);
                    minPriceMap.put(attractionId, minPrice);
                });
        
        // 填充到景点对象中
        for (AttractionInfo attraction : attractionList) {
            attraction.setMinTicketPrice(minPriceMap.get(attraction.getId()));
        }
    }

    private void convert(AttractionInfo entity) {
        List<AttractionInfo> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
