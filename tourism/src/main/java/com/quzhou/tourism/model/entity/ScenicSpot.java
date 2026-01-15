package com.quzhou.tourism.model.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * 景点实体类
 * 对应数据库表：scenic_spot
 * 存储景点的核心基础信息
 */
@Data
public class ScenicSpot implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 对应数据库字段：id (BIGINT, 自增主键)
     */
    private Long id;

    /**
     * 景点名称
     * 对应数据库字段：scenic_name (VARCHAR(200), 非空)
     */
    private String scenicName;

    /**
     * 景点地址
     * 对应数据库字段：scenic_address (VARCHAR(500), 可为空)
     */
    private String scenicAddress;

    /**
     * 经度
     * 对应数据库字段：longitude (DOUBLE, 可为空)
     * 用于高德地图定位与渲染
     */
    private Double longitude;

    /**
     * 纬度
     * 对应数据库字段：latitude (DOUBLE, 可为空)
     * 用于高德地图定位与渲染
     */
    private Double latitude;

    /**
     * 景点介绍
     * 对应数据库字段：scenic_intro (LONGVARCHAR, 可为空)
     */
    private String scenicIntro;

    /**
     * 景点图片URL
     * 对应数据库字段：scenic_img (VARCHAR(1000), 可为空)
     */
    private String scenicImg;

    /**
     * 景点评分
     * 对应数据库字段：rating (DOUBLE, 默认0.0)
     */
    private Double rating;
}