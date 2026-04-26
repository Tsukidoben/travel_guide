package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import cn.y8e.common.vo.QueryFilter;
import com.own.common.annotation.IgnoreAuth;
import com.own.model.FoodInfo;
import com.own.model.vo.DelVo;
import com.own.service.FoodInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小吃信息表
 * 控制层
 */
@RestController
@RequestMapping("/api/food")
public class FoodInfoController {

    @Autowired
    private FoodInfoService foodInfoService;

    /**
     * 分页+名称模糊+分类筛选
     */
    @PostMapping("/listPage")
    @IgnoreAuth
    public String listPage(@RequestBody(required = false) QueryFilter<FoodInfo> queryFilter) {
        return ResultUtil.<FoodInfo>returnPages(foodInfoService.listPage(queryFilter));
    }

    /**
     * 小吃简单列表（不分页）
     */
    @PostMapping("/list")
    @IgnoreAuth
    public String list() {
        return ResultUtil.<List<FoodInfo>>successWithData(foodInfoService.list());
    }

    /**
     * 小吃详情（关联带出店铺信息）
     */
    @PostMapping("/detail/{id}")
    @IgnoreAuth
    public String detail(@PathVariable("id") Long id) {
        return ResultUtil.<FoodInfo>successWithData(foodInfoService.getByIdPlus(id));
    }

    /**
     * 后台新增编辑小吃
     */
    @PostMapping("/saveOrUpdate")
    public String saveOrUpdate(@RequestBody FoodInfo foodInfo) {
        foodInfoService.saveOrUpdatePlus(foodInfo);
        return ResultUtil.success("操作成功");
    }

    /**
     * 删除小吃
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        foodInfoService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除小吃
     */
    @PostMapping("/delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        foodInfoService.delBatch(delVo);
        return ResultUtil.success("批量删除成功");
    }

    /**
     * 上下架
     */
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestBody FoodInfo foodInfo) {
        foodInfoService.updateStatus(foodInfo.getId(), foodInfo.getStatus());
        return ResultUtil.success(foodInfo.getStatus() == 1 ? "上架成功" : "下架成功");
    }

    /**
     * 设置推荐
     */
    @PostMapping("/updateRecommend")
    public String updateRecommend(@RequestBody FoodInfo foodInfo) {
        foodInfoService.updateRecommend(foodInfo.getId(), foodInfo.getIsRecommend());
        return ResultUtil.success(foodInfo.getIsRecommend() == 1 ? "已设为首页推荐" : "已取消推荐");
    }
}
