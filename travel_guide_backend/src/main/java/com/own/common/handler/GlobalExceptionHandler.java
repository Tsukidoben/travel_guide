package com.own.common.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.ResultUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;

/**
* 全局异常处理器
*/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 自定义异常
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public String error(Exception e, HttpServletResponse response){
        log.error("encounter Exception:{}", e.getMessage());
        log.error(ExceptionUtil.stacktraceToString(e, -1));
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


        //数据库异常
        if (e instanceof SQLException || e instanceof MybatisPlusException || e.getCause() instanceof SQLException
                || e.getCause() instanceof MybatisPlusException || e instanceof MyBatisSystemException) {
            return ResultUtil.error("数据库操作失败");
        }


        //业务异常
        if (e instanceof BaseException) {
            return ResultUtil.error(((BaseException) e).getCode(), ((BaseException) e).getMsg());
        }


        if (e instanceof NoResourceFoundException) {
            return ResultUtil.error(404, StrUtil.format("无法找到路径 {}", ((NoResourceFoundException) e).getResourcePath()));
        }
        return ResultUtil.error("系统异常");

    }
}
