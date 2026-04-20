package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import cn.y8e.common.vo.QueryFilter;
import com.own.common.annotation.IgnoreAuth;
import com.own.model.User;
import com.own.model.vo.*;
import com.own.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户信息表
 * 控制层
 */
@RestController
@RequestMapping(value = "/user/", produces = "application/json;charset=utf-8")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 编辑用户信息
     */
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@RequestBody User user) {
        userService.saveOrUpdatePlus(user);
        return ResultUtil.success();
    }

    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        userService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        userService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<User> queryFilter) {
        return ResultUtil.<User>returnPages(userService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<User>successWithData(userService.getByIdPlus(id));
    }

    /**
     * 用户登录
     *
     * @return
     */
    @IgnoreAuth
    @PostMapping("login")
    public String login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        Map<String, Object> user = userService.login(loginVo, request);
        return ResultUtil.successWithData("登录成功", user);
    }

    /**
     * 重置登录次数
     * @param userId
     * @return
     */
    @PostMapping("resetLoginStatus/{userId}")
    public String resetLoginStatus(@PathVariable("userId") String userId) {
        userService.resetLoginStatus(userId);
        return ResultUtil.successWithData("重置登录次数成功");
    }

    @IgnoreAuth
    @PostMapping("register")
    public String register(@RequestBody User registerVo, HttpServletRequest request) {
        Map<String, Object> user = userService.register(registerVo, request);
        return ResultUtil.successWithData("注册成功", user);
    }

    /**
     * 退出登录
     */
    @PostMapping("logout")
    public String logout() {
        userService.logout();
        return ResultUtil.success("退出登录成功");
    }

    @PostMapping("changePwd")
    public String changePwd(@RequestBody PwdVo pwdVo) {
        userService.changePwd(pwdVo, 1);
        return ResultUtil.success();
    }

    @PostMapping("resetPwd")
    public String resetPwd(@RequestBody PwdVo pwdVo) {
        userService.changePwd(pwdVo, 2);
        return ResultUtil.success();
    }

    /**
     * 获取当前登录用户信息
     */
    @PostMapping("getCurrentUser")
    public String getCurrentUser() {
        return ResultUtil.successWithData(userService.getCurrentUser(), true);
    }
}

