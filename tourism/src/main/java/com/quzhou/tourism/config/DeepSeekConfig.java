package com.quzhou.tourism.config;

import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * DeepSeek AI 配置类
 * 统一管理DeepSeek相关配置 + OkHttp客户端配置（替代RestTemplate，解决超时问题）
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek") // 与 application.yml 中 deepseek 前缀匹配
@EnableConfigurationProperties(DeepSeekConfig.class) // 启用配置绑定（SpringBoot 3.x 必需）
public class DeepSeekConfig {
    /**
     * DeepSeek API Key（从DeepSeek开放平台申请）
     */
    private String apiKey = "sk-1c37b7a1f1d74c16974bdafae76b94d6";

    /**
     * DeepSeek 接口请求地址（默认通用对话接口）
     */
    private String requestUrl = "https://api.deepseek.com/v1/chat/completions";

    /**
     * DeepSeek 模型版本（如 deepseek-chat / deepseek-light（轻量版更快））
     */
    private String model = "deepseek-chat"; // 默认改为轻量模型，加快响应

    /**
     * 请求超时时间（单位：毫秒）
     */
    private Integer connectTimeout = 30000; // 连接超时30秒

    /**
     * 读取数据超时时间（单位：毫秒）
     */
    private Integer readTimeout = 180000; // 延长到180秒（3分钟），给足模型推理时间

    /**
     * 文本生成温度（0-1，值越高生成结果越随机）
     */
    private Double temperature = 0.5; // 降低温度，更稳定

    /**
     * 最大生成令牌数
     */
    private Integer maxTokens = 1500; // 减少令牌数，加快响应

    /**
     * 专门用于调用 Deepseek API 的 OkHttp 客户端（替代RestTemplate，超时配置更可靠）
     * @return OkHttpClient 实例（Bean名称：deepseekOkHttpClient）
     */
    @Bean(name = "deepseekOkHttpClient") // 给Bean命名，方便后续注入
    public OkHttpClient deepseekOkHttpClient() {
        return new OkHttpClient.Builder()
                // 连接超时（从配置读取，默认30秒）
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                // 读取超时（核心：模型推理时间，默认180秒）
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                // 写入超时（新增，避免请求发送超时）
                .writeTimeout(60, TimeUnit.SECONDS)
                // 连接失败自动重试（可选，提升稳定性）
                .retryOnConnectionFailure(true)
                .build();
    }
}