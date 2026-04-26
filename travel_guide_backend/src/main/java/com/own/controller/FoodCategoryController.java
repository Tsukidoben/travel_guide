package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import cn.y8e.common.vo.QueryFilter;
import com.own.common.annotation.IgnoreAuth;
import com.own.model.FoodCategory;
import com.own.model.vo.DelVo;
import com.own.service.FoodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小吃分类表
 * 控制层
 */
@RestController
@RequestMapping("/api/food/category")
public class FoodCategoryController {

    @Autowired
    private FoodCategoryService foodCategoryService;

    /**
     * 查询所有分类
     */
    @PostMapping("/list")
    @IgnoreAuth
    public String list() {
        return ResultUtil.<List<FoodCategory>>successWithData(foodCategoryService.listAll());
    }

    /**
     * 分页查询分类
     */
    @PostMapping("/listPage")
    @IgnoreAuth
    public String listPage(@RequestBody(required = false) QueryFilter<FoodCategory> queryFilter) {
        return ResultUtil.<FoodCategory>returnPages(foodCategoryService.listPage(queryFilter));
    }

    /**
     * 新增/编辑分类
     */
    @PostMapping("/saveOrUpdate")
    public String saveOrUpdate(@RequestBody FoodCategory foodCategory) {
        foodCategoryService.saveOrUpdatePlus(foodCategory);
        return ResultUtil.success("操作成功");
    }

    /**
     * 删除分类
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        foodCategoryService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除分类
     */
    @PostMapping("/delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        foodCategoryService.delBatch(delVo);
        return ResultUtil.success("批量删除成功");
    }
}
