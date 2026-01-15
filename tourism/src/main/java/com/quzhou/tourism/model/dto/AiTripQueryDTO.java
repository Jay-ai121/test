package com.quzhou.tourism.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * AI定制行程查询DTO
 * 用于接收前端传递的AI行程查询条件（如行程ID、用户需求关键词等）
 * 对应数据库表：user_trip
 */
@Data
public class AiTripQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 行程主键ID（精确查询）
     * 用于根据ID查询单个历史行程
     */
    private Long id;

    /**
     * 行程名称关键词（模糊查询）
     * 用于根据行程名称关键字搜索历史行程（如："山水游"、"人文之旅"）
     */
    private String tripNameKeyword;

    /**
     * 用户需求关键词（模糊查询）
     * 用于根据用户原始旅游需求关键字搜索历史行程（如："2天1晚"、"衢州"）
     */
    private String userQuestionKeyword;

    /**
     * 分页查询 - 页码（默认1）
     * 用于分页查询历史行程列表
     */
    private Integer pageNum = 1;

    /**
     * 分页查询 - 每页条数（默认10）
     * 用于分页查询历史行程列表
     */
    private Integer pageSize = 10;
}