package com.quzhou.tourism.model.vo;

import lombok.Data;
import java.util.Date;

/**
 * 旅游攻略VO
 * 封装攻略列表展示所需字段
 */
@Data
public class TravelGuideVO {
    /**
     * 攻略ID
     */
    private Long id;

    /**
     * 攻略标题
     */
    private String guideTitle;

    /**
     * 攻略作者
     */
    private String author;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    private Date createTime;
}