// com/quzhou/tourism/controller/ScenicController.java
package com.quzhou.tourism.controller;

import com.quzhou.tourism.service.ScenicService;
import com.quzhou.tourism.util.ResultUtil;
import com.quzhou.tourism.model.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

/**
 * 景点大全控制层
 */
@RestController
@RequestMapping("/scenic")
public class ScenicController {

    @Autowired
    private ScenicService scenicService;

    /**
     * 获取景点列表（支持分页，默认第1页，每页10条）
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 景点分页数据
     */
    @GetMapping("/list")
    public ResultVO getScenicList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            // 计算分页偏移量
            Integer offset = (pageNum - 1) * pageSize;
            Map<String, Object> result = new HashMap<>();
            // 获取景点列表
            result.put("scenicList", scenicService.getScenicList(offset, pageSize));
            // 获取总条数
            result.put("totalCount", scenicService.getScenicTotalCount());
            return ResultUtil.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取景点列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取景点详情
     * @param id 景点ID
     * @return 景点详情
     */
    @GetMapping("/detail")
    public ResultVO getScenicDetail(@RequestParam Long id) {
        try {
            return ResultUtil.success(scenicService.getScenicDetail(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取景点详情失败：" + e.getMessage());
        }
    }
}