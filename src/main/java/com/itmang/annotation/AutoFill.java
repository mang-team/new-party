package com.itmang.annotation;




import com.itmang.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//方法上使用
@Retention(RetentionPolicy.RUNTIME)//运行时触发
public @interface AutoFill {
    OperationType value();
}
