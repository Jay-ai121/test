package com.quzhou.tourism.service.impl;

import com.quzhou.tourism.mapper.ScenicSpotMapper;
import com.quzhou.tourism.model.vo.ScenicDetailVO;
import com.quzhou.tourism.model.vo.ScenicListVO;
import com.quzhou.tourism.service.ScenicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 景点大全业务层实现类
 * 实现景点分页查询、详情查询的核心逻辑
 */
@Service
public class ScenicServiceImpl implements ScenicService {

    // 注入景点Mapper
    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    /**
     * 分页查询景点列表
     * 直接调用Mapper层分页SQL
     * @param offset 分页偏移量
     * @param pageSize 每页条数
     * @return 景点列表
     */
    @Override
    public List<ScenicListVO> getScenicList(Integer offset, Integer pageSize) {
        // 校验参数，避免非法值
        if (offset == null || offset < 0) {
            offset = 0;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 50) {
            pageSize = 10; // 默认每页10条，最大50条
        }
        return scenicSpotMapper.selectScenicList(offset, pageSize);
    }

    /**
     * 获取景点总条数
     * 用于前端分页组件计算总页数
     * @return 景点总数量
     */
    @Override
    public Integer getScenicTotalCount() {
        return scenicSpotMapper.selectScenicTotalCount();
    }

    /**
     * 根据ID查询景点详情
     * @param id 景点ID
     * @return 景点详情
     */
    @Override
    public ScenicDetailVO getScenicDetail(Long id) {
        // 校验ID合法性
        if (id == null || id <= 0) {
            return null;
        }
        return scenicSpotMapper.selectScenicDetailById(id);
    }
}