package com.own.common.interceptor;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.own.common.utils.ContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息拦截器
 */
@Slf4j
@Component
public class UserInfoInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            ContextUtil.setUserInfo();
        } catch (Exception e) {
            log.error("获取用户信息出错并且拼装默认信息出错");
            log.error(ExceptionUtil.stacktraceToString(e, -1));
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //清除自定义ThreadLocal
        ContextUtil.destroy();
    }
}
