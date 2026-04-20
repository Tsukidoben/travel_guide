package com.own.controller;

import com.own.model.AttractionComment;
import com.own.model.TripStrategyComment;
import com.own.service.AttractionCommentService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点评论表
 * 控制层
 */
@RestController
@RequestMapping("/attractionComment")
public class AttractionCommentController {

    @Autowired
    private AttractionCommentService baseService;

    /**
     * 发布评论
     */
    @PostMapping("addComment")
    public String addComment(@RequestBody AttractionComment attractionComment) {
        baseService.addComment(attractionComment);
        return ResultUtil.success("发布成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        baseService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<AttractionComment> queryFilter) {
        return ResultUtil.<AttractionComment>returnPages(baseService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<AttractionComment>successWithData(baseService.getByIdPlus(id));
    }

    /**
     * 根据景点id查询评论
     */
    @PostMapping("getByAttractionId/{attractionId}")
    public String getByAttractionId(@PathVariable("attractionId") String attractionId) {
        return ResultUtil.<List<AttractionComment>>successWithData(baseService.getByAttractionId(attractionId));
    }
}
