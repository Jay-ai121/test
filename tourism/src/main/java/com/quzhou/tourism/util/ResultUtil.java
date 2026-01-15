// com/quzhou/tourism/util/ResultUtil.java
package com.quzhou.tourism.util;

import com.quzhou.tourism.model.vo.ResultVO;

public class ResultUtil {
    // 成功响应（带数据）
    public static ResultVO success(Object data) {
        return ResultVO.success(data);
    }

    // 成功响应（无数据）
    public static ResultVO success() {
        return ResultVO.success(null);
    }

    // 失败响应
    public static ResultVO error(String msg) {
        return ResultVO.error(msg);
    }
}