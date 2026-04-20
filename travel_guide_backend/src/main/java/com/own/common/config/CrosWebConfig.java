package com.own.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
* 跨域配置
*/
@Configuration
public class CrosWebConfig {
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        CorsConfiguration cors=new CorsConfiguration();
        //允许验证信息跨域
        cors.setAllowCredentials(true);
        //允许哪些请求跨域
        cors.addAllowedOriginPattern("*");
        //允许哪些头信息跨域
        cors.addAllowedHeader("*");
        //允许哪些方法跨域
        cors.addAllowedMethod("*");
        source.registerCorsConfiguration("/**",cors);

        return new CorsFilter(source);

    }
}
