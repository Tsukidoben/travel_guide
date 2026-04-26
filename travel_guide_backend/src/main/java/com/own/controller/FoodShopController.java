package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import cn.y8e.common.vo.QueryFilter;
import com.own.common.annotation.IgnoreAuth;
import com.own.model.FoodShop;
import com.own.model.vo.DelVo;
import com.own.service.FoodShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小吃店铺表
 * 控制层
 */
@RestController
@RequestMapping("/api/food/shop")
public class FoodShopController {

    @Autowired
    private FoodShopService foodShopService;

    /**
     * 店铺分页列表
     */
    @PostMapping("/listPage")
    @IgnoreAuth
    public String listPage(@RequestBody(required = false) QueryFilter<FoodShop> queryFilter) {
        return ResultUtil.<FoodShop>returnPages(foodShopService.listPage(queryFilter));
    }

    /**
     * 店铺简单列表（不分页）
     */
    @PostMapping("/list")
    @IgnoreAuth
    public String list() {
        return ResultUtil.<List<FoodShop>>successWithData(foodShopService.list());
    }

    /**
     * 店铺详情
     */
    @PostMapping("/detail/{id}")
    @IgnoreAuth
    public String detail(@PathVariable("id") Long id) {
        return ResultUtil.<FoodShop>successWithData(foodShopService.getByIdPlus(id));
    }

    /**
     * 新增编辑店铺
     */
    @PostMapping("/saveOrUpdate")
    public String saveOrUpdate(@RequestBody FoodShop foodShop) {
        foodShopService.saveOrUpdatePlus(foodShop);
        return ResultUtil.success("操作成功");
    }

    /**
     * 删除店铺
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        foodShopService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除店铺
     */
    @PostMapping("/delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        foodShopService.delBatch(delVo);
        return ResultUtil.success("批量删除成功");
    }

    /**
     * 获取所有店铺列表（简单列表，用于下拉选择）
     */
    @PostMapping("/listSimple")
    @IgnoreAuth
    public String listSimple() {
        return ResultUtil.<List<FoodShop>>successWithData(foodShopService.listSimple());
    }
}
