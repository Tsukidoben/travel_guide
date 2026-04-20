package com.own.common.annotation;

import java.lang.annotation.*;

/**
 * 无需验证注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {

}
