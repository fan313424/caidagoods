package com.caida.config;

import com.caida.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 用于注册拦截器和配置其他Web相关设置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LoginInterceptor loginInterceptor;
    
    /**
     * 添加拦截器配置
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 注册登录拦截器
         * 拦截所有API请求，但排除登录和注册相关的公开接口
         */
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有API请求
                .excludePathPatterns(
                        "/api/user/login",      // 排除登录接口
                        "/api/user/register",   // 排除注册接口
                        "/api/user/check-username",  // 排除用户名检查接口
                        "/api/cart/**",          // 排除购物车相关接口（测试阶段）
                        "/api/order/**",         // 排除订单相关接口（测试阶段）
                        "/api/admin/**",         // 排除管理员相关接口
                        "/api/ai/**"             // 排除AI助手接口
                );
    }
    
    /**
     * 配置CORS跨域访问
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 对所有API接口允许跨域
                .allowedOrigins("*")  // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")  // 允许所有请求头
                .maxAge(3600);  // 预检请求缓存时间（秒）
    }
}
