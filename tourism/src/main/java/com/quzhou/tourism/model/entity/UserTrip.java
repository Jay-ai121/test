package com.quzhou.tourism.model.entity;

import lombok.Data;
import java.util.Date;

/**
 * 用户AI定制行程实体类
 * 对应数据库表：user_trip
 */
@Data
public class UserTrip {
    /**
     * 主键ID
     * 对应数据库字段：id (BIGINT, 自增主键)
     */
    private Long id;

    /**
     * 行程名称
     * 对应数据库字段：trip_name (VARCHAR(200), 非空)
     */
    private String tripName;

    /**
     * 用户问题/旅游需求
     * 对应数据库字段：user_question (LONGVARCHAR, 可为空)
     */
    private String userQuestion;

    /**
     * AI生成的行程内容（JSON格式）
     * 对应数据库字段：ai_trip_content (LONGVARCHAR, 可为空)
     */
    private String aiTripContent;

    /**
     * 创建时间
     * 对应数据库字段：create_time (DATETIME, 默认当前时间)
     * 无需手动赋值，数据库自动填充，MyBatis查询时自动映射
     */
    private Date createTime;
}