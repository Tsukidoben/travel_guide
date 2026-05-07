package com.own.model.vo;

import lombok.Data;
import java.util.List;

/**
 * 每日行程VO
 */
@Data
public class ItineraryDay {

    /**
     * 第几天
     */
    private Integer dayNum;

    /**
     * 日期
     */
    private String date;

    /**
     * 当天的行程项列表
     */
    private List<ItineraryItem> items;
}
