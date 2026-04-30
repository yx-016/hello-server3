//package com.yx198.helloserver3.config;
//
//import com.yx198.helloserver3.interceptor.AuthInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuthInterceptor())
//                .addPathPatterns("/api/**")  // 拦截 /api 下的所有请求路径
//                .excludePathPatterns(
//                        "/api/users/login","/api/users"    // 只放行登录接口，其他全部拦截交由拦截器内部判断
//                );
//    }
//}