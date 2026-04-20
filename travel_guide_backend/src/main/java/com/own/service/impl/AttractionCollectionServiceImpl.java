package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.mappers.AttractionCollectionMapper;
import com.own.model.AttractionCollection;
import com.own.model.AttractionInfo;
import com.own.service.AttractionCollectionService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import com.own.service.AttractionInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.ArrayList;

/**
 * 景点收藏表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttractionCollectionServiceImpl extends ServiceImpl<AttractionCollectionMapper, AttractionCollection> implements AttractionCollectionService {

    @Override
    public void collect(String attractionId) {
        AttractionInfo attractionInfo = SpringUtil.getBean(AttractionInfoService.class).getById(attractionId);

        AttractionCollection attractionCollection = this.lambdaQuery()
                .eq(AttractionCollection::getAttractionId, attractionId)
                .eq(AttractionCollection::getUserId,ContextUtil.getCurrentUserId())
                .one();

        if (ObjectUtil.isEmpty(attractionCollection)) {
            // 说明没收藏
            attractionCollection = new AttractionCollection();
            attractionCollection.setAttractionId(attractionId)
                    .setUserId(ContextUtil.getCurrentUserId());
            this.save(attractionCollection);
        }
    }

    @Override
    public void noCollect(String attractionId) {
        // 删除收藏记录
        this.lambdaUpdate()
                .eq(AttractionCollection::getAttractionId, attractionId)
                .eq(AttractionCollection::getUserId,ContextUtil.getCurrentUserId())
                .remove();
    }

    @Override
    public void delById(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要删除的数据");
        }

        this.removeById(id);
    }

    @Override
    public void delByBatch(DelVo delVo) {
        if (ObjectUtil.isEmpty(delVo.getIds())) {
            throw new BaseException("请选择要删除的数据");
        }

        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public List<AttractionCollection> myCollect() {
        List<AttractionCollection> list = this.lambdaQuery()
                .eq(AttractionCollection::getUserId, ContextUtil.getCurrentUserId())
                .orderByDesc(AttractionCollection::getCreateTime)
                .list();
        if (CollUtil.isNotEmpty(list)) {
            this.convert(list);
        }
        return list;
    }

    private void convert(List<AttractionCollection> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, AttractionCollection.class)
                    .lambdaAdd(AttractionCollection::getAttractionId, AttractionInfo::getId, AttractionCollection::getAttractionName, AttractionInfo::getAttractionName, AttractionInfo.class)
                    .lambdaAdd(AttractionCollection::getAttractionId, AttractionInfo::getId, AttractionCollection::getAttractionDesc, AttractionInfo::getAttractionDesc, AttractionInfo.class)
                    .lambdaAdd(AttractionCollection::getAttractionId, AttractionInfo::getId, AttractionCollection::getAttractionPic, AttractionInfo::getAttractionPic, AttractionInfo.class)
                    .done()
                    .convert();
        }
    }

    private void convert(AttractionCollection entity) {
        List<AttractionCollection> list = new ArrayList<>();
        list.add(entity);

        this.convert(list);
    }
}
