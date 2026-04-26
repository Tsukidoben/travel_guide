package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.FoodCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 小吃分类表 Mapper
 */
@Mapper
public interface FoodCategoryMapper extends BaseMapper<FoodCategory> {
}
