package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import cn.y8e.common.vo.QueryFilter;
import com.own.model.FoodFavorite;
import com.own.service.FoodFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小吃收藏表
 * 控制层
 */
@RestController
@RequestMapping("/api/food/favorite")
public class FoodFavoriteController {

    @Autowired
    private FoodFavoriteService foodFavoriteService;

    /**
     * 收藏/取消收藏（同一用户同一小吃只能收藏一次）
     */
    @PostMapping("")
    public String toggleFavorite(@RequestBody FoodFavorite foodFavorite) {
        String result = foodFavoriteService.toggleFavorite(foodFavorite.getFoodId());
        return ResultUtil.success(result);
    }

    /**
     * 获取用户收藏小吃分页
     */
    @PostMapping("/listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<FoodFavorite> queryFilter) {
        return ResultUtil.<FoodFavorite>returnPages(foodFavoriteService.listPage(queryFilter));
    }

    /**
     * 获取用户收藏小吃列表
     */
    @PostMapping("/list")
    public String list() {
        return ResultUtil.<List<FoodFavorite>>successWithData(foodFavoriteService.list());
    }
}
