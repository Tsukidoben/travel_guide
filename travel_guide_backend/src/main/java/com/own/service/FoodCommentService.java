package com.own.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.FoodComment;
import cn.y8e.common.vo.QueryFilter;

/**
 * 小吃评论表
 * 业务层
 */
public interface FoodCommentService extends IService<FoodComment> {

    void addComment(FoodComment foodComment);

    void delById(Long id);

    IPage<FoodComment> listPage(QueryFilter<FoodComment> queryFilter);
}
