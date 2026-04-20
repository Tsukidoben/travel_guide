package com.own.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.own.common.constant.FileConstant;
import com.own.common.interceptor.AuthorizationInterceptor;
import com.own.common.interceptor.UserInfoInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
* 拦截器配置类
*/
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    /**
     * 用户验证
     */
    @Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        converters.add(mappingJackson2HttpMessageConverter());
    }


    /**
     * 解决序列化空对象问题
     *
     * @return 序列化转换器
     */
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //*在传入的参数中匹配不上接收类的字段时忽略
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new MappingJackson2HttpMessageConverter(mapper);
    }

    /**
     * 用户信息
     */
    @Bean
    public UserInfoInterceptor getUserInfoInterceptor() {
        return new UserInfoInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthorizationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/static/**", FileConstant.FILE_REFLEXFOLDER + "**");
        registry.addInterceptor(getUserInfoInterceptor());
    }

    /**
     * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler(FileConstant.FILE_REFLEXFOLDER + "**")
                .addResourceLocations(ResourceUtils.FILE_URL_PREFIX + FileConstant.FILE_REALFOLDER);
    }
}
