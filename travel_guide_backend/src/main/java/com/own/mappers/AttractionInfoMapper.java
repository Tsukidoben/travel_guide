package com.own.mappers;

import com.own.model.AttractionInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点信息表
 * Mapper（Dao）持久层
 */
@Mapper
public interface AttractionInfoMapper extends BaseMapper<AttractionInfo> {

}
