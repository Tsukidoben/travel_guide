package com.own.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.PageBean;
import cn.y8e.common.vo.QueryFilter;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
* 公用公举嘞
*/
@Slf4j
public class CommonUtil {
    /**
     * 根据pageBean获取ipage
     */
    public static <T> IPage<T> getPage(QueryFilter<T> queryFilter) {
        return getPage(queryFilter, null);
    }


    /**
     * 根据pageBean获取ipage
     */
    @SuppressWarnings("unused")
    public static <T> IPage<T> getPage(QueryFilter<?> queryFilter, Class<T> clazz) {
        IPage<T> page = new Page<>();
        if (ObjectUtil.isEmpty(queryFilter) || ObjectUtil.isEmpty(queryFilter.getPageBean())) {
            page.setCurrent(1);
            page.setSize(-1);
        } else {
            PageBean pageBean = queryFilter.getPageBean();
            page.setCurrent(ObjectUtil.isNotEmpty(pageBean.getPage()) ? pageBean.getPage() : 1);
            page.setSize(ObjectUtil.isNotEmpty(pageBean.getPageSize()) ? pageBean.getPageSize() : -1);
        }

        return page;
    }

    /**
     * 根据queryFilter获取查询参数
     */
    public static <T> T getParams(QueryFilter<T> queryFilter, Class<T> clazz) {
        if (ObjectUtil.isEmpty(queryFilter) || ObjectUtil.isEmpty(queryFilter.getParams())) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("无法创建对象", e);
            }
        }
        return queryFilter.getParams();
    }

    /**
     * 创建长度为n得9
     */
    public static Integer numberTo9(Integer n) {
        if (n <= 0) {
            throw new BaseException("参数必须大于0");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("9");
        }
        return Integer.parseInt(sb.toString());
    }

    /**
     * 从lambd表达式获取字段名
     */
    public static  <T> String parseSfunctionField(SFunction<T, ?> target) {
        String implMethodName = LambdaUtils.extract(target).getImplMethodName();

        if (StrUtil.isBlank(implMethodName)) {
            return null;
        }

        String fieldName = implMethodName.replaceFirst("get", "")
                .replaceFirst("is", "");
        fieldName = StrUtil.toCamelCase(fieldName);
        fieldName = StrUtil.lowerFirst(fieldName);
        return fieldName;
    }

    /**
     * 从类对象获取数据库字段名
     * @param func
     * @return
     * @param <T>
     */
    public static <T> String getDbColumnName(SFunction<T, ?> func) {
        LambdaMeta lambdaMeta = LambdaUtils.extract(func);
        // 获取对应的get方法名
        String methodName = lambdaMeta.getImplMethodName(); // getUsername

        // 获取对应的类名
        Class<?> instantiatedClass = lambdaMeta.getInstantiatedClass();

        // 获取具体名称
        String fieldName = methodName.replaceFirst("get", "")
                .replaceFirst("is", "");

        // 把第一个转换成小写字母
        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);

        if(ObjectUtil.isEmpty(fieldName)){
            log.info("[流水号生成工具]该项目不支持lambda新增，自动跳过");
            return null;
        }

        Field declaredField = null;
        try {
            declaredField = instantiatedClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            log.info("[流水号生成工具]该项目不支持lambda新增，自动跳过");
            return null;
        }

        TableId tableId = declaredField.getAnnotation(TableId.class);
        if(declaredField.isAnnotationPresent(TableId.class) && tableId != null){
            return tableId.value();
        }

        TableName annotation = declaredField.getAnnotation(TableName.class);
        if(!declaredField.isAnnotationPresent(TableName.class) || annotation == null || ObjectUtil.isEmpty(annotation.value())) {
            // 没有该字段，自动转换成下划线
            return StrUtil.toSymbolCase(fieldName, '_');
        }

        return annotation.value();
    }


    /**
     * 根据类获取数据库表名
     * @param tableClass 类
     */
    public static String getTableNameFromClass(Class<?> tableClass) {
        boolean annotationPresent = tableClass.isAnnotationPresent(TableName.class);
        TableName annotation = tableClass.getAnnotation(TableName.class);
        if(!annotationPresent || annotation == null){
            log.info("[流水号生成工具]该项不支持lambda式新增，自动跳过");
            return null;
        }
        return annotation.value();
    }

    /**
     * 对象转JSON
     */
    public static String toJson(Object obj, boolean includeNull){
        if(ObjectUtil.isNotEmpty(obj)){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectWriter writer;
                if (includeNull) {
                    writer = objectMapper.writer();
                } else {
                    writer = objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL).writer();
                }
                return writer.writeValueAsString(obj);
            }catch (Exception e) {
                throw new BaseException("json转换异常");
            }
        }else {
            return null;
        }
    }

    /**
     * 对象转JSON
     */
    public static String toJson(Object obj) {
        return toJson(obj, false);
    }
}
