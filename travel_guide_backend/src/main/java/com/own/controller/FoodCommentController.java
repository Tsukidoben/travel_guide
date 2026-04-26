package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import cn.y8e.common.vo.QueryFilter;
import com.own.common.annotation.IgnoreAuth;
import com.own.model.FoodComment;
import com.own.service.FoodCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 小吃评论表
 * 控制层
 */
@RestController
@RequestMapping("/api/food/comment")
public class FoodCommentController {

    @Autowired
    private FoodCommentService foodCommentService;

    /**
     * 新增评论
     */
    @PostMapping("/add")
    public String add(@RequestBody FoodComment foodComment) {
        foodCommentService.addComment(foodComment);
        return ResultUtil.success("评论成功");
    }

    /**
     * 分页查小吃评论
     */
    @PostMapping("/listPage")
    @IgnoreAuth
    public String listPage(@RequestBody(required = false) QueryFilter<FoodComment> queryFilter) {
        return ResultUtil.<FoodComment>returnPages(foodCommentService.listPage(queryFilter));
    }

    /**
     * 根据小吃ID查询评论列表
     */
    @PostMapping("/list")
    @IgnoreAuth
    public String list(@RequestBody(required = false) QueryFilter<FoodComment> queryFilter) {
        return ResultUtil.<FoodComment>returnPages(foodCommentService.listPage(queryFilter));
    }

    /**
     * 管理员删除评论
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        foodCommentService.delById(id);
        return ResultUtil.success("删除成功");
    }
}
