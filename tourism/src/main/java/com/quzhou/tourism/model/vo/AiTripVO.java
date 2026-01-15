package com.quzhou.tourism.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * AI定制行程 - 出参VO（包含行程信息+地图路线数据）
 */
@Data
public class AiTripVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 行程基础信息（与Deepseek返回JSON对应）
    private String tripName; // 行程名称（如：衢州2天1晚山水人文之旅）
    private Integer days; // 行程天数
    private String aiContent; // AI生成的原始行程内容（JSON格式，用于备份）

    // 每日详细行程安排（新增：与Deepseek返回的schedule对应）
    private List<DailyScheduleVO> dailySchedules; // 每日行程列表

    // 地图路线数据（高德地图渲染用，结构化设计，避免前端二次解析）
    private String routeJson; // 高德地图路线规划JSON数据（可选，保留原字段）
    private List<TripRouteVO> routeList; // 结构化行程路线列表（前端直接渲染）

    /**
     * 每日行程安排VO（新增：对应Deepseek返回的schedule字段）
     */
    @Data
    public static class DailyScheduleVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer day; // 第几天（如：1）
        private String dateDesc; // 日期描述（如：第一天）
        private List<ScheduleItemVO> scheduleItems; // 当日行程项列表
    }

    /**
     * 每日行程项VO（新增：细化当日安排）
     */
    @Data
    public static class ScheduleItemVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private String timeSlot; // 时间段（如：09:00-11:30）
        private String scenicName; // 景点名称
        private String scenicAddress; // 景点地址
        private String description; // 游玩描述（如：游览江郎山，欣赏丹霞地貌）
        private String tips; // 游玩小贴士（如：建议穿舒适运动鞋）
    }

    /**
     * 结构化行程路线VO（优化：字段名标准化，与高德地图适配）
     */
    @Data
    public static class TripRouteVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer day; // 第几天
        private String scenicName; // 景点名称
        private String scenicAddress; // 景点地址
        private Double longitude; // 经度（高德地图要求字段名，保持不变）
        private Double latitude; // 纬度（高德地图要求字段名，保持不变）
        private String startTime; // 开始时间（如：09:00）
        private String endTime; // 结束时间（如：11:30）
        private String description; // 游玩描述
        private String routeDesc; // 路线描述（新增：如“从江郎山到廿八都古镇，车程约1.5小时”）
    }
}