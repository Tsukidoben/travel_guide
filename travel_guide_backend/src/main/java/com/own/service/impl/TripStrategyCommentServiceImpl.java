package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.mappers.TripStrategyCommentMapper;
import com.own.model.TripStrategyComment;
import com.own.model.User;
import com.own.service.TripStrategyCommentService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.ArrayList;

/**
 * 攻略评论表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TripStrategyCommentServiceImpl extends ServiceImpl<TripStrategyCommentMapper, TripStrategyComment> implements TripStrategyCommentService {

    @Override
    public void addComment(TripStrategyComment comment) {
        if (ObjectUtil.isEmpty(comment.getTripStrategyId())) {
            throw new BaseException("请选择要评论的攻略id");
        }
        if (ObjectUtil.isNotEmpty(comment.getReplayCommentId())) {
            // 回复评论
            TripStrategyComment replyComment = this.getById(comment.getReplayCommentId());
            if (replyComment == null) {
                throw new BaseException("没有找到对应回复");
            }
            comment.setMainComment(0)
                    .setMainCommentId(replyComment.getMainCommentId())
                    .setReplyId(replyComment.getCreator())
                    .setReplyName(replyComment.getCreateName());
        } else {
            // 新增评论
            comment.setId(IdUtil.getSnowflakeNextIdStr())
                    .setMainComment(1)
                    .setMainCommentId(comment.getId());
        }
        this.saveOrUpdate(comment);
    }

    @Override
    public void delById(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要删除的数据");
        }

        this.removeById(id);
    }

    @Override
    public TripStrategyComment getByIdPlus(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要查看的数据");
        }

        TripStrategyComment entity = this.getById(id);

        if (ObjectUtil.isNotEmpty(entity)) {
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public List<TripStrategyComment> getByTripStrategyId(String tripStrategyId) {
        // 查询出该攻略下所有评论信息
        List<TripStrategyComment> strategyCommentList = this.lambdaQuery()
                .eq(TripStrategyComment::getTripStrategyId, tripStrategyId)
                .orderByDesc(TripStrategyComment::getCreateTime)
                .list();
        if (CollUtil.isEmpty(strategyCommentList)) {
            return new ArrayList<>();
        }

        this.convert(strategyCommentList);

        // 过滤出主评论信息
        List<TripStrategyComment> mainComment = strategyCommentList.stream().filter(i -> i.getMainComment() == 1).toList();

        return mainComment.stream()
                .peek(comment -> {
                    // 查询子评论
                    List<TripStrategyComment> children = strategyCommentList.stream()
                            .filter(i -> i.getMainComment() == 0 && StrUtil.equals(i.getMainCommentId(), comment.getId()))
                            .sorted((o1, o2) ->
                                    o2.getCreateTime().compareTo(o1.getCreateTime())
                            )
                            .toList();
                    comment.setChildren(children);
                })
                .toList();
    }

    private void convert(List<TripStrategyComment> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, TripStrategyComment.class)
                    .lambdaAdd(TripStrategyComment::getCreator, User::getId, TripStrategyComment::getCreatorHead, User::getHeadPicUrl, User.class)
                    .lambdaAdd(TripStrategyComment::getReplyId, User::getId, TripStrategyComment::getReplyHead, User::getHeadPicUrl, User.class)
                    .done()
                    .convert();
        }
    }

    private void convert(TripStrategyComment entity) {
        List<TripStrategyComment> list = new ArrayList<>();
        list.add(entity);

        this.convert(list);
    }
}
