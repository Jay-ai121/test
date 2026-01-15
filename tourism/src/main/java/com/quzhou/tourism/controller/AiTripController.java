package com.quzhou.tourism.controller;

import com.quzhou.tourism.model.dto.UserQuestionDTO;
import com.quzhou.tourism.service.AiTripService;
import com.quzhou.tourism.util.ResultUtil;
import com.quzhou.tourism.model.vo.ResultVO;
// 导入AiTripVO类，替换为你的实际包路径
import com.quzhou.tourism.model.vo.AiTripVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI定制行程控制层
 */
@RestController
@RequestMapping("/ai-trip")
public class AiTripController {
    @Autowired
    private AiTripService aiTripService;

    /**
     * 用户提交问题，生成AI行程并返回地图渲染数据
     * @param userQuestionDTO 用户问题DTO
     * @return 行程数据+高德地图路线数据
     */
    @PostMapping("/generate")
    public ResultVO generateAiTrip(@RequestBody UserQuestionDTO userQuestionDTO) {
        try {
            String userQuestion = userQuestionDTO.getUserQuestion();
            // 1. 修改变量类型为 AiTripVO，与服务层方法返回值一致
            AiTripVO aiTripData = aiTripService.generateTripWithMapRoute(userQuestion);
            // 2. 直接返回 AiTripVO 对象，ResultUtil.success 可接收任意对象
            return ResultUtil.success(aiTripData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("生成定制行程失败：" + e.getMessage());
        }
    }
}