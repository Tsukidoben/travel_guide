package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.FoodComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 小吃评论表 Mapper
 */
@Mapper
public interface FoodCommentMapper extends BaseMapper<FoodComment> {
}
