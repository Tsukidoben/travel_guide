package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.own.model.FoodInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 小吃信息表 Mapper
 */
@Mapper
public interface FoodInfoMapper extends BaseMapper<FoodInfo> {
    
    /**
     * 分页查询 - 带关联查询（分类名称、店铺名称）
     */
    IPage<FoodInfo> selectPageWithJoin(Page<FoodInfo> page, @Param("params") FoodInfo params);
}
