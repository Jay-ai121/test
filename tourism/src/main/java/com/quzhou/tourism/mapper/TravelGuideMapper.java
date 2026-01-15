package com.quzhou.tourism.mapper;

import com.quzhou.tourism.model.vo.TravelGuideVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 旅游攻略Mapper接口
 * 对应数据库表：travel_guide
 * 与 src/main/resources/mapper/TravelGuideMapper.xml 映射绑定
 */
public interface TravelGuideMapper {

    /**
     * 查询热门攻略（按浏览量倒序，限制条数）
     * @param limit 返回数据条数
     * @return 热门攻略列表（TravelGuideVO）
     */
    List<TravelGuideVO> selectHotGuides(@Param("limit") Integer limit);
}