package com.quzhou.tourism.service;

import com.quzhou.tourism.model.vo.ScenicDetailVO;
import com.quzhou.tourism.model.vo.ScenicListVO;

import java.util.List;

/**
 * 景点大全业务层接口
 * 封装景点列表、景点详情相关业务逻辑
 */
public interface ScenicService {

    /**
     * 分页获取景点列表
     * @param offset 分页偏移量（(pageNum-1)*pageSize）
     * @param pageSize 每页显示条数
     * @return 景点列表（ScenicListVO集合）
     */
    List<ScenicListVO> getScenicList(Integer offset, Integer pageSize);

    /**
     * 获取景点总条数
     * 用于分页组件计算总页数
     * @return 景点总数量
     */
    Integer getScenicTotalCount();

    /**
     * 根据景点ID获取景点详情
     * @param id 景点主键ID
     * @return 景点详情VO（ScenicDetailVO）
     */
    ScenicDetailVO getScenicDetail(Long id);
}