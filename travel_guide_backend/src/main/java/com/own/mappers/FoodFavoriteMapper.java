package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.FoodFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 小吃收藏表 Mapper
 */
@Mapper
public interface FoodFavoriteMapper extends BaseMapper<FoodFavorite> {
}
