package com.itmang.aop;

import com.alibaba.fastjson.JSON;
import com.itmang.annotation.GlobalInterceptor;
import com.itmang.constant.AdminConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@Slf4j
public class GlobalAspect {

    @Resource
    private RedisTemplate redisTemplate;

    @Before("@annotation(com.itmang.annotation.GlobalInterceptor)")
    public void interceptor(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
        if (interceptor == null) {
            return;
        }
        if (interceptor.checkLogin()) {
            checkLogin();
        }
        if (interceptor.checkPermission()) {
            checkPermission();
        }
    }

    private void checkLogin() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            throw new BaseException(MessageConstant.USER_NOT_LOGIN);
        }
        if (redisTemplate.hasKey("blacklist:" + token)) {
            throw new BaseException(MessageConstant.USER_NOT_LOGIN);
        }
    }

    private void checkPermission() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();
        List<String> permissionList =
                (List<String>) JSON.parse((String) redisTemplate.opsForValue()
                        .get(AdminConstant.USER_PERMISSION_KEY + BaseContext.getCurrentId()));
        if (!permissionList.contains(requestURI)) {
            throw new BaseException("你没有此权限");
        }
    }
}
