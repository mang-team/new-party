package com.itmang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记需要记录日志的方法
 */
@Target(ElementType.METHOD) // 该注解作用于方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留该注解
public @interface ActionLog {
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 操作类型（1为查找操作，2为修改操作，3为增加操作，4为删除操作）
     */
    int operationType() default 1;
}