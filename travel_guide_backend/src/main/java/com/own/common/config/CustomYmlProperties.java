package com.own.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 自定义参数
 */
@Component
@Data
@ConfigurationProperties(prefix = "com.own.properties")
public class CustomYmlProperties {
    private String uploadType;
    private LoginInfo login;
    private jwt jwt;


    @Data
    public static class LoginInfo{
        private Boolean isNeedCaptcha;
        // 登录运行错误最大次数 -1 则不限制
        private Integer errorMaxCount;
    }

    @Data
    public static class jwt{
        private String key;
        private Long expireTime;
    }
}
