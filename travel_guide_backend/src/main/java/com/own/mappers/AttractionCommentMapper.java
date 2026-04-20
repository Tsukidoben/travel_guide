package com.own.mappers;

import com.own.model.AttractionComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点评论表
 * Mapper（Dao）持久层
 */
@Mapper
public interface AttractionCommentMapper extends BaseMapper<AttractionComment> {

}
