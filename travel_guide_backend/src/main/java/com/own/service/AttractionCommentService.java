package com.own.service;

import com.own.model.AttractionComment;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;

import java.util.List;

/**
 * 景点评论表
 * 业务层
 */
public interface AttractionCommentService extends IService<AttractionComment> {
    void addComment(AttractionComment attractionComment);

    void delById(String id);

    IPage<AttractionComment> listPage(QueryFilter<AttractionComment> queryFilter);

    AttractionComment getByIdPlus(String id);

    List<AttractionComment> getByAttractionId(String attractionId);
}
