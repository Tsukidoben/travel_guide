package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.CommonUtil;
import com.own.common.utils.ContextUtil;
import com.own.mappers.FoodCommentMapper;
import com.own.model.FoodComment;
import com.own.model.FoodInfo;
import com.own.model.User;
import com.own.service.FoodCommentService;
import com.own.service.FoodInfoService;
import com.own.service.UserService;
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
 * 小吃评论表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FoodCommentServiceImpl extends ServiceImpl<FoodCommentMapper, FoodComment> implements FoodCommentService {

    @Override
    public void addComment(FoodComment foodComment) {
        if (ObjectUtil.isEmpty(foodComment)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(foodComment.getFoodId())) {
            throw new BaseException("小吃ID不能为空");
        }
        if (ObjectUtil.isEmpty(foodComment.getContent())) {
            throw new BaseException("评价内容不能为空");
        }
        if (ObjectUtil.isEmpty(foodComment.getScore())) {
            foodComment.setScore(5);
        }
        
        // 设置当前用户ID
        foodComment.setUserId(Long.parseLong(ContextUtil.getCurrentUserId()));
        
        // 默认状态为通过
        if (ObjectUtil.isEmpty(foodComment.getStatus())) {
            foodComment.setStatus(1);
        }
        
        this.save(foodComment);
    }

    @Override
    public void delById(Long id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要删除的数据");
        }
        this.removeById(id);
    }

    @Override
    public IPage<FoodComment> listPage(QueryFilter<FoodComment> queryFilter) {
        IPage<FoodComment> page = CommonUtil.getPage(queryFilter);
        FoodComment params = CommonUtil.getParams(queryFilter, FoodComment.class);
        LambdaQueryWrapper<FoodComment> wrapper = new LambdaQueryWrapper<>();

        // 按小吃ID查询
        wrapper.eq(ObjectUtil.isNotEmpty(params.getFoodId()), FoodComment::getFoodId, params.getFoodId());
        
        // 只查询通过的评论
        wrapper.eq(FoodComment::getStatus, 1);

        // 排序
        wrapper.orderByDesc(FoodComment::getCreateTime);

        IPage<FoodComment> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            this.convert(resp.getRecords());
        }
        return resp;
    }

    private void convert(List<FoodComment> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 获取所有用户ID和小吃ID
            List<Long> userIds = list.stream().map(FoodComment::getUserId).distinct().collect(Collectors.toList());
            List<Long> foodIds = list.stream().map(FoodComment::getFoodId).distinct().collect(Collectors.toList());

            // 批量查询用户信息
            Map<Long, User> userMap = SpringUtil.getBean(UserService.class)
                    .listByIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(u -> Long.parseLong(u.getId()), u -> u));

            // 批量查询小吃信息
            Map<Long, FoodInfo> foodMap = SpringUtil.getBean(FoodInfoService.class)
                    .listByIds(foodIds)
                    .stream()
                    .collect(Collectors.toMap(FoodInfo::getId, f -> f));

            // 填充用户名、头像和小吃名称
            list.forEach(comment -> {
                // 填充用户信息
                User user = userMap.get(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getUserName());
                    comment.setUserHeadPicUrl(user.getHeadPicUrl());
                }
                
                // 填充小吃名称
                FoodInfo food = foodMap.get(comment.getFoodId());
                if (food != null) {
                    comment.setFoodName(food.getName());
                }
            });
        }
    }

    private void convert(FoodComment entity) {
        List<FoodComment> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
