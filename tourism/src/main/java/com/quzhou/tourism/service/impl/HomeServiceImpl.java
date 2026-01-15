package com.quzhou.tourism.service.impl;

import com.quzhou.tourism.mapper.ScenicSpotMapper;
import com.quzhou.tourism.mapper.TravelGuideMapper;
import com.quzhou.tourism.model.vo.HomeDataVO;
import com.quzhou.tourism.model.vo.ScenicListVO;
import com.quzhou.tourism.model.vo.TravelGuideVO;
import com.quzhou.tourism.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页业务层实现类
 * 实现首页数据的聚合与组装逻辑
 */
@Service
public class HomeServiceImpl implements HomeService {

    // 注入景点Mapper和攻略Mapper
    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private TravelGuideMapper travelGuideMapper;

    /**
     * 核心逻辑：聚合首页所需所有数据
     * @return HomeDataVO
     */
    @Override
    public HomeDataVO getHomeData() {
        // 1. 初始化首页数据VO
        HomeDataVO homeDataVO = new HomeDataVO();

        // 2. 获取推荐景点（前6个，按评分倒序）
        List<ScenicListVO> recommendScenics = scenicSpotMapper.selectRecommendScenics(6);
        homeDataVO.setRecommendScenics(recommendScenics);

        // 3. 获取热门攻略（前4个，按浏览量倒序）
        List<TravelGuideVO> hotGuides = travelGuideMapper.selectHotGuides(4);
        homeDataVO.setHotGuides(hotGuides);

        // 4. 设置衢州概览（固定文案，也可后续从数据库配置表读取）
        String quzhouOverview = "衢州，浙江省地级市，地处浙江省西部、钱塘江上游，素有“东南阙里、南孔圣地”之称，是国家历史文化名城。这里拥有江郎山、龙游石窟、南宗孔庙、廿八都古镇等众多自然与人文景观，山水相依，儒风浩荡，是休闲度假、文化探秘的绝佳目的地。";
        homeDataVO.setQuzhouOverview(quzhouOverview);

        // 5. 返回组装后的首页数据
        return homeDataVO;
    }
}