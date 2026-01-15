// com/quzhou/tourism/controller/HomeController.java
package com.quzhou.tourism.controller;

import com.quzhou.tourism.service.HomeService;
import com.quzhou.tourism.util.ResultUtil;
import com.quzhou.tourism.model.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页数据控制层
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 获取首页核心数据（热门攻略、推荐景点、衢州概览）
     * @return 首页聚合数据
     */
    @GetMapping("/home-data")
    public ResultVO getHomeData() {
        try {
            Object homeData = homeService.getHomeData();
            return ResultUtil.success(homeData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取首页数据失败：" + e.getMessage());
        }
    }
}