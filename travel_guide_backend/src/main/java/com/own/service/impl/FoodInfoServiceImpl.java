package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.CommonUtil;
import com.own.mappers.FoodInfoMapper;
import com.own.model.FoodCategory;
import com.own.model.FoodInfo;
import com.own.model.FoodShop;
import com.own.model.vo.DelVo;
import com.own.service.FoodCategoryService;
import com.own.service.FoodInfoService;
import com.own.service.FoodShopService;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小吃信息表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FoodInfoServiceImpl extends ServiceImpl<FoodInfoMapper, FoodInfo> implements FoodInfoService {

    @Override
    public void saveOrUpdatePlus(FoodInfo foodInfo) {
        if (ObjectUtil.isEmpty(foodInfo)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(foodInfo.getName())) {
            throw new BaseException("小吃名称不能为空");
        }
        if (ObjectUtil.isEmpty(foodInfo.getCategoryId())) {
            throw new BaseException("分类ID不能为空");
        }
        if (ObjectUtil.isEmpty(foodInfo.getShopId())) {
            throw new BaseException("店铺ID不能为空");
        }
        
        // 校验分类是否存在
        FoodCategory category = SpringUtil.getBean(FoodCategoryService.class).getById(foodInfo.getCategoryId());
        if (ObjectUtil.isEmpty(category)) {
            throw new BaseException("分类不存在");
        }
        
        // 校验店铺是否存在
        FoodShop shop = SpringUtil.getBean(FoodShopService.class).getById(foodInfo.getShopId());
        if (ObjectUtil.isEmpty(shop)) {
            throw new BaseException("店铺不存在");
        }
        
        // 默认值设置
        if (ObjectUtil.isEmpty(foodInfo.getIsRecommend())) {
            foodInfo.setIsRecommend(0);
        }
        if (ObjectUtil.isEmpty(foodInfo.getStatus())) {
            foodInfo.setStatus(1);
        }
        
        if (ObjectUtil.isEmpty(foodInfo.getId())) {
            this.save(foodInfo);
        } else {
            this.updateById(foodInfo);
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
    public IPage<FoodInfo> listPage(QueryFilter<FoodInfo> queryFilter) {
        IPage<FoodInfo> page = CommonUtil.getPage(queryFilter);
        FoodInfo params = CommonUtil.getParams(queryFilter, FoodInfo.class);
        
        // 注意：status 参数由前端传递，不设置默认值
        // 当 status 为 null 时，SQL 不会添加状态筛选条件，查询所有数据
        // 当 status 有明确值（0 或 1）时，才按该状态进行筛选
        
        // 使用 XML 中的关联查询 SQL
        return this.baseMapper.selectPageWithJoin((com.baomidou.mybatisplus.extension.plugins.pagination.Page<FoodInfo>) page, params);
    }

    @Override
    public FoodInfo getByIdPlus(Long id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要查看的数据");
        }
        FoodInfo entity = this.getById(id);
        if (ObjectUtil.isNotEmpty(entity)) {
            this.convert(entity);
            
            // 关联查询店铺信息
            FoodShop foodShop = SpringUtil.getBean(FoodShopService.class).getById(entity.getShopId());
            entity.setFoodShop(foodShop);
        }
        return entity;
    }

    private void convert(List<FoodInfo> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 获取所有分类ID和店铺ID
            List<Long> categoryIds = list.stream().map(FoodInfo::getCategoryId).distinct().collect(Collectors.toList());
            List<Long> shopIds = list.stream().map(FoodInfo::getShopId).distinct().collect(Collectors.toList());

            // 批量查询分类和店铺
            Map<Long, String> categoryMap = SpringUtil.getBean(FoodCategoryService.class)
                    .listByIds(categoryIds)
                    .stream()
                    .collect(Collectors.toMap(FoodCategory::getId, FoodCategory::getName));

            Map<Long, String> shopMap = SpringUtil.getBean(FoodShopService.class)
                    .listByIds(shopIds)
                    .stream()
                    .collect(Collectors.toMap(FoodShop::getId, FoodShop::getName));

            // 填充分类名称和店铺名称
            list.forEach(foodInfo -> {
                foodInfo.setCategoryName(categoryMap.get(foodInfo.getCategoryId()));
                foodInfo.setShopName(shopMap.get(foodInfo.getShopId()));
            });
        }
    }

    private void convert(FoodInfo entity) {
        List<FoodInfo> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要操作的数据");
        }
        if (ObjectUtil.isEmpty(status)) {
            throw new BaseException("状态不能为空");
        }
        FoodInfo foodInfo = this.getById(id);
        if (ObjectUtil.isEmpty(foodInfo)) {
            throw new BaseException("小吃不存在");
        }
        foodInfo.setStatus(status);
        this.updateById(foodInfo);
    }

    @Override
    public void updateRecommend(Long id, Integer isRecommend) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要操作的数据");
        }
        if (ObjectUtil.isEmpty(isRecommend)) {
            throw new BaseException("推荐状态不能为空");
        }
        FoodInfo foodInfo = this.getById(id);
        if (ObjectUtil.isEmpty(foodInfo)) {
            throw new BaseException("小吃不存在");
        }
        foodInfo.setIsRecommend(isRecommend);
        this.updateById(foodInfo);
    }
}
