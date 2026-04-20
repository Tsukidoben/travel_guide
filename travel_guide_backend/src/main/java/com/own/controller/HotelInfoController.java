package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.model.AttractionInfo;
import com.own.model.HotelInfo;
import com.own.service.HotelInfoService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 酒店管理表
 * 控制层
 */
@RestController
@RequestMapping("/hotelInfo")
public class HotelInfoController {

    @Autowired
    private HotelInfoService hotelInfoService;

    /**
     * 新增/修改
     */
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@RequestBody HotelInfo hotelInfo) {
        hotelInfoService.saveOrUpdatePlus(hotelInfo);
        return ResultUtil.success("操作成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        hotelInfoService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        hotelInfoService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<HotelInfo> queryFilter) {
        return ResultUtil.<HotelInfo>returnPages(hotelInfoService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<HotelInfo>successWithData(hotelInfoService.getByIdPlus(id));
    }

    /**
     * 酒店推荐
     */
    @PostMapping("recommend")
    @IgnoreAuth
    public String recommend() {
        return ResultUtil.<List<HotelInfo>>successWithData(hotelInfoService.recommend());
    }
}
