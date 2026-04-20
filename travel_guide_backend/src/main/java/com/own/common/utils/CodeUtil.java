package com.own.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.y8e.common.exception.BaseException;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.own.mappers.JDBCMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 流水号自动生成工具
 */
@Slf4j
public class CodeUtil {
    /**
     * 生成流水号
     * @param codePrefix 前缀
     * @param codeNumLength 长度
     * @param table 表名
     * @param codeField 需要的字段名
     * @param isStartOne 是否从1开始
     */
    public static String generateCode(String codePrefix, Integer codeNumLength, String table, String codeField,Boolean isStartOne) {
        JDBCMapper jdbcMapper = SpringUtil.getBean(JDBCMapper.class);

        String stringBuilder = "select max(" +
                codeField +
                ") as maxCode from " +
                table +
                " where " +
                codeField +
                " like '" +
                codePrefix +
                "%'";

        List<LinkedHashMap<String, Object>> linkedHashMaps = jdbcMapper.runSql(stringBuilder);

        int num = 1;

        if (ObjectUtil.isNotEmpty(linkedHashMaps) && ObjectUtil.isNotEmpty(linkedHashMaps.get(0))) {
            Map<String, Object> maxCodeMap = linkedHashMaps.get(0);
            int temp = Integer.parseInt(maxCodeMap.get("maxCode").toString().substring(codePrefix.length()));

            if (temp == CommonUtil.numberTo9(codeNumLength)) {
                throw new BaseException("编码预留数量不足，请扩容");
            }

            num = temp + 1;
        }

        if(!isStartOne && num == 1){
            num = RandomUtil.randomInt(0, CommonUtil.numberTo9(codeNumLength <= 2 ? codeNumLength : (codeNumLength - 1)));
        }

        return codePrefix + String.format("%0" + codeNumLength + "d", num);
    }

    /**
     * lambd语法生成编码
     * @param codePrefix 前缀
     * @param codeNumLength 长度
     * @param tableClass 表名
     * @param field 字段
     */
    public static <T> String lambdaGenerateCode(String codePrefix, Integer codeNumLength, Class<?> tableClass, SFunction<T, ?> field) {
        // 通过类获取表名
        String tableName = CommonUtil.getTableNameFromClass(tableClass);
        // 获取字段名
        String dbColumnName = CommonUtil.getDbColumnName(field);
        return generateCode(codePrefix, codeNumLength, tableName, dbColumnName,true);
    }

    public static String generateCodeByUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateCodeByUuid(Integer codeNumLength) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // uuid 长度是 32，从中截取连续 20 位
        int start = RandomUtil.randomInt(0, uuid.length() - codeNumLength + 1);
        return uuid.substring(start, start + codeNumLength);
    }

    /**
     * lambd生成带日期的编码
     * @param codePrefix 前缀
     * @param codeNumLength 长度
     * @param tableClass 类
     * @param field 编码字段
     * @param isStartOne 是否从1开始
     */
    public static <T> String lambdaGenerateCodeWithDate(String codePrefix, Integer codeNumLength, Class<?> tableClass, SFunction<T, ?> field,Boolean isStartOne) {
        // 通过类获取表名
        String tableName = CommonUtil.getTableNameFromClass(tableClass);
        // 获取字段名
        String dbColumnName = CommonUtil.getDbColumnName(field);
        return generateCodeWithDate(codePrefix, codeNumLength, tableName, dbColumnName,isStartOne);
    }

    /**
     * lambd生成带日期的编码，默认从1开始
     * @param codePrefix 前缀
     * @param codeNumLength 长度
     * @param tableClass 类
     * @param field 编码字段
     */
    public static <T> String lambdaGenerateCodeWithDate(String codePrefix, Integer codeNumLength, Class<?> tableClass, SFunction<T, ?> field) {
        // 通过类获取表名
        String tableName = CommonUtil.getTableNameFromClass(tableClass);
        // 获取字段名
        String dbColumnName = CommonUtil.getDbColumnName(field);
        return generateCodeWithDate(codePrefix, codeNumLength, tableName, dbColumnName,true);
    }

    public static String generateCodeWithDate(String codePrefix, Integer codeNumLength, String table, String codeField) {
        String format = DateUtil.format(new Date(), "yyyyMMdd");

        return generateCode(codePrefix + format, codeNumLength, table, codeField,true);
    }

    public static String generateCodeWithDate(String codePrefix, Integer codeNumLength, String table, String codeField,Boolean isStartOne) {
        String format = DateUtil.format(new Date(), "yyyyMMdd");

        return generateCode(codePrefix + format, codeNumLength, table, codeField,isStartOne);
    }
}
