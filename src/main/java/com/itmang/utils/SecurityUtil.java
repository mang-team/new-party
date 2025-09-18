package com.itmang.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全工具类（不依赖Spring Security）
 * 兼容 Maven 3.0.6+ 和 Spring Boot 3.x
 */
public class SecurityUtil{

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);


    private SecurityUtil() {
        // 工具类，防止实例化
    }

    /**
     * 获取当前登录用户名
     */
    public  static String getCurrentUsername() {
        // 方案1：从Session中获取（传统Web应用）
        String usernameFromSession = getUsernameFromSession();
        if (usernameFromSession != null) {
            return usernameFromSession;
        }

        // 方案2：从请求头中获取（前后端分离）
        String usernameFromHeader = getUsernameFromHeader();
        if (usernameFromHeader != null) {
            return usernameFromHeader;
        }

        // 方案3：从JWT token中获取（如果使用JWT）
        String usernameFromJwt = getUsernameFromJwt();
        if (usernameFromJwt != null) {
            return usernameFromJwt;
        }

        // 方案4：返回默认用户
        logger.warn("无法获取当前用户信息，使用默认系统用户");
        return "system";
    }

    /**
     * 从Session中获取用户名
     */
    private static String getUsernameFromSession() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object username = session.getAttribute("username");
                if (username instanceof String) {
                    return (String) username;
                }
            }
        } catch (Exception e) {
            logger.debug("从Session获取用户名失败", e);
        }
        return null;
    }

    /**
     * 从请求头中获取用户名
     */
    private static String getUsernameFromHeader() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            // 尝试多种可能的请求头名称
            String[] headerNames = {"X-Username", "username", "X-User-Name", "User-Name"};
            for (String headerName : headerNames) {
                String username = request.getHeader(headerName);
                if (username != null && !username.trim().isEmpty()) {
                    return username.trim();
                }
            }
        } catch (Exception e) {
            logger.debug("从请求头获取用户名失败", e);
        }
        return null;
    }

    /**
     * 从JWT token中获取用户名
     */
    private static String getUsernameFromJwt() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // 这里可以添加JWT解析逻辑
                // 例如：return JwtTokenUtil.getUsernameFromToken(token);
                logger.debug("检测到JWT token，但未实现解析逻辑");
            }
        } catch (Exception e) {
            logger.debug("从JWT获取用户名失败", e);
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();

            // 从Session获取
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object userId = session.getAttribute("userId");
                if (userId instanceof Long) {
                    return (Long) userId;
                } else if (userId instanceof Integer) {
                    return ((Integer) userId).longValue();
                } else if (userId instanceof String) {
                    try {
                        return Long.parseLong((String) userId);
                    } catch (NumberFormatException e) {
                        logger.debug("用户ID格式错误", e);
                    }
                }
            }

            // 从头信息获取
            String[] headerNames = {"X-User-Id", "userId", "X-UserId", "User-Id"};
            for (String headerName : headerNames) {
                String userIdStr = request.getHeader(headerName);
                if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                    try {
                        return Long.parseLong(userIdStr.trim());
                    } catch (NumberFormatException e) {
                        logger.debug("用户ID格式错误: {}", userIdStr);
                    }
                }
            }

        } catch (Exception e) {
            logger.debug("获取用户ID失败", e);
        }
        return null;
    }

    /**
     * 检查是否有请求上下文
     */
    public static boolean hasRequestContext() {
        try {
            return RequestContextHolder.getRequestAttributes() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取当前请求对象
     */
    public static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            logger.debug("获取当前请求失败", e);
        }
        return null;
    }
}