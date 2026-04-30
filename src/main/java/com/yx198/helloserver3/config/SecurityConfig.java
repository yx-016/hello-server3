package com.yx198.helloserver3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 开启 CORS
                .csrf(AbstractHttpConfigurer::disable)  // 关闭 CSRF（前后端分离模式）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 无状态会话
                .authorizeHttpRequests(auth -> auth
                        // 放行：注册接口
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // 放行：登录接口
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        // 放行：获取用户信息（GET，可选，根据需求）
                        // .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)  // 关闭表单登录
                .httpBasic(AbstractHttpConfigurer::disable);  // 关闭 HTTP Basic 认证

        return http.build();
    }

    // CORS 配置（允许前端跨域访问）
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));  // 允许所有来源（生产环境应指定具体域名）
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}