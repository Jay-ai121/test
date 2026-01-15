package com.quzhou.tourism.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 高德地图配置类
 * 统一管理高德地图相关配置，读取 application.yml 中 amap 前缀的配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "amap")
public class AmapConfig {
    /**
     * 高德地图Web端/服务端API Key（从高德开放平台申请）
     */
    private String apiKey;

    /**
     * 高德地图路线规划接口地址（默认驾车路线）
     */
    private String routeUrl = "https://restapi.amap.com/v3/direction/driving";

    /**
     * 高德地图请求超时时间（单位：毫秒）
     */
    private Integer connectTimeout = 5000;

    /**
     * 高德地图读取数据超时时间（单位：毫秒）
     */
    private Integer readTimeout = 10000;
}