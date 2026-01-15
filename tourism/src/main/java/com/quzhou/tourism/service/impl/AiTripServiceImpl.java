package com.quzhou.tourism.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.quzhou.tourism.config.DeepSeekConfig;
import com.quzhou.tourism.model.vo.AiTripVO;
import com.quzhou.tourism.service.AiTripService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI定制行程 - 业务实现类（OkHttp版本，解决超时问题+JSON格式问题）
 */
@Slf4j
@Service
public class AiTripServiceImpl implements AiTripService {

    // 注入配置类（已读取 application.yml 中的 deepseek 配置）
    @Autowired
    private DeepSeekConfig deepSeekConfig;

    // 注入专门用于 Deepseek 调用的 OkHttp 客户端（替代原来的 RestTemplate）
    @Autowired
    @Qualifier("deepseekOkHttpClient") // 匹配 DeepSeekConfig 中的 Bean 名称
    private OkHttpClient deepseekOkHttpClient;

    /**
     * 核心方法：生成AI行程+地图路线
     */
    @Override
    public AiTripVO generateTripWithMapRoute(String userQuestion) throws Exception {
        // 1. 校验用户需求
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            throw new IllegalArgumentException("用户旅游需求不能为空");
        }

        // 打印配置信息，验证是否生效
        log.info("Deepseek 配置生效：模型={}, 读取超时={}秒, 最大令牌数={}",
                deepSeekConfig.getModel(),
                deepSeekConfig.getReadTimeout() / 1000,
                deepSeekConfig.getMaxTokens());

        // 2. 调用 Deepseek API 生成行程（JSON格式）
        String aiJsonContent = callDeepSeekApi(userQuestion);
        log.info("Deepseek 生成并净化后的行程JSON：{}", aiJsonContent);

        // 3. 解析JSON为结构化VO
        AiTripVO aiTripVO = parseAiTripJson(aiJsonContent);

        // 4. 保存行程到数据库（可选，根据业务需求）
        saveUserTrip(userQuestion, aiJsonContent);

        // 5. 返回结构化行程数据（包含地图路线）
        return aiTripVO;
    }

    /**
     * 调用 Deepseek API（核心逻辑：OkHttp版本+JSON净化）
     */
    private String callDeepSeekApi(String userQuestion) throws Exception {
        try {
            // 构建 Deepseek 请求参数（严格匹配API格式）
            DeepSeekRequest request = new DeepSeekRequest();
            request.setModel(deepSeekConfig.getModel()); // 从配置读取模型（默认 deepseek-light）
            request.setTemperature(deepSeekConfig.getTemperature()); // 温度（默认0.5）
            request.setMaxTokens(deepSeekConfig.getMaxTokens()); // 最大令牌数（默认1500）

            // 构建消息体（强化system指令，强制纯JSON输出）
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", "你是严格的JSON生成器，仅输出符合要求的JSON字符串，无任何其他内容。要求：1. 必须包含字段：tripName（字符串）、days（整数）、schedule（数组，元素含day（整数）、items（数组，元素含timeSlot、scenicName、scenicAddress、description、tips（均为字符串））、routeList（数组，元素含day（整数）、name、address、startTime、endTime、description（均为字符串）、longitude、latitude（均为数字））；2. 衢州景点经纬度准确（如江郎山：longitude=118.6798，latitude=28.5964）；3. 字段名大小写严格匹配，无拼写错误；4. 直接返回JSON，不要加任何前缀（如\"json\"）、后缀、注释、换行、空格；5. 不要用代码块包裹（无```json标记）。"));
            messages.add(new Message("user", userQuestion)); // 用户需求
            request.setMessages(messages);

            // 构建 OkHttp 请求（JSON格式）
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            String requestJson = JSON.toJSONString(request); // 序列化请求参数
            Request okHttpRequest = new Request.Builder()
                    .url(deepSeekConfig.getRequestUrl()) // 从配置读取接口地址
                    .post(RequestBody.create(requestJson, mediaType))
                    .header("Authorization", "Bearer " + deepSeekConfig.getApiKey()) // 从配置读取密钥
                    .header("Content-Type", "application/json")
                    .build();

            // 打印关键日志，便于排查
            log.info("Deepseek API Key：{}", deepSeekConfig.getApiKey());
            log.info("Deepseek 请求头 Authorization：{}", "Bearer " + deepSeekConfig.getApiKey());
            log.info("Deepseek 请求参数：{}", requestJson);

            // 发送请求（同步调用，使用 OkHttp 配置的超时时间）
            Response response = deepseekOkHttpClient.newCall(okHttpRequest).execute();

            // 处理响应
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                log.info("Deepseek API 原始响应：{}", responseBody);

                // 解析 Deepseek 响应体（提取模型生成的内容）
                DeepSeekResponse deepSeekResponse = JSON.parseObject(responseBody, DeepSeekResponse.class);
                if (deepSeekResponse == null || deepSeekResponse.getChoices() == null || deepSeekResponse.getChoices().isEmpty()) {
                    throw new RuntimeException("Deepseek 响应格式异常，无有效行程数据");
                }

                // 提取模型生成的内容并净化（核心优化）
                String aiContent = deepSeekResponse.getChoices().get(0).getMessage().getContent().trim();
                log.info("Deepseek 模型原始输出：{}", aiContent);
                String purifiedJson = purifyJsonContent(aiContent); // 自动处理多余字符

                // 验证净化后的JSON是否合法
                if (isValidJson(purifiedJson)) {
                    return purifiedJson;
                } else {
                    throw new RuntimeException("Deepseek 返回内容经净化后仍非合法JSON：" + purifiedJson);
                }
            } else {
                String errorMsg = response.body() != null ? response.body().string() : "无响应内容";
                throw new RuntimeException("Deepseek API 调用失败，状态码：" + response.code() + "，错误信息：" + errorMsg);
            }
        } catch (Exception e) {
            log.error("调用 Deepseek API 异常", e);
            throw new Exception("生成定制行程失败：" + e.getMessage(), e);
        }
    }

    /**
     * 新增：JSON内容净化（自动去除多余字符，确保格式正确）
     */
    private String purifyJsonContent(String rawContent) {
        if (rawContent == null || rawContent.trim().isEmpty()) {
            return "";
        }

        // 1. 去除代码块标记（如 ```json 或 ```）
        rawContent = rawContent.replaceAll("```(json)?", "").trim();

        // 2. 去除开头的非JSON字符（如 "json\n"、"{" 之前的所有字符）
        Pattern jsonStartPattern = Pattern.compile("\\{.*", Pattern.DOTALL);
        Matcher matcher = jsonStartPattern.matcher(rawContent);
        if (matcher.find()) {
            rawContent = matcher.group();
        }

        // 3. 去除结尾的非JSON字符（如 "```"、注释等）
        Pattern jsonEndPattern = Pattern.compile(".*\\}", Pattern.DOTALL);
        matcher = jsonEndPattern.matcher(rawContent);
        if (matcher.find()) {
            rawContent = matcher.group();
        }

        // 4. 去除多余的换行、制表符、空格（只保留JSON结构必需的空格）
        rawContent = rawContent.replaceAll("\\s+", " ").replaceAll("\\s*,", ",").replaceAll(":\\s*", ":");

        log.info("JSON净化后内容：{}", rawContent);
        return rawContent;
    }

    /**
     * 保存用户行程到数据库（根据实际表结构修改）
     */
    @Override
    public boolean saveUserTrip(String userQuestion, String aiTripContent) {
        try {
            // TODO: 替换为你的数据库保存逻辑（如 JPA/MyBatis 操作）
            // 示例：
            // UserTrip userTrip = new UserTrip();
            // userTrip.setUserQuestion(userQuestion);
            // userTrip.setTripContent(aiTripContent);
            // userTrip.setCreateTime(LocalDateTime.now());
            // userTripRepository.save(userTrip);
            log.info("保存用户行程成功：用户需求={}", userQuestion);
            return true;
        } catch (Exception e) {
            log.error("保存用户行程失败，用户需求={}", userQuestion, e);
            return false;
        }
    }

    /**
     * 解析 Deepseek 返回的JSON为 AiTripVO（结构化数据，匹配最新VO字段）
     */
    @Override
    public AiTripVO parseAiTripJson(String aiJsonContent) {
        try {
            // 1. 解析Deepseek返回的净化后JSON
            JSONObject deepSeekJson = JSON.parseObject(aiJsonContent);
            if (deepSeekJson == null) {
                throw new RuntimeException("行程JSON解析失败，数据为空");
            }

            // 2. 构建AiTripVO（严格匹配VO字段）
            AiTripVO aiTripVO = new AiTripVO();
            aiTripVO.setTripName(deepSeekJson.getString("tripName"));
            aiTripVO.setDays(deepSeekJson.getInteger("days"));
            aiTripVO.setAiContent(aiJsonContent); // 保存原始JSON备份

            // 3. 解析每日行程安排（dailySchedules）
            List<JSONObject> scheduleJsonList = deepSeekJson.getJSONArray("schedule").toJavaList(JSONObject.class);
            List<AiTripVO.DailyScheduleVO> dailySchedules = new ArrayList<>();
            for (JSONObject scheduleJson : scheduleJsonList) {
                AiTripVO.DailyScheduleVO dailySchedule = new AiTripVO.DailyScheduleVO();
                dailySchedule.setDay(scheduleJson.getInteger("day"));
                dailySchedule.setDateDesc("第" + scheduleJson.getInteger("day") + "天");

                // 解析当日行程项
                List<JSONObject> itemJsonList = scheduleJson.getJSONArray("items").toJavaList(JSONObject.class);
                List<AiTripVO.ScheduleItemVO> scheduleItems = new ArrayList<>();
                for (JSONObject itemJson : itemJsonList) {
                    AiTripVO.ScheduleItemVO item = new AiTripVO.ScheduleItemVO();
                    item.setTimeSlot(itemJson.getString("timeSlot"));
                    item.setScenicName(itemJson.getString("scenicName"));
                    item.setScenicAddress(itemJson.getString("scenicAddress"));
                    item.setDescription(itemJson.getString("description"));
                    item.setTips(itemJson.getString("tips"));
                    scheduleItems.add(item);
                }
                dailySchedule.setScheduleItems(scheduleItems);
                dailySchedules.add(dailySchedule);
            }
            aiTripVO.setDailySchedules(dailySchedules);

            // 4. 解析路线列表（routeList，用于高德地图渲染）
            List<JSONObject> routeJsonList = deepSeekJson.getJSONArray("routeList").toJavaList(JSONObject.class);
            List<AiTripVO.TripRouteVO> routeList = new ArrayList<>();
            for (JSONObject routeJson : routeJsonList) {
                AiTripVO.TripRouteVO route = new AiTripVO.TripRouteVO();
                route.setDay(routeJson.getInteger("day"));
                route.setScenicName(routeJson.getString("name"));
                route.setScenicAddress(routeJson.getString("address"));
                route.setLongitude(routeJson.getDouble("longitude")); // 与VO字段一致
                route.setLatitude(routeJson.getDouble("latitude"));  // 与VO字段一致
                route.setStartTime(routeJson.getString("startTime"));
                route.setEndTime(routeJson.getString("endTime"));
                route.setDescription(routeJson.getString("description"));
                routeList.add(route);
            }
            aiTripVO.setRouteList(routeList);

            // 5. 校验核心字段（确保前端渲染所需数据存在）
            if (aiTripVO.getRouteList() == null || aiTripVO.getRouteList().isEmpty()) {
                throw new RuntimeException("行程数据中无有效路线信息（routeList字段缺失或为空）");
            }
            if (aiTripVO.getDailySchedules() == null || aiTripVO.getDailySchedules().isEmpty()) {
                throw new RuntimeException("行程数据中无有效每日安排（dailySchedules字段缺失或为空）");
            }

            log.info("行程JSON解析成功：行程名称={}, 天数={}", aiTripVO.getTripName(), aiTripVO.getDays());
            return aiTripVO;
        } catch (Exception e) {
            log.error("解析行程JSON异常，JSON内容：{}", aiJsonContent, e);
            throw new RuntimeException("行程数据解析失败：" + e.getMessage(), e);
        }
    }

    /**
     * 辅助方法：验证字符串是否为合法JSON
     */
    private boolean isValidJson(String jsonStr) {
        try {
            JSON.parseObject(jsonStr);
            return true;
        } catch (Exception e) {
            try {
                JSON.parseArray(jsonStr);
                return true;
            } catch (Exception ex) {
                log.error("JSON格式校验失败：{}", jsonStr, ex);
                return false;
            }
        }
    }

    // ==================== 内部静态类（匹配 Deepseek API 格式）====================
    /**
     * Deepseek API 请求参数类
     */
    @lombok.Data
    private static class DeepSeekRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        private Integer maxTokens;
    }

    /**
     * 消息体类（role + content）
     */
    @lombok.Data
    private static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    /**
     * Deepseek API 响应类
     */
    @lombok.Data
    private static class DeepSeekResponse {
        private List<Choice> choices;
    }

    @lombok.Data
    private static class Choice {
        private Message message;
    }
}