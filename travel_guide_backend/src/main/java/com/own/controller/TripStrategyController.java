package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.model.AttractionInfo;
import com.own.model.TripStrategy;
import com.own.service.TripStrategyService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 攻略管理表
 * 控制层
 */
@RestController
@RequestMapping("/tripStrategy")
public class TripStrategyController {

    @Autowired
    private TripStrategyService tripStrategyService;

    /**
     * 发布攻略
     */
    @PostMapping("addNew")
    public String addNew(@RequestBody TripStrategy tripStrategy) {
        tripStrategyService.addNew(tripStrategy);
        return ResultUtil.success("发布成功，等待审核中");
    }

    /**
     * 修改攻略
     */
    @PostMapping("update")
    public String update(@RequestBody TripStrategy tripStrategy) {
        tripStrategyService.updatePlus(tripStrategy);
        return ResultUtil.success("修改成功");
    }

    /**
     * 审核通过
     */
    @PostMapping("agree/{id}")
    public String agree(@PathVariable("id") String id) {
        tripStrategyService.agree(id);
        return ResultUtil.success("审核完成");
    }

    /**
     * 审核不通过
     */
    @PostMapping("noAgree")
    public String noAgree(@RequestBody TripStrategy tripStrategy) {
        tripStrategyService.noAgree(tripStrategy);
        return ResultUtil.success("审核完成");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        tripStrategyService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        tripStrategyService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<TripStrategy> queryFilter) {
        return ResultUtil.<TripStrategy>returnPages(tripStrategyService.listPage(queryFilter));
    }

    /**
     * 用户端查询
     */
    @PostMapping("list")
    @IgnoreAuth
    public String list(@RequestBody(required = false) QueryFilter<TripStrategy> queryFilter) {
        return ResultUtil.<TripStrategy>returnPages(tripStrategyService.listFront(queryFilter));
    }

    /**
     * 我的攻略
     */
    @PostMapping("myTripStrategies")
    public String myTripStrategies(@RequestBody(required = false) QueryFilter<TripStrategy> queryFilter) {
        return ResultUtil.<TripStrategy>returnPages(tripStrategyService.myTripStrategies(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    @IgnoreAuth
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<TripStrategy>successWithData(tripStrategyService.getByIdPlus(id));
    }

    /**
     * 攻略推荐
     */
    @PostMapping("recommend")
    @IgnoreAuth
    public String recommend() {
        return ResultUtil.<List<TripStrategy>>successWithData(tripStrategyService.recommend());
    }
}
