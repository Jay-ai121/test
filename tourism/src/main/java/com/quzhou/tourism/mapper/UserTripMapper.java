package com.quzhou.tourism.mapper;

import com.quzhou.tourism.model.entity.UserTrip;
import org.apache.ibatis.annotations.Param;

/**
 * 用户AI行程 - Mapper接口（MyBatis纯框架）
 */
public interface UserTripMapper {

    /**
     * 保存用户行程
     * @param userTrip 用户行程实体
     * @return 受影响行数
     */
    int insert(UserTrip userTrip);

    /**
     * 可选：根据ID查询用户行程
     * @param id 主键ID
     * @return 用户行程实体
     */
    UserTrip selectById(@Param("id") Long id);
}