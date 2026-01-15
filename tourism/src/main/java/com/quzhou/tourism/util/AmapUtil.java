package com.quzhou.tourism.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 高德地图工具类（支持驾车/步行/公交三种路线类型，完全适配 Java 21）
 */
@Component
@ConfigurationProperties(prefix = "third-party.amap")
public class AmapUtil {
    // 高德Web服务API Key（通过ConfigurationProperties绑定，对应配置：third-party.amap.key）
    private String key;

    // 三种路线类型的API地址（对应配置：third-party.amap.route-url.driving/walking/transit）
    private Map<String, String> routeUrl = new HashMap<>();

    // 超时配置（默认值兜底，支持配置文件覆盖）
    private int connectTimeout = 5000;
    private int readTimeout = 10000;

    // OkHttpClient实例（懒加载，避免初始化时机问题）
    private OkHttpClient okHttpClient;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 启动时校验配置合法性（提前发现配置问题，避免运行时报错）
     */
    @PostConstruct
    public void validateConfig() {
        // 1. 校验API Key是否配置
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalStateException("【AmapUtil】高德地图API Key未配置！请检查配置项：third-party.amap.key");
        }

        // 2. 校验所有路线类型的API地址是否配置
        List<String> requiredRouteTypes = Arrays.asList("driving", "walking", "transit");
        for (String type : requiredRouteTypes) {
            String url = routeUrl.get(type);
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalStateException("【AmapUtil】未配置" + type + "路线的API地址！请检查配置项：third-party.amap.route-url." + type);
            }
        }

        // 3. 校验超时时间合理性
        if (connectTimeout <= 0) {
            connectTimeout = 5000;
            System.out.println("【AmapUtil】连接超时时间配置无效，使用默认值：5000ms");
        }
        if (readTimeout <= 0) {
            readTimeout = 10000;
            System.out.println("【AmapUtil】读取超时时间配置无效，使用默认值：10000ms");
        }
    }

    /**
     * 懒加载初始化OkHttpClient（确保配置已绑定完成，Java 21 兼容）
     */
    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            lock.lock();
            try {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                            .build();
                }
            } finally {
                lock.unlock();
            }
        }
        return okHttpClient;
    }

    /**
     * 获取驾车路线
     * @param origin 起点（纬度,经度，如："28.9783,118.8750"）
     * @param destination 终点（纬度,经度）
     * @return 高德地图路线JSON数据
     * @throws Exception 异常
     */
    public String getDrivingRoute(String origin, String destination) throws Exception {
        return getRoute(origin, destination, "driving");
    }

    /**
     * 获取步行路线
     * @param origin 起点（纬度,经度）
     * @param destination 终点（纬度,经度）
     * @return 高德地图路线JSON数据
     * @throws Exception 异常
     */
    public String getWalkingRoute(String origin, String destination) throws Exception {
        return getRoute(origin, destination, "walking");
    }

    /**
     * 获取公交路线（集成版，包含步行+公交）
     * @param origin 起点（纬度,经度）
     * @param destination 终点（纬度,经度）
     * @return 高德地图路线JSON数据
     * @throws Exception 异常
     */
    public String getTransitRoute(String origin, String destination) throws Exception {
        return getRoute(origin, destination, "transit");
    }

    /**
     * 核心通用方法：根据路线类型获取路线数据（Java 21 专属优化：替换过时的 HttpUrl.parse）
     * @param origin 起点（纬度,经度）
     * @param destination 终点（纬度,经度）
     * @param routeType 路线类型：driving/walking/transit
     * @return 高德地图路线JSON数据
     * @throws Exception 异常
     */
    private String getRoute(String origin, String destination, String routeType) throws Exception {
        // 1. 校验核心参数
        if (origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException("起点经纬度不能为空");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("终点经纬度不能为空");
        }

        // 2. 获取对应路线的API地址
        String baseUrl = routeUrl.get(routeType);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new Exception("未配置" + routeType + "路线的API地址");
        }

        // 3. 构造请求URL（Java 21 优化：用 HttpUrl.parse 替代过时方法，自动编码参数）
        okhttp3.HttpUrl httpUrl = okhttp3.HttpUrl.parse(baseUrl);
        if (httpUrl == null) {
            throw new Exception(routeType + "路线API地址格式错误：" + baseUrl);
        }

        okhttp3.HttpUrl.Builder urlBuilder = httpUrl.newBuilder()
                .addQueryParameter("origin", origin)
                .addQueryParameter("destination", destination)
                .addQueryParameter("key", key) // 使用配置绑定的key
                .addQueryParameter("output", "json");

        // 公交路线额外参数（自动编码中文“衢州市”，Java 21 完全兼容）
        if ("transit".equals(routeType)) {
            urlBuilder.addQueryParameter("city", "衢州市")
                    .addQueryParameter("transit_type", "bus")
                    .addQueryParameter("extensions", "all");
        }

        okhttp3.HttpUrl finalUrl = urlBuilder.build();
        System.out.println("【AmapUtil】请求高德API：" + finalUrl); // 打印最终请求地址，方便排查

        // 4. 执行HTTP请求（Java 21 自动资源管理，无需额外处理流关闭）
        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .build();

        try (Response response = getOkHttpClient().newCall(request).execute()) {
            // 5. 校验响应状态
            if (!response.isSuccessful()) {
                throw new Exception("高德API调用失败：状态码=" + response.code() + "，请求地址=" + finalUrl);
            }
            if (response.body() == null) {
                throw new Exception("高德API返回空数据，请求地址=" + finalUrl);
            }

            // 6. 打印高德返回的原始JSON，关键排查依据！
            String routeJson = response.body().string();
            System.out.println("【AmapUtil】高德API返回原始数据（" + routeType + "）：" + routeJson);

            return routeJson;
        } catch (Exception e) {
            // 增强异常信息，方便定位
            throw new Exception("获取" + routeType + "路线失败：" + e.getMessage() + "，请求地址=" + finalUrl, e);
        }
    }

    // ------------------------------ getter/setter（必须，ConfigurationProperties绑定用）------------------------------
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getRouteUrl() {
        return routeUrl;
    }

    public void setRouteUrl(Map<String, String> routeUrl) {
        this.routeUrl = routeUrl;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}