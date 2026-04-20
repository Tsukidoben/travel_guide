package com.own.controller;

import com.own.model.TicketInfo;
import com.own.service.TicketInfoService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门票管理表
 * 控制层
 */
@RestController
@RequestMapping("/ticketInfo")
public class TicketInfoController {

    @Autowired
    private TicketInfoService ticketInfoService;

    /**
     * 新增/修改
     */
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@RequestBody TicketInfo ticketInfo) {
        ticketInfoService.saveOrUpdatePlus(ticketInfo);
        return ResultUtil.success("操作成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        ticketInfoService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        ticketInfoService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<TicketInfo> queryFilter) {
        return ResultUtil.<TicketInfo>returnPages(ticketInfoService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<TicketInfo>successWithData(ticketInfoService.getByIdPlus(id));
    }

    /**
     * 根据景点id查询对应门票信息
     */
    @PostMapping("getByAttractionId/{attractionId}")
    public String getByAttractionId(@PathVariable("attractionId") String attractionId) {
        return ResultUtil.<List<TicketInfo>>successWithData(ticketInfoService.getByAttractionId(attractionId));
    }
}
