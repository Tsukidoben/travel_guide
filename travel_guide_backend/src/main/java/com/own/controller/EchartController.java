package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import com.own.common.utils.ContextUtil;
import com.own.mappers.EchartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/echart")
public class EchartController {

    @Autowired
    private EchartMapper echartMapper;

    // 管理员------------------------start------------------------------
    /**
     * 看板，总订单量，今日订单量，总流水、今日流水
     */
    @PostMapping("/adminBoardData")
    public String adminBoardData() {
        return ResultUtil.<List<Map<String, Object>>>successWithData(echartMapper.adminBoardData());
    }

    /**
     * 不同分类景点订单数量占比图（饼图）
     */
    @PostMapping("/typeAttractionOrder")
    public String typeAttractionOrder() {
        return ResultUtil.<List<Map<String, String>>>successWithData(echartMapper.typeAttractionOrder());
    }

    /**
     * 最近7日销售额趋势图（折线图）
     */
    @PostMapping("/adminRecent7Days")
    public String adminRecent7Days() {
        return ResultUtil.<List<Map<String, String>>>successWithData(echartMapper.adminRecent7Days());
    }

    // 商家------------------------start------------------------------

    /**
     * 看板，总订单量，今日订单量，总流水、今日流水
     */
    @PostMapping("/boardData")
    public String boardData() {
        return ResultUtil.<List<Map<String, Object>>>successWithData(echartMapper.boardData(ContextUtil.getCurrentUserId()));
    }
    /**
     * 订单数量最多的前5个酒店（垂直柱状图）
     */
    @PostMapping("/hotelTop5")
    public String hotelTop5() {
        return ResultUtil.<List<Map<String, String>>>successWithData(echartMapper.hotelTop5(ContextUtil.getCurrentUserId()));
    }

    /**
     * 最近7日订单金额趋势图（折线图）
     */
    @PostMapping("/recent7Days")
    public String recent7Days() {
        return ResultUtil.<List<Map<String, String>>>successWithData(echartMapper.recent7Days(ContextUtil.getCurrentUserId()));
    }
}
