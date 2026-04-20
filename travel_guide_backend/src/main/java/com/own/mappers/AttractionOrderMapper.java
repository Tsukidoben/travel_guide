package com.own.mappers;

import com.own.model.AttractionOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点订单表
 * Mapper（Dao）持久层
 */
@Mapper
public interface AttractionOrderMapper extends BaseMapper<AttractionOrder> {

}
