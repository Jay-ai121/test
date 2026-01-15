package com.quzhou.tourism.model.vo;

import lombok.Data;

/**
 * 景点列表展示VO
 * 仅返回列表页所需核心字段，减少数据传输
 */
@Data
public class ScenicListVO {
    /**
     * 景点ID
     */
    private Long id;

    /**
     * 景点名称
     */
    private String scenicName;

    /**
     * 景点图片URL
     */
    private String scenicImg;

    /**
     * 景点评分
     */
    private Double rating;

    /**
     * 景点地址
     */
    private String scenicAddress;
}