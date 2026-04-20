package com.own.common.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.own.common.utils.ContextUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis自动填充处理器
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    /**
     * 使用mybatis-plus进行添加操作时，会自动执行这个方法
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        ContextUtil contextUtil = SpringUtil.getBean(ContextUtil.class);
        this.setFieldValByName("creator", contextUtil.getCurrentUserId(), metaObject);
        this.setFieldValByName("createName", contextUtil.getCurrentUserName(), metaObject);
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("version", 1, metaObject);
        this.setFieldValByName("isDele", 0, metaObject);

    }

    /**
     * 使用mybatis-plus进行更新操作时，会自动执行这个方法
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        ContextUtil contextUtil = SpringUtil.getBean(ContextUtil.class);


        this.setFieldValByName("updator", contextUtil.getCurrentUserId(), metaObject);
        this.setFieldValByName("updateName", contextUtil.getCurrentUserName(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
