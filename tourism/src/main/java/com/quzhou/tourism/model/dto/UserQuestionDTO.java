package com.quzhou.tourism.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * AI定制行程 - 用户问答入参DTO
 */
@Data
public class UserQuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户旅游需求不能为空")
    private String userQuestion; // 用户输入的旅游需求（如："衢州2天1晚山水游，预算1000元"）
}