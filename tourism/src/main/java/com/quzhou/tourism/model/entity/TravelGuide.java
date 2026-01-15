package com.quzhou.tourism.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 旅游攻略实体类
 * 对应数据库表：travel_guide
 * 存储旅游攻略的核心基础信息
 */
@Data
public class TravelGuide implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 对应数据库字段：id (BIGINT, 自增主键)
     */
    private Long id;

    /**
     * 攻略标题
     * 对应数据库字段：guide_title (VARCHAR(500), 非空)
     */
    private String guideTitle;

    /**
     * 攻略内容
     * 对应数据库字段：guide_content (LONGVARCHAR, 非空)
     */
    private String guideContent;

    /**
     * 攻略作者
     * 对应数据库字段：author (VARCHAR(100), 非空)
     */
    private String author;

    /**
     * 浏览量
     * 对应数据库字段：view_count (INT, 默认0)
     */
    private Integer viewCount;

    /**
     * 发布时间
     * 对应数据库字段：create_time (DATETIME, 默认当前时间)
     * 数据库自动填充，无需手动赋值
     */
    private Date createTime;
}