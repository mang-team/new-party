package com.itmang.aop;

import com.itmang.annotation.ActionLog;
import com.itmang.context.BaseContext;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.entity.UserLog;
import com.itmang.service.user.LogService;
import com.itmang.service.user.UserService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 日志切面类
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private LogService logService;

    @Resource
    private IdGenerate idGenerate;

    @Resource
    private UserService userService;

    /**
     * 返回通知处理日志记录（正常返回时触发）
     *
     * @param joinPoint 连接点
     */
    @AfterReturning("@annotation(com.itmang.annotation.ActionLog)")
    public void logAfterReturning(JoinPoint joinPoint) {
        try {
            // 获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 获取注解信息
            ActionLog actionLogAnnotation = method.getAnnotation(ActionLog.class);

            // 构建日志信息
            UserLog userLog = buildUserLog(actionLogAnnotation, null);

            // 保存日志
            logService.saveLog(userLog);
        } catch (Exception e) {
            log.error("保存日志失败: {}", e.getMessage());
        }
    }

    /**
     * 异常通知处理日志记录（抛出异常时触发）
     *
     * @param joinPoint 连接点
     * @param exception 异常信息
     */
    @AfterThrowing(pointcut = "@annotation(com.itmang.annotation.ActionLog)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        try {
            // 获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 获取注解信息
            ActionLog actionLogAnnotation = method.getAnnotation(ActionLog.class);

            // 构建日志信息
            UserLog userLog = buildUserLog(actionLogAnnotation, exception);

            // 保存日志
            logService.saveLog(userLog);
        } catch (Exception e) {
            log.error("保存异常日志失败: {}", e.getMessage());
        }
    }

    /**
     * 构建用户日志实体
     *
     * @param actionLogAnnotation 日志注解
     * @param exception           异常信息
     * @return 用户日志实体
     */
    private UserLog buildUserLog(ActionLog actionLogAnnotation,
                                 Exception exception) {
        UserLog userLog = new UserLog();
        userLog.setId(idGenerate.nextUUID(userLog)); // 简单生成ID
        userLog.setCreateTime(LocalDateTime.now());

        // 设置操作者
        User userInfo = userService.getById(BaseContext.getCurrentId());
        userLog.setCreateBy(userInfo.getUserName());


        // 设置描述
        String description = actionLogAnnotation.description();
        userLog.setContent(description);

        //如果有异常，就将用户日志内容后加上异常信息
        if(exception != null){
            userLog.setContent(description + "，出现异常：" + exception.getMessage());
        }

        // 设置操作类型
        int operationType = actionLogAnnotation.operationType();
        userLog.setType(operationType);

        return userLog;
    }
}