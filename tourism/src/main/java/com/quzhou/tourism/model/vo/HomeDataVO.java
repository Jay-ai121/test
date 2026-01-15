package com.quzhou.tourism.model.vo;

import lombok.Data;
import java.util.List;

/**
 * 首页聚合数据VO
 * 封装首页所需所有数据
 */
@Data
public class HomeDataVO {
    /**
     * 衢州概览文案
     */
    private String quzhouOverview;

    /**
     * 推荐景点列表
     */
    private List<ScenicListVO> recommendScenics;

    /**
     * 热门攻略列表
     */
    private List<TravelGuideVO> hotGuides;
}