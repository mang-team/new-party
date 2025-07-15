package com.itmang.aop;


import com.itmang.annotation.AutoFill;
import com.itmang.constant.AutoFillConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.context.BaseContext;
import com.itmang.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component//将该类放入bean容器管理
@Aspect//声明该类为aop类
@Slf4j
public class AutoFillAop {

    //定义切点
    @Pointcut("execution(* com.itmang.mapper.*.*(..)) " +
            "&& @annotation(com.itmang.annotation.AutoFill)")
    public void autoFillPointCut(){ }

    //定义切面
    @Before("autoFillPointCut()")
    public void AutoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("自动填充数据");
        //获取方法名签名
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //获取方法上的注解
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType=autoFill.value();//获取注解内传的值
        //获取方法参数
        Object[] args = joinPoint.getArgs();
        Object entriy= args[0];
        //将要填入的数据先得到
        LocalDateTime now = LocalDateTime.now();
        String userId= BaseContext.getCurrentId();

        //根据注解的值进行不同的填充
        if(operationType.equals(OperationType.INSERT)){
            //获得方法
            Method setCreateTime =entriy.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
            Method setCreateBy =entriy.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_By,String.class);
            Method setUpdateTime =entriy.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            Method setUpdateBy =entriy.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_BY,String.class);
            //调用得到的方法进行设置数据
            setCreateTime.invoke(entriy,now);
            setCreateBy.invoke(entriy,userId);
            setUpdateTime.invoke(entriy,now);
            setUpdateBy.invoke(entriy,userId);
        } else if (operationType.equals(OperationType.UPDATE)) {
            Method setUpdateTime =entriy.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            Method setUpdateBy =entriy.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_BY,String.class);
            setUpdateTime.invoke(entriy,now);
            setUpdateBy.invoke(entriy,userId);
        }else{
            log.info(MessageConstant.UNKNOWN_ERROR);
        }


    }

}
