package com.quzhou.tourism.mapper;

import com.quzhou.tourism.model.vo.ScenicDetailVO;
import com.quzhou.tourism.model.vo.ScenicListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点Mapper接口
 * 对应数据库表：scenic_spot
 * 与 src/main/resources/mapper/ScenicSpotMapper.xml 映射绑定
 */
public interface ScenicSpotMapper {

    /**
     * 查询推荐景点（按评分倒序，限制条数）
     * @param limit 返回数据条数
     * @return 推荐景点列表（ScenicListVO）
     */
    List<ScenicListVO> selectRecommendScenics(@Param("limit") Integer limit);

    /**
     * 分页查询景点列表
     * @param offset 分页偏移量（(pageNum-1)*pageSize）
     * @param pageSize 每页显示条数
     * @return 景点分页列表（ScenicListVO）
     */
    List<ScenicListVO> selectScenicList(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 查询景点总条数
     * @return 景点总数量
     */
    Integer selectScenicTotalCount();

    /**
     * 根据景点ID查询景点详情
     * @param id 景点主键ID
     * @return 景点详情（ScenicDetailVO）
     */
    ScenicDetailVO selectScenicDetailById(@Param("id") Long id);
}