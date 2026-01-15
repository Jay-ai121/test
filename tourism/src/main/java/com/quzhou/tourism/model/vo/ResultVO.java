// com/quzhou/tourism/model/vo/ResultVO.java
package com.quzhou.tourism.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO {
    private Integer code; // 200成功，500失败
    private String msg; // 提示信息
    private Object data; // 响应数据

    // 快速构建成功响应
    public static ResultVO success(Object data) {
        return new ResultVO(200, "操作成功", data);
    }

    // 快速构建失败响应
    public static ResultVO error(String msg) {
        return new ResultVO(500, msg, null);
    }
}