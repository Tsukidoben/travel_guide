package com.own.controller;

import com.own.model.TripStrategyComment;
import com.own.service.TripStrategyCommentService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 攻略评论表
 * 控制层
 */
@RestController
@RequestMapping("/tripStrategyComment")
public class TripStrategyCommentController {

    @Autowired
    private TripStrategyCommentService baseService;

    /**
     * 发布评论
     */
    @PostMapping("addComment")
    public String addComment(@RequestBody TripStrategyComment entity) {
        baseService.addComment(entity);
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
     * 根据攻略id查询评论
     */
    @PostMapping("getByTripStrategyId/{tripStrategyId}")
    public String getByTripStrategyId(@PathVariable("tripStrategyId") String tripStrategyId) {
        return ResultUtil.<List<TripStrategyComment>>successWithData(baseService.getByTripStrategyId(tripStrategyId));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<TripStrategyComment>successWithData(baseService.getByIdPlus(id));
    }
}
