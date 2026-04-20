package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.mappers.TripStrategyMapper;
import com.own.model.AttractionInfo;
import com.own.model.TripStrategy;
import com.own.model.User;
import com.own.service.TripStrategyService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * 攻略管理表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TripStrategyServiceImpl extends ServiceImpl<TripStrategyMapper, TripStrategy> implements TripStrategyService {

    @Override
    public void addNew(TripStrategy tripStrategy) {
        if (ObjectUtil.isEmpty(tripStrategy)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(tripStrategy.getStrategyContent())) {
            throw new BaseException("攻略内容不能为空");
        }

        if (ObjectUtil.isEmpty(tripStrategy.getAttractionId())) {
            throw new BaseException("景点id不能为空");
        }

        tripStrategy.setStatus("10");
        this.save(tripStrategy);
    }

    @Override
    public void updatePlus(TripStrategy tripStrategy) {
        this.updateById(tripStrategy);
    }

    @Override
    public void agree(String id) {
        TripStrategy byId = this.getById(id);
        if (ObjectUtil.isEmpty(byId)) {
            throw new BaseException("攻略不存在");
        }
        byId.setStatus("80")
                .setReviewReason("审核通过，自动发布")
                .setReviewTime(new Date());
        this.updateById(byId);
    }

    @Override
    public void noAgree(TripStrategy tripStrategy) {
        TripStrategy byId = this.getById(tripStrategy.getId());
        if (ObjectUtil.isEmpty(byId)) {
            throw new BaseException("攻略不存在");
        }
        byId.setStatus("-2")
                .setReviewReason(tripStrategy.getReviewReason())
                .setReviewTime(new Date());
        this.updateById(byId);
    }

    @Override
    public void delById(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要删除的数据");
        }
        this.removeById(id);
    }

    @Override
    public void delBatch(DelVo delVo) {
        if (ObjectUtil.isEmpty(delVo.getIds())) {
            throw new BaseException("请选择要删除的数据");
        }
        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public IPage<TripStrategy> listPage(QueryFilter<TripStrategy> queryFilter) {
        // 获取分页条件
        IPage<TripStrategy> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        TripStrategy params = CommonUtil.getParams(queryFilter, TripStrategy.class);
        LambdaQueryWrapper<TripStrategy> wrapper = new LambdaQueryWrapper<>();

        wrapper
                .like(ObjectUtil.isNotEmpty(params.getStrategyContent()), TripStrategy::getStrategyContent, params.getStrategyContent())
                .eq(ObjectUtil.isNotEmpty(params.getStatus()), TripStrategy::getStatus, params.getStatus());

        // 排序
        wrapper.orderByDesc(TripStrategy::getCreateTime);

        IPage<TripStrategy> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public TripStrategy getByIdPlus(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要查看的数据");
        }
        TripStrategy entity = this.getById(id);
        if (ObjectUtil.isNotEmpty(entity)) {
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public IPage<TripStrategy> listFront(QueryFilter<TripStrategy> queryFilter) {
        // 获取分页条件
        IPage<TripStrategy> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        TripStrategy params = CommonUtil.getParams(queryFilter, TripStrategy.class);
        LambdaQueryWrapper<TripStrategy> wrapper = new LambdaQueryWrapper<>();

        wrapper
                .like(ObjectUtil.isNotEmpty(params.getStrategyContent()), TripStrategy::getStrategyContent, params.getStrategyContent())
                .eq(true, TripStrategy::getStatus, "80");

        // 排序
        wrapper.orderByDesc(TripStrategy::getCreateTime);

        IPage<TripStrategy> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public IPage<TripStrategy> myTripStrategies(QueryFilter<TripStrategy> queryFilter) {
        // 获取分页条件
        IPage<TripStrategy> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        TripStrategy params = CommonUtil.getParams(queryFilter, TripStrategy.class);
        LambdaQueryWrapper<TripStrategy> wrapper = new LambdaQueryWrapper<>();

        wrapper
                .like(ObjectUtil.isNotEmpty(params.getStrategyContent()), TripStrategy::getStrategyContent, params.getStrategyContent())
                .eq(true, TripStrategy::getCreator, ContextUtil.getCurrentUserId())
                .eq(ObjectUtil.isNotEmpty(params.getStatus()), TripStrategy::getStatus, params.getStatus());

        // 排序
        wrapper.orderByDesc(TripStrategy::getCreateTime);

        IPage<TripStrategy> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public List<TripStrategy> recommend() {
        List<TripStrategy> list = this.list();
        if(ObjectUtil.isNotEmpty(list)){
            this.convert(list);
        }
        return RandomUtil.randomEleList(list,5);
    }

    private void convert(List<TripStrategy> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, TripStrategy.class)
                    .lambdaAdd(TripStrategy::getCreator, User::getId, TripStrategy::getCreateHead, User::getHeadPicUrl, User.class)
                    .lambdaAdd(TripStrategy::getAttractionId, AttractionInfo::getId, TripStrategy::getAttractionName, AttractionInfo::getAttractionName, AttractionInfo.class)
                    .done()
                    .convert();
        }
    }

    private void convert(TripStrategy entity) {
        List<TripStrategy> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
