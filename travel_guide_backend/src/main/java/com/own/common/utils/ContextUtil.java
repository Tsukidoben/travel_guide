package com.own.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.y8e.common.utils.AddressUtil;
import com.own.common.config.CustomYmlProperties;
import com.own.common.constant.SysConstant;
import com.own.common.vo.UserContext;
import com.own.mappers.UserMapper;
import com.own.model.User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
* 上下文获取
*/
@Component
@Slf4j
public class ContextUtil {


    private static final ThreadLocal<UserContext> USER_FACADE_THREAD_LOCAL = new ThreadLocal<>();

    public static String getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentUserName() {
        return getCurrentUser().getUserName();
    }

    public static String getCurrentUserRole() {
        return getCurrentUser().getUserRole();
    }

    public static String getCurrentUserAccount() {
        return getCurrentUser().getUserAccount();
    }

    public static UserContext getCurrentUser() {
        return getCurrentUser(false);
    }

    /**
     * 设置用户信息到ThreadLocal
     */
    public static void setUserInfo() {
        UserContext userContext = USER_FACADE_THREAD_LOCAL.get();

        if (ObjectUtil.isNotEmpty(userContext)) {
            return;
        }

        UserContext currentUser = getCurrentUserDb(false);
        USER_FACADE_THREAD_LOCAL.set(currentUser);
    }


    /**
     * 设置传入的用户信息到ThreadLocal
     */
    public static void setUserInfo(UserContext userContext) {
        UserContext userLocal = USER_FACADE_THREAD_LOCAL.get();

        if (ObjectUtil.isNotEmpty(userLocal)) {
        } else {
            USER_FACADE_THREAD_LOCAL.set(userContext);
        }

    }

    public static void destroy() {
        USER_FACADE_THREAD_LOCAL.remove();
    }

    /**
     * 结合jwt以及查询数据库获取当前用户信息
     *
     * @param isNeedPwd 是否需要密码
     * @return 用户信息
     */
    public static UserContext getCurrentUserDb(Boolean isNeedPwd) {
        UserContext resp = null;
        try {

            CustomYmlProperties prop = SpringUtil.getBean(CustomYmlProperties.class);
            UserMapper userMapper = SpringUtil.getBean(UserMapper.class);

            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String token = request.getHeader(SysConstant.TOKEN);
            if (ObjectUtil.isNotEmpty(token)) {
                String userId = Jwts.parser().setSigningKey(prop.getJwt().getKey()).parseClaimsJws(token).getBody().getId();
                User user = userMapper.selectById(userId);
                if (!isNeedPwd) {
                    user.setPassword(null);
                }

                if(ObjectUtil.isNotEmpty(user)){
                    resp = new UserContext();

                    resp.setUserId(user.getId())
                            .setUserName(user.getUserName())
                            .setUserAccount(user.getUserAccount())
                            .setUserRole(user.getUserRole());
                }
            }
        } catch (Exception e) {
            log.info("获取用户信息失败："+e);
        }

        if(ObjectUtil.isEmpty(resp)){
            resp = new UserContext();
            // 给默认值
            resp.setUserId(AddressUtil.getUserIp())
                    .setUserName(AddressUtil.getUserIp() + "-神秘用户")
                    .setUserPhone(AddressUtil.getUserIp())
                    .setUserAccount(AddressUtil.getUserIp());
        }

        return resp;
    }

    /**
     * 获取当前用户信息
     *
     * @param isNeedPwd 是否返回密码
     * @return 用户信息
     */
    public static UserContext getCurrentUser(Boolean isNeedPwd) {
        //拿ThreadLocal中的信息，没有则查库
        UserContext userContext = USER_FACADE_THREAD_LOCAL.get();

        if (ObjectUtil.isEmpty(userContext)) {
            userContext = getCurrentUserDb(isNeedPwd);
        }

        return userContext;
    }

    /**
     * 切换当前线程上下文的用户
     * @param newUserId 目标用户ID
     */
    public static void switchUser(String newUserId) {
        if (ObjectUtil.isEmpty(newUserId)) {
            log.warn("switchUser 失败：newUserId 为空");
            return;
        }

        try {
            UserMapper userMapper = SpringUtil.getBean(UserMapper.class);
            User user = userMapper.selectById(newUserId);

            if (ObjectUtil.isEmpty(user)) {
                log.warn("switchUser 失败：未找到用户 {}", newUserId);
                return;
            }

            UserContext newUser = new UserContext()
                    .setUserId(user.getId())
                    .setUserName(user.getUserName())
                    .setUserAccount(user.getUserAccount())
                    .setUserRole(user.getUserRole());

            USER_FACADE_THREAD_LOCAL.set(newUser);
            log.info("当前用户已切换为：{}", newUser.getUserName());
        } catch (Exception e) {
            log.error("切换用户失败：", e);
        }
    }
}
