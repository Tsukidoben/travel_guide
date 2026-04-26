package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.CommonUtil;
import com.own.mappers.FoodShopMapper;
import com.own.model.FoodShop;
import com.own.model.vo.DelVo;
import com.own.service.FoodShopService;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 小吃店铺表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FoodShopServiceImpl extends ServiceImpl<FoodShopMapper, FoodShop> implements FoodShopService {

    @Override
    public void saveOrUpdatePlus(FoodShop foodShop) {
        if (ObjectUtil.isEmpty(foodShop)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(foodShop.getName())) {
            throw new BaseException("店铺名称不能为空");
        }
        
        // 校验电话号码格式（如果提供了电话号码）
        if (ObjectUtil.isNotEmpty(foodShop.getPhone())) {
            String phone = foodShop.getPhone().trim();
            // 支持手机号和固定电话
            boolean isValidPhone = phone.matches("^1[3-9]\\d{9}$") ||  // 手机号
                                   phone.matches("^0\\d{2,3}-?\\d{7,8}$");  // 固定电话
            if (!isValidPhone) {
                throw new BaseException("电话号码格式不正确");
            }
        }
        
        if (ObjectUtil.isEmpty(foodShop.getId())) {
            this.save(foodShop);
        } else {
            this.updateById(foodShop);
        }
    }

    @Override
    public void delById(Long id) {
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
        List<Long> ids = delVo.getIds().stream()
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
        this.removeBatchByIds(ids);
    }

    @Override
    public IPage<FoodShop> listPage(QueryFilter<FoodShop> queryFilter) {
        IPage<FoodShop> page = CommonUtil.getPage(queryFilter);
        FoodShop params = CommonUtil.getParams(queryFilter, FoodShop.class);
        LambdaQueryWrapper<FoodShop> wrapper = new LambdaQueryWrapper<>();

        // 关键字模糊搜索
        if (ObjectUtil.isNotEmpty(params.getKeyword())) {
            wrapper.and(w ->
                    w.like(FoodShop::getName, params.getKeyword())
                            .or().like(FoodShop::getAddress, params.getKeyword())
            );
        }

        // 排序
        wrapper.orderByDesc(FoodShop::getCreateTime);

        IPage<FoodShop> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public FoodShop getByIdPlus(Long id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要查看的数据");
        }
        FoodShop entity = this.getById(id);
        if (ObjectUtil.isNotEmpty(entity)) {
            this.convert(entity);
        }
        return entity;
    }

    private void convert(List<FoodShop> list) {
        if (CollUtil.isNotEmpty(list)) {
            ConvertUtil.of(list, FoodShop.class)
                    .done()
                    .convert();
        }
    }

    private void convert(FoodShop entity) {
        List<FoodShop> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }

    @Override
    public List<FoodShop> listSimple() {
        LambdaQueryWrapper<FoodShop> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(FoodShop::getId, FoodShop::getName)
                .orderByDesc(FoodShop::getCreateTime);
        return this.list(wrapper);
    }
}
