package com.quzhou.tourism.service;

import com.quzhou.tourism.model.vo.AiTripVO;

/**
 * AI定制行程 - 业务接口
 */
public interface AiTripService {

    /**
     * 生成AI定制行程并关联高德地图路线
     * @param userQuestion 用户旅游需求
     * @return AiTripVO 行程数据+地图路线数据
     * @throws Exception 异常（API调用失败、数据解析失败等）
     */
    AiTripVO generateTripWithMapRoute(String userQuestion) throws Exception;

    /**
     * 保存用户AI定制行程到数据库
     * @param userQuestion 用户问题
     * @param aiTripContent AI生成的行程内容（JSON格式）
     * @return 保存是否成功
     */
    boolean saveUserTrip(String userQuestion, String aiTripContent);

    /**
     * 解析DeepSeek返回的JSON行程数据为结构化VO
     * @param aiJsonContent AI返回的JSON字符串
     * @return AiTripVO 结构化行程VO
     */
    AiTripVO parseAiTripJson(String aiJsonContent);
}