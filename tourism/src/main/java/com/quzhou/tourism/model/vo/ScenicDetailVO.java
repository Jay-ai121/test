package com.quzhou.tourism.model.vo;

import lombok.Data;

/**
 * 景点详情VO
 * 返回景点详情页所需全部字段
 */
@Data
public class ScenicDetailVO {
    /**
     * 景点ID
     */
    private Long id;

    /**
     * 景点名称
     */
    private String scenicName;

    /**
     * 景点地址
     */
    private String scenicAddress;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 景点介绍
     */
    private String scenicIntro;

    /**
     * 景点图片URL
     */
    private String scenicImg;

    /**
     * 景点评分
     */
    private Double rating;
}