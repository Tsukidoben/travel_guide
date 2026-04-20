package com.own.service;

import cn.y8e.common.vo.QueryFilter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.User;
import com.own.model.vo.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 用户信息表
 * 业务层
 */
public interface UserService extends IService<User> {
    /**
     * 新增/修改用户信息
     */
    void saveOrUpdatePlus(User user);

    /**
     * 根据id删除用户
     */
    void delById(String id);

    /**
     * 批量删除
     */
    void delBatch(DelVo delVo);

    /**
     * 分页查询
     */
    IPage<User> listPage(QueryFilter<User> queryFilter);

    /**
     * 根据id获取用户信息
     */
    User getByIdPlus(String id);

    /**
     * 用户登录
     */
    Map<String, Object> login(LoginVo loginVo, HttpServletRequest request);

    /**
     * 修改密码
     * @param type 1 修改密码 2 重置密码
     */
    void changePwd(PwdVo pwdVo, Integer type);

    /**
     * 获取当前登录用户
     */
    User getCurrentUser();

    /**
    * 退出登录
    */
    void logout();

    /**
    * 用户注册
    */
    Map<String, Object> register(User registerVo, HttpServletRequest request);

    /**
    * 重置登录
    */
    void resetLoginStatus(String userId);

}
