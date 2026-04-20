package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.model.CarouselImage;
import com.own.service.CarouselImageService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理表
 * 控制层
 */
@RestController
@RequestMapping("/carouselImage")
public class CarouselImageController {

    @Autowired
    private CarouselImageService carouselImageService;

    /**
     * 新增/修改
     */
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@RequestBody CarouselImage carouselImage) {
        carouselImageService.saveOrUpdatePlus(carouselImage);
        return ResultUtil.success("操作成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        carouselImageService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        carouselImageService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<CarouselImage> queryFilter) {
        return ResultUtil.<CarouselImage>returnPages(carouselImageService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<CarouselImage>successWithData(carouselImageService.getByIdPlus(id));
    }

    /**
     * 用户端查询
     */
    @PostMapping("list")
    @IgnoreAuth
    public String list() {
        return ResultUtil.<List<CarouselImage>>successWithData(carouselImageService.listFront());
    }
}
