package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.model.AttractionType;
import com.own.service.AttractionTypeService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理表
 * 控制层
 */
@RestController
@RequestMapping("/attractionType")
public class AttractionTypeController {

    @Autowired
    private AttractionTypeService attractionTypeService;

    /**
     * 新增/修改
     */
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@RequestBody AttractionType attractionType) {
        attractionTypeService.saveOrUpdatePlus(attractionType);
        return ResultUtil.success("操作成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        attractionTypeService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        attractionTypeService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<AttractionType> queryFilter) {
        return ResultUtil.<AttractionType>returnPages(attractionTypeService.listPage(queryFilter));
    }

    /**
     * 用户端查询
     */
    @PostMapping("list")
    @IgnoreAuth
    public String list() {
        return ResultUtil.<List<AttractionType>>successWithData(attractionTypeService.listFront());
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<AttractionType>successWithData(attractionTypeService.getByIdPlus(id));
    }
}
