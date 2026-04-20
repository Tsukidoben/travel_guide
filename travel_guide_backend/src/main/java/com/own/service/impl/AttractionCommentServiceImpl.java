package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.mappers.AttractionCommentMapper;
import com.own.model.AttractionComment;
import com.own.model.User;
import com.own.service.AttractionCommentService;
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
 * 景点评论表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttractionCommentServiceImpl extends ServiceImpl<AttractionCommentMapper, AttractionComment> implements AttractionCommentService {

    @Override
    public void addComment(AttractionComment attractionComment) {
        if (ObjectUtil.isEmpty(attractionComment)) {
            throw new BaseException("参数不全");
        }

        if (ObjectUtil.isEmpty(attractionComment.getAttractionId())) {
            throw new BaseException("景点id不能为空");
        }
        if (ObjectUtil.isEmpty(attractionComment.getCommentDetail())) {
            throw new BaseException("评论详情不能为空");
        }
        if (ObjectUtil.isEmpty(attractionComment.getPictureUrl())) {
            throw new BaseException("评论图片不能为空");
        }

        this.save(attractionComment);
    }

    @Override
    public void delById(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要删除的数据");
        }

        this.removeById(id);
    }

    @Override
    public IPage<AttractionComment> listPage(QueryFilter<AttractionComment> queryFilter) {
        // 获取分页条件
        IPage<AttractionComment> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        AttractionComment params = CommonUtil.getParams(queryFilter, AttractionComment.class);

        LambdaQueryWrapper<AttractionComment> wrapper = new LambdaQueryWrapper<>();

        // 查询条件
        wrapper.eq(ObjectUtil.isNotEmpty(params.getId()), AttractionComment::getId, params.getId())
                .eq(ObjectUtil.isNotEmpty(params.getAttractionId()), AttractionComment::getAttractionId, params.getAttractionId())
                .eq(ObjectUtil.isNotEmpty(params.getCommentDetail()), AttractionComment::getCommentDetail, params.getCommentDetail())
                .eq(ObjectUtil.isNotEmpty(params.getPictureUrl()), AttractionComment::getPictureUrl, params.getPictureUrl())
                .orderByDesc(AttractionComment::getCreateTime)
        ;

        // 关键字模糊搜索
        if (ObjectUtil.isNotEmpty(params.getKeyword())) {
            wrapper.and(w ->
                    w.like(AttractionComment::getAttractionId, params.getKeyword())
                            .or().like(AttractionComment::getCommentDetail, params.getKeyword())
                            .or().like(AttractionComment::getPictureUrl, params.getKeyword())
            );
        }

        IPage<AttractionComment> resp = this.page(page, wrapper);

        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }

        return resp;
    }

    @Override
    public AttractionComment getByIdPlus(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要查看的数据");
        }

        AttractionComment entity = this.getById(id);

        if (ObjectUtil.isNotEmpty(entity)) {
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public List<AttractionComment> getByAttractionId(String attractionId) {
        List<AttractionComment> list = this.lambdaQuery()
                .eq(AttractionComment::getAttractionId, attractionId)
                .orderByDesc(AttractionComment::getCreateTime)
                .list();
        this.convert(list);
        return list;
    }

    private void convert(List<AttractionComment> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, AttractionComment.class)
                    .lambdaAdd(AttractionComment::getCreator, User::getId, AttractionComment::getCreatorHead, User::getHeadPicUrl, User.class)
                    .done()
                    .convert();
        }
    }

    private void convert(AttractionComment entity) {
        List<AttractionComment> list = new ArrayList<>();
        list.add(entity);

        this.convert(list);
    }
}
