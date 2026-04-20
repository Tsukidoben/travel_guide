package com.own.controller;

import com.own.model.AttractionCollection;
import com.own.service.AttractionCollectionService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点收藏表
 * 控制层
 */
@RestController
@RequestMapping("/attractionCollection")
public class AttractionCollectionController {

    @Autowired
    private AttractionCollectionService baseService;

    /**
     * 收藏
     */
    @PostMapping("collect/{attractionId}")
    public String collect(@PathVariable("attractionId") String attractionId) {
        baseService.collect(attractionId);
        return ResultUtil.success("收藏成功");
    }

    /**
     * 取消收藏
     */
    @PostMapping("noCollect/{attractionId}")
    public String noCollect(@PathVariable("attractionId") String attractionId) {
        baseService.noCollect(attractionId);
        return ResultUtil.success("取消收藏成功");
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
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        baseService.delByBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 我的收藏
     */
    @PostMapping("myCollect")
    public String myCollect() {
        return ResultUtil.<List<AttractionCollection>>successWithData(baseService.myCollect());
    }
}
