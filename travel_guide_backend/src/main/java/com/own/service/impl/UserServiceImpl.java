package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.y8e.common.constant.ResultConstant;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.MD5Util;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.vo.QueryFilter;
import com.own.model.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.config.CustomYmlProperties;
import com.own.common.constant.SysConstant;
import com.own.common.utils.CommonUtil;
import com.own.common.utils.ContextUtil;
import com.own.common.utils.CreateImgUtil;
import com.own.common.utils.JwtUtil;
import com.own.mappers.UserMapper;
import com.own.model.User;
import com.own.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.*;

/**
 * 用户信息表
 * 业务层实现类
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private CustomYmlProperties customYmlProperties;

    /**
     * 用于存储每个用户的登录信息，key为用户id 值为用户
     */
    private static final Map<String, LoginStatusVo> loginStatusMap = new HashMap<>();

    @Override
    public void saveOrUpdatePlus(User user) {

        this.saveOrUpdateBefore(user);

        if (ObjectUtil.isNotEmpty(user.getId())) {
            // 修改
            if (ObjectUtil.isEmpty(user.getHeadPicUrl())) {
                user.setHeadPicUrl(CreateImgUtil.createImgByName(user.getUserName(), 2, "back"));
            }
            // 删除密码
            user.setPassword(null);

            this.updateById(user);

        } else {
            // 新增用户信息
            if (ObjectUtil.isEmpty(user.getHeadPicUrl())) {
                user.setHeadPicUrl(CreateImgUtil.createImgByName(user.getUserName(), 2, "back"));
            }
            // 如果没有密码则默认密码
            String password = ObjectUtil.isNotEmpty(user.getPassword()) ? user.getPassword() : SysConstant.DEFAULT_PWD;

            user.setPassword(MD5Util.generateMD5(password));

            if (ObjectUtil.isEmpty(user.getUserRole())) {
                user.setUserRole("0");
            }

            this.save(user);
        }
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    @Override
    public void delById(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择需要删除的用户");
        }
        User user = this.getById(id);
        if (ObjectUtil.isEmpty(user)) {
            throw new BaseException("用户不存在");
        }
        this.removeById(id);
    }

    @Override
    public void delBatch(DelVo delVo) {
        if(ObjectUtil.isEmpty(delVo.getIds())){
            throw new BaseException("请选择要删除的数据");
        }

        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public IPage<User> listPage(QueryFilter<User> queryFilter) {
        // 获取分页条件
        IPage<User> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        User params = CommonUtil.getParams(queryFilter, User.class);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ObjectUtil.isNotEmpty(params.getUserRole()), User::getUserRole, params.getUserRole());

        wrapper
            .like(ObjectUtil.isNotEmpty(params.getUserName()), User::getUserName, params.getUserName())            .like(ObjectUtil.isNotEmpty(params.getUserAccount()), User::getUserAccount, params.getUserAccount())            .eq(ObjectUtil.isNotEmpty(params.getUserRole()), User::getUserRole, params.getUserRole())            .like(ObjectUtil.isNotEmpty(params.getUserPhone()), User::getUserPhone, params.getUserPhone());

        // 排序
        wrapper.orderByDesc(User::getCreateTime);


        IPage<User> resp = this.page(page, wrapper);

        if(ObjectUtil.isNotEmpty(resp.getRecords())){
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }

        return resp;
    }

    @Override
    public User getByIdPlus(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要查看的数据");
        }

        User entity = this.getById(id);

        if(ObjectUtil.isNotEmpty(entity)){
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    private void convert(List<User> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, User.class)
                .done()
                .convert();
        }
    }

    private void convert(User entity) {
        List<User> list = new ArrayList<>();
        list.add(entity);

        this.convert(list);
    }

    // ===================其他接口==================

    /**
     * 登录
     */
    @Override
    public Map<String, Object> login(LoginVo loginVo, HttpServletRequest request) {
        // 登录前检查
        this.loginBefore(loginVo, request);
        // 查询用户信息
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(true, User::getUserAccount, loginVo.getLoginAccount()));

        if (ObjectUtil.isEmpty(user)) {
            throw new BaseException("用户名或密码错误");
        }

        // 判断能否进行登录
        this.isCanLogin(user, request);

        // 检验加密后的密码是否一致
        String md5WithSalt = MD5Util.generateMD5(loginVo.getPassword());

        if (!user.getPassword().equals(md5WithSalt)) {
            this.LoginError(user, request);

            throw new BaseException("用户名或密码错误");
        }

        // 验证成功，生成token
        String token = JwtUtil.createToken(user.getId(), user.getUserName());

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        user.setPassword(null);
        resp.put("userInfo", user);

        this.loginSuccess(user, request);

        return resp;
    }

    /**
     * 是否允许登录
     */
    private void isCanLogin(User user, HttpServletRequest request) {
        // 有用户过后判断当前用户登录次数是否超过设置
        if (customYmlProperties.getLogin().getErrorMaxCount() != -1) {
            // 等于-1则不做限制
            // 获取当前登录用户数据
            LoginStatusVo loginStatusVo = loginStatusMap.get(user.getId());
            // 为空的话说明今天是第一次登录，则不进行特殊处理
            if (ObjectUtil.isNotEmpty(loginStatusVo)) {
                // 如果错误次数超过设置的最大次数且和上次登录失败时间为同一天
                if (loginStatusVo.getErrorCount() >= customYmlProperties.getLogin().getErrorMaxCount()
                        && loginStatusVo.getLastErrorTime().equals(LocalDate.now())
                ) {
                    throw new BaseException("错误次数过多，请明天再试");
                }
            }
        }
    }

    /**
     * 登录失败回调
     */
    private void LoginError(User user, HttpServletRequest request) {
        // 有用户过后判断当前用户登录次数是否超过设置
        if (customYmlProperties.getLogin().getErrorMaxCount() != -1) {
            // 等于-1则不做限制
            // 获取当前登录用户数据
            LoginStatusVo loginStatusVo = loginStatusMap.get(user.getId());
            if (ObjectUtil.isEmpty(loginStatusVo)) {
                // 为空则赋值错误次数为0
                loginStatusVo = new LoginStatusVo();
                loginStatusVo.setUserId(user.getId())
                        .setErrorCount(0)
                        .setLastErrorTime(LocalDate.now());
            }

            loginStatusVo.setUserId(user.getId())
                    .setErrorCount(loginStatusVo.getErrorCount() + 1)
                    .setLastErrorTime(LocalDate.now());

            loginStatusMap.put(user.getId(), loginStatusVo);
            log.info("登录失败,当前loginStatusMap：" + loginStatusVo);
        }
    }

    /**
     * 登录成功回调
     *
     * @param user
     * @param request
     */
    private void loginSuccess(User user, HttpServletRequest request) {
        // 有用户过后判断当前用户登录次数是否超过设置
        if (customYmlProperties.getLogin().getErrorMaxCount() != -1) {
            // 等于-1则不做限制
            // 获取当前登录用户数据
            LoginStatusVo loginStatusVo = loginStatusMap.get(user.getId());
            if (ObjectUtil.isEmpty(loginStatusVo)) {
                // 为空则赋值错误次数为0
                loginStatusVo = new LoginStatusVo();
            }

            loginStatusVo.setUserId(user.getId())
                    .setErrorCount(0)
                    .setLastErrorTime(LocalDate.now());

            loginStatusMap.put(user.getId(), loginStatusVo);
            log.info("登录成功,当前loginStatusMap：" + loginStatusVo);
        }
    }

    /**
     * 修改/重置密码
     *
     * @param pwdVo
     * @param type  1 修改密码 2 重置密码
     */
    @Override
    public void changePwd(PwdVo pwdVo, Integer type) {
        if (ObjectUtil.isEmpty(pwdVo)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isNotEmpty(type) && type == 1) {
            // 修改密码逻辑
            this.sureChangePwd(pwdVo);
        } else if (ObjectUtil.isNotEmpty(type)) {
            // 重置密码逻辑
            this.resetPwd(pwdVo.getUserId());
        } else {
            throw new BaseException("请选择修改密码或重置密码");
        }
    }

    @Override
    public User getCurrentUser() {
        User currentUser = this.getById(ContextUtil.getCurrentUserId());
        if (ObjectUtil.isEmpty(currentUser)) {
            throw new BaseException(ResultConstant.NO_LOGIN, "请先登录");
        }

        return currentUser;
    }

    @Override
    public void logout() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 作废token
        JwtUtil.invalidateToken(request.getHeader(SysConstant.TOKEN));
    }

    @Override
    public Map<String, Object> register(User user, HttpServletRequest request) {
        this.saveOrUpdateBefore(user);
        String oldPwd = user.getPassword();

        // 如果密码为空则默认
        String password = ObjectUtil.isNotEmpty(user.getPassword()) ? user.getPassword() : SysConstant.DEFAULT_PWD;

        user.setPassword(MD5Util.generateMD5(password));

        if (ObjectUtil.isEmpty(user.getHeadPicUrl())) {
            user.setHeadPicUrl(CreateImgUtil.createImgByName(user.getUserName(), 2, "back"));
        }

        this.save(user);

        // 自动登录
        LoginVo loginVo = new LoginVo();
        loginVo.setLoginAccount(user.getUserAccount())
                .setPassword(oldPwd);


        return this.login(loginVo, request);
    }

    @Override
    public void resetLoginStatus(String userId) {
        if (customYmlProperties.getLogin().getErrorMaxCount() != -1) {
            // 等于-1则不做限制
            // 获取当前登录用户数据
            LoginStatusVo loginStatusVo = loginStatusMap.get(userId);
            if (ObjectUtil.isEmpty(loginStatusVo)) {
                // 为空则赋值错误次数为0
                loginStatusVo = new LoginStatusVo();
            }

            loginStatusVo.setUserId(userId)
                    .setErrorCount(0)
                    .setLastErrorTime(LocalDate.now());

            loginStatusMap.put(userId, loginStatusVo);
            log.info("登录成功,当前loginStatusMap：" + loginStatusVo);
        }
    }


    /**
     * 重置密码
     */
    private void resetPwd(String userId) {
        if (ObjectUtil.isEmpty(userId)) {
            throw new BaseException("请选择重置密码用户");
        }
        User user = this.getById(userId);
        user.setPassword(MD5Util.generateMD5(SysConstant.DEFAULT_PWD));

        this.updateById(user);
    }

    /**
     * 修改密码逻辑
     *
     * @param pwdVo
     */
    private void sureChangePwd(PwdVo pwdVo) {
        // type =1 修改密码 type =2 重置密码
        User currentUser = ObjectUtil.isNotEmpty(pwdVo.getUserId()) ? this.getById(pwdVo.getUserId()) : this.getById(ContextUtil.getCurrentUserId());

        // 判断旧密码是否相等
        // 检验加密后的密码是否一致
        String md5WithSalt = MD5Util.generateMD5(pwdVo.getOldPwd());

        if (!currentUser.getPassword().equals(md5WithSalt)) {
            throw new BaseException("旧密码错误");
        }
        // 判断新密码
        if (ObjectUtil.isEmpty(pwdVo.getNewPwd()) || ObjectUtil.isEmpty(pwdVo.getRepeatPwd())) {
            throw new BaseException("新密码不能为空");
        }

        if (!pwdVo.getNewPwd().equals(pwdVo.getRepeatPwd())) {
            throw new BaseException("新密码和二次重复密码不相同");
        }
        // 修改密码操作
        currentUser.setPassword(MD5Util.generateMD5(pwdVo.getNewPwd()));

        this.updateById(currentUser);
    }

    /**
     * 保存前置
     */
    private void saveOrUpdateBefore(User user) {
        if (ObjectUtil.isEmpty(user)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(user.getUserName())) {
            throw new BaseException("用户名不能为空");
        }

        if (ObjectUtil.isEmpty(user.getUserRole())) {
            user.setUserRole("2");
        }

        if (ObjectUtil.isNotEmpty(user.getUserAccount()) && ObjectUtil.isNotEmpty(this.list(new LambdaQueryWrapper<User>()
                .eq(true, User::getUserAccount, user.getUserAccount())
                .ne(ObjectUtil.isNotEmpty(user.getId()), User::getId, user.getId())))) {
            // 查询账号是否重复
            throw new BaseException("用户账号重复");
        }
    }

    /**
     * 登录校验
     */
    private void loginBefore(LoginVo loginVo, HttpServletRequest request) {
        if (ObjectUtil.isEmpty(loginVo) || ObjectUtil.isEmpty(loginVo.getLoginAccount()) || ObjectUtil.isEmpty(loginVo.getPassword())) {
            throw new BaseException("登录信息不全，请检查账号密码");
        }
    }
}
