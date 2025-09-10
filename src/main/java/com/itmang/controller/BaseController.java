package com.itmang.controller;

import com.itmang.constant.JwtClaimsConstant;
import com.itmang.properties.JwtProperties;
import com.itmang.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:BaseController
 * Package:com.itmang.controller
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/9/10 14:07
 * Version: 1.0
 */
public class BaseController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private JwtProperties jwtProperties;//导入jwt配置类


    /**
     * 获取tokenDto
     *
     * @return
     */
    protected String getUserIdFromToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        return (String) claims.get(JwtClaimsConstant.USER_ID);
    }

    /**
     * 保存token到cookie中
     *
     * @param response
     * @param token
     */
    protected void saveToken2cookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(24 * 60 * 60 * 7);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 重置token的有效时间
     *
     * @return
     */
    protected String resetTokenTime() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        String userId = (String) claims.get(JwtClaimsConstant.USER_ID);
        if (claims.getExpiration().getTime() - System.currentTimeMillis() < 24 * 60 * 60 * 1000) {
            Map<String, Object> newClaims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, userId);//将用户的id存储进声明中
            //生成token令牌
            token = JwtUtil.createJWT(
                    jwtProperties.getAdminSecretKey(),//设置关键字
                    jwtProperties.getAdminTtl(),//设置token有效时间
                    newClaims);//设置声明信息（键值对）
        }
        return token;
    }

    /**
     * 清除cookie的token和redis中的tokenDto
     *
     * @param response
     */
    protected void cleanTokenFromCookie(HttpServletResponse response) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                break;
            }
        }
    }

    /**
     * 将令牌加入黑名单（基于剩余过期时间）
     */
    protected void blacklistToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        redisTemplate.opsForValue().set(
                "blacklist:" + token,
                "invalid",
                claims.getExpiration().getTime() - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
        );
    }


    /**
     * 验证时检查黑名单
     *
     * @return
     */
    protected boolean isTokenValid() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        return !redisTemplate.hasKey("blacklist:" + token); // 令牌已失效
    }
}
