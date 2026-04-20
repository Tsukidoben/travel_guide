package com.own.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EchartMapper {
    List<Map<String, Object>> adminBoardData();

    List<Map<String, String>> typeAttractionOrder();

    List<Map<String, String>> adminRecent7Days();

    List<Map<String, Object>> boardData(@Param("userId") String userId);

    List<Map<String, String>> hotelTop5(@Param("userId") String userId);

    List<Map<String, String>> recent7Days(@Param("userId") String userId);
}
