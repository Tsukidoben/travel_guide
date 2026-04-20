package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.model.AttractionInfo;
import com.own.service.AttractionInfoService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点信息表
 * 控制层
 */
@RestController
@RequestMapping("/attractionInfo")
public class AttractionInfoController {

    @Autowired
    private AttractionInfoService attractionInfoService;

    /**
     * 新增/修改
     */
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@RequestBody AttractionInfo attractionInfo) {
        attractionInfoService.saveOrUpdatePlus(attractionInfo);
        return ResultUtil.success("操作成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        attractionInfoService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        attractionInfoService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    @IgnoreAuth
    public String listPage(@RequestBody(required = false) QueryFilter<AttractionInfo> queryFilter) {
        return ResultUtil.<AttractionInfo>returnPages(attractionInfoService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    @IgnoreAuth
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<AttractionInfo>successWithData(attractionInfoService.getByIdPlus(id));
    }

    /**
     * 景点推荐
     */
    @PostMapping("recommend")
    @IgnoreAuth
    public String recommend() {
        return ResultUtil.<List<AttractionInfo>>successWithData(attractionInfoService.recommend());
    }
}
