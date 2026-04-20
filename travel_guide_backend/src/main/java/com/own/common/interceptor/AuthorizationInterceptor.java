package com.own.common.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.y8e.common.constant.ResultConstant;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.ResultUtil;
import com.own.common.annotation.IgnoreAuth;
import com.own.common.config.CustomYmlProperties;
import com.own.common.constant.SysConstant;
import com.own.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.List;

/**
 * 权限(Token)验证
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomYmlProperties customYmlProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //支持跨域请求
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,request-source,Token, Origin,imgType, Content-Type, cache-control,postman-token,Cookie, Accept,authorization");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return true;
        }

        IgnoreAuth annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IgnoreAuth.class);
        } else {
            return true;
        }

        // 如果加了忽略验证注解直接通过
        if (annotation != null) {
            return true;
        }

        //从header中获取token
        String token = request.getHeader(SysConstant.TOKEN);

        if (ObjectUtil.isNotEmpty(token)) {
            int verify = jwtUtil.verify(token);
            if(verify == 1){
                return true;
            }
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(ResultUtil.error(ResultConstant.NO_LOGIN, "请先登录"));
        }
        return false;
    }

    private static boolean containsInArray(String uri, List<String> array) {
        for (String delItem : array) {
            if (uri.contains(delItem)) {
                return true;
            }
        }
        return false;
    }
}
