package com.quzhou.tourism.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有后端接口跨域（包括 /api 前缀的）
                .allowedOrigins("http://localhost:5173") // 只允许你的前端地址访问
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的请求方式
                .allowedHeaders("*") // 允许所有请求头（包括你前端的 Content-Type: application/json）
                .exposedHeaders("*") // 允许前端读取后端返回的所有头（避免某些场景报错）
                .allowCredentials(true) // 允许携带Cookie
                .maxAge(3600); // 预检请求缓存1小时（减少请求次数）
    }
}