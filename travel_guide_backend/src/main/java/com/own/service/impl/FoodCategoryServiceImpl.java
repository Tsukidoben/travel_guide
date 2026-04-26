package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.CommonUtil;
import com.own.mappers.FoodCategoryMapper;
import com.own.model.FoodCategory;
import com.own.model.vo.DelVo;
import com.own.service.FoodCategoryService;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 小吃分类表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FoodCategoryServiceImpl extends ServiceImpl<FoodCategoryMapper, FoodCategory> implements FoodCategoryService {

    @Override
    public void saveOrUpdatePlus(FoodCategory foodCategory) {
        if (ObjectUtil.isEmpty(foodCategory)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(foodCategory.getName())) {
            throw new BaseException("分类名称不能为空");
        }
        
        // 校验分类名称唯一性
        LambdaQueryWrapper<FoodCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodCategory::getName, foodCategory.getName());
        
        // 如果是编辑操作，排除当前记录
        if (ObjectUtil.isNotEmpty(foodCategory.getId())) {
            wrapper.ne(FoodCategory::getId, foodCategory.getId());
        }
        
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BaseException("分类名称已存在，请使用其他名称");
        }
        
        if (ObjectUtil.isEmpty(foodCategory.getId())) {
            this.save(foodCategory);
        } else {
            this.updateById(foodCategory);
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
        // 将 String 类型的 ID 转换为 Long 类型
        List<Long> ids = delVo.getIds().stream()
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
        this.removeBatchByIds(ids);
    }

    @Override
    public IPage<FoodCategory> listPage(QueryFilter<FoodCategory> queryFilter) {
        IPage<FoodCategory> page = CommonUtil.getPage(queryFilter);
        LambdaQueryWrapper<FoodCategory> wrapper = new LambdaQueryWrapper<>();
        
        // 排序
        wrapper.orderByAsc(FoodCategory::getSort)
                .orderByDesc(FoodCategory::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<FoodCategory> listAll() {
        LambdaQueryWrapper<FoodCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(FoodCategory::getSort)
                .orderByDesc(FoodCategory::getCreateTime);
        return this.list(wrapper);
    }
}
