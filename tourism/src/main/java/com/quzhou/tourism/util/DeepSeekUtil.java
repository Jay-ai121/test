package com.quzhou.tourism.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API调用工具类
 */
@Component
public class DeepSeekUtil {
    @Value("${third-party.deepseek.api-key}")
    private String deepSeekKey;

    @Value("${third-party.deepseek.request-url}")
    private String deepSeekBaseUrl;

    // 定义JSON媒体类型
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用DeepSeek生成衢州定制旅游行程
     * @param userQuestion 用户问题（如："我想在衢州玩2天，求山水路线"）
     * @return AI生成的行程内容（JSON格式，包含景点、时间、路线描述等）
     * @throws Exception 异常
     */
    public String generateQuzhouTrip(String userQuestion) throws Exception {
        // 构造Prompt，限定返回格式
        String prompt = "你是衢州本地旅游规划师，用户需求：" + userQuestion +
                "，请返回JSON格式的衢州旅游行程，包含以下字段：" +
                "tripName（行程名称）、days（天数）、routes（数组，每个元素包含day（第几天）、scenicName（景点名称）、" +
                "scenicAddress（景点地址）、longitude（经度，必填）、latitude（纬度，必填）、startTime（开始时间）、" +
                "endTime（结束时间）、description（游玩描述）），确保经纬度是衢州真实坐标，景点都是衢州本地的。";

        // 构造DeepSeek请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "deepseek-chat");
        requestMap.put("temperature", 0.7);
        requestMap.put("max_tokens", 2048);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestMap.put("messages", messages);

        // 转换为JSON字符串
        String requestJson = objectMapper.writeValueAsString(requestMap);

        // 构造请求
        RequestBody requestBody = RequestBody.create(requestJson, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(deepSeekBaseUrl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + deepSeekKey)
                .addHeader("Content-Type", "application/json")
                .build();

        // 执行请求并获取响应
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("DeepSeek API调用失败，状态码：" + response.code());
            }
            String responseBody = response.body().string();
            // 解析响应，提取content
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> messageMap = (Map<String, String>) choice.get("message");
            return messageMap.get("content");
        }
    }
}