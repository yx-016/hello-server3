package com.yx198.helloserver3.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 获取本次请求的 HTTP 动词和具体路径
        String method = request.getMethod();
        String uri = request.getRequestURI();

        System.out.println("拦截器检测 - 请求方法: " + method + ", 请求路径: " + uri);

        // 2. 手写细粒度放行规则
        // 规则 A: 如果是 POST 请求，且路径精确等于 "/api/users"，则放行（允许注册）
        boolean isCreateUser = "POST".equalsIgnoreCase(method) && "/api/users".equals(uri);

        // 规则 B: 如果是 GET 请求，且路径以 "/api/users/" 开头（允许查看用户信息）
        boolean isGetUser = "GET".equalsIgnoreCase(method) && uri.startsWith("/api/users/") && !uri.equals("/api/users");

        // 规则 C: 登录接口完全放行
        boolean isLogin = "/api/users/login".equals(uri);

        // 只要满足上述任一合法公开规则，直接放行，无需查验 Token
        if (isCreateUser || isGetUser || isLogin) {
            System.out.println("拦截器放行 - 公开接口: " + method + " " + uri);
            return true;
        }

        // 3. 执行严格的 Token 校验（针对 DELETE、PUT 等敏感操作）
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            // 未携带 Token，返回 401 错误
            response.setContentType("application/json;charset=UTF-8");
            String errorJson = "{\"code\": 401, \"msg\": \"非法操作: 敏感操作 [" + method + " " + uri + "] 需要授权\"}";
            response.getWriter().write(errorJson);
            System.out.println("拦截器拦截 - 未携带 Token: " + method + " " + uri);
            return false;
        }

        // 简单的 Token 校验（实际生产环境应该验证 Token 的有效性）
        if (!token.startsWith("Bearer ") && !token.startsWith("mock-token-")) {
            response.setContentType("application/json;charset=UTF-8");
            String errorJson = "{\"code\": 401, \"msg\": \"Token 无效，请重新登录\"}";
            response.getWriter().write(errorJson);
            System.out.println("拦截器拦截 - Token 无效: " + token);
            return false;
        }

        System.out.println("拦截器放行 - Token 验证通过: " + method + " " + uri);
        return true; // 令牌存在且有效，予以放行
    }
}