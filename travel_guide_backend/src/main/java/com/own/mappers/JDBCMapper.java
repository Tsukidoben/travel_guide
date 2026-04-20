package com.own.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedHashMap;
import java.util.List;


@Mapper
public interface JDBCMapper {

    @Select("${sql}")
    List<LinkedHashMap<String, Object>> runSql(@Param("sql") String sql);
}
