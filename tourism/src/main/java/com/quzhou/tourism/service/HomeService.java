package com.quzhou.tourism.service;

import com.quzhou.tourism.model.vo.HomeDataVO;

/**
 * 首页业务层接口
 * 封装首页核心数据的业务逻辑
 */
public interface HomeService {

    /**
     * 获取首页聚合数据
     * 包含：衢州概览、推荐景点、热门攻略
     * @return HomeDataVO 首页核心数据VO
     */
    HomeDataVO getHomeData();
}