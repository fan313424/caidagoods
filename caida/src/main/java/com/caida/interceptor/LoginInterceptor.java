package com.caida.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * 用于检查用户是否已登录，未登录用户将被重定向到登录页面
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    /**
     * 在请求处理之前进行调用
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler 被调用的处理器
     * @return true表示继续处理请求，false表示中断请求
     * @throws Exception 异常信息
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许OPTIONS预检请求通过（CORS机制）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 获取Session
        HttpSession session = request.getSession(false);
        
        // 检查Session中是否存在用户信息
        if (session != null && session.getAttribute("currentUser") != null) {
            // 用户已登录，允许继续访问
            return true;
        }
        
        // 判断是否为AJAX请求
        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            // AJAX请求，返回401状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\",\"data\":null}");
            return false;
        }
        
        // 普通请求，重定向到登录页面
        response.sendRedirect("/login.html");
        return false;
    }
}
