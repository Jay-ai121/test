package com.quzhou.tourism.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.quzhou.tourism.util.AmapUtil;
import com.quzhou.tourism.util.ResultUtil;
import com.quzhou.tourism.model.vo.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 高德地图路线查询专用控制器（通用接口，供前端/其他业务调用）
 * 职责：只处理“起点/终点/路线类型”到“高德路线数据”的转换
 */
@RestController
@RequestMapping("/route") // 接口基础路径：/api/route（因为server.context-path=/api）
public class RouteController {
    // 1. 常量提取：提升可维护性，避免魔法值
    private static final List<String> VALID_ROUTE_TYPES = Collections.unmodifiableList(
            Arrays.asList("driving", "walking", "transit")
    );
    private static final String DEFAULT_ROUTE_TYPE = "driving";
    private static final String GAODE_SUCCESS_STATUS = "1";
    private static final String POLYLINE_SEPARATOR = ";";
    // 衢州地理范围常量
    private static final double MIN_LAT = 28.4;
    private static final double MAX_LAT = 29.5;
    private static final double MIN_LNG = 118.4;
    private static final double MAX_LNG = 119.5;

    // 2. 依赖注入：添加final关键字，确保不可被重新赋值
    private final AmapUtil amapUtil;

    // 3. 构造器注入：替代@Autowired字段注入，更符合Spring最佳实践（便于单元测试）
    // 修复：构造器注入时，若类只有一个构造器，@Autowired可以省略（Spring自动注入）
    public RouteController(AmapUtil amapUtil) {
        this.amapUtil = amapUtil;
    }

    /**
     * 通用路线查询接口（支持驾车/步行/公交）
     * 前端访问地址：http://localhost:8080/api/route/getRoute
     * @param origin 起点经纬度（格式：纬度,经度，如：28.9783,118.8750）
     * @param destination 终点经纬度（同上）
     * @param routeType 路线类型：driving（默认）/walking/transit
     * @return 解码后的路线经纬度字符串数组（适配前端解析逻辑）
     */
    @GetMapping("/getRoute")
    public ResultVO getRoute(
            @RequestParam String origin,          // 必传：起点经纬度
            @RequestParam String destination,     // 必传：终点经纬度
            @RequestParam(defaultValue = DEFAULT_ROUTE_TYPE) String routeType // 可选，默认驾车
    ) {
        try {
            // 新增：打印进入接口+参数，确认参数接收正常
            System.out.println("=====进入getRoute接口=====");
            System.out.println("origin=" + origin + ", destination=" + destination + ", routeType=" + routeType);

            // 步骤1：校验路线类型合法性
            if (!VALID_ROUTE_TYPES.contains(routeType)) {
                return ResultUtil.error(String.format("路线类型错误，仅支持：%s", String.join("、", VALID_ROUTE_TYPES)));
            }

            // 步骤2：校验经纬度格式（必须是“数字,数字”且在衢州合理范围）
            if (!isValidLatLng(origin)) {
                return ResultUtil.error("起点经纬度格式错误，正确格式：纬度,经度（如：28.9783,118.8750），且需在衢州区域内");
            }
            if (!isValidLatLng(destination)) {
                return ResultUtil.error("终点经纬度格式错误，正确格式：纬度,经度（如：28.9783,118.8750），且需在衢州区域内");
            }

            // 步骤3：转换经纬度顺序：后端接收（纬度,经度）→ 高德API要求（经度,纬度）
            String gaodeOrigin = convertLatLngOrder(origin);
            String gaodeDest = convertLatLngOrder(destination);
            System.out.println("转换后高德API参数：origin=" + gaodeOrigin + ", destination=" + gaodeDest);

            // 步骤4：调用AmapUtil获取对应类型的路线原始数据
            String routeJson = getRouteJsonByType(gaodeOrigin, gaodeDest, routeType);

            // 打印高德API返回的原始数据
            System.out.println("高德API返回原始数据：" + routeJson);

            // 步骤5：处理高德API返回结果：判断是否请求成功（status=1表示成功）
            JSONObject jsonObject = JSONObject.parseObject(routeJson);
            String status = jsonObject.getString("status");
            if (!GAODE_SUCCESS_STATUS.equals(status)) {
                String info = jsonObject.getString("info");
                String infocode = jsonObject.getString("infocode");
                return ResultUtil.error(String.format("高德API请求失败：状态码=%s, 原因=%s, 错误码=%s", status, info, infocode));
            }

            // 步骤6：解析高德返回的JSON，提取polyline（修复公交路线多段拼接问题）
            String polyline = extractPolylineFromJson(jsonObject, routeType);
            if (polyline == null || polyline.isEmpty()) {
                return ResultUtil.error("未查询到有效路线，请检查起点终点距离或路线类型");
            }
            System.out.println("解析到的polyline：" + polyline);

            // 步骤7：解码polyline（加密的经纬度串→经纬度数组）
            List<double[]> routePoints = decodePolyline(polyline);

            // 步骤8：转换为前端适配的格式（List<String>："lng,lat"字符串）
            List<String> routePointStrs = convertToFrontendFormat(routePoints);

            // 步骤9：返回结果（前端可直接解析绘图）
            return ResultUtil.success(routePointStrs);
        } catch (Exception e) {
            System.out.println("=====接口执行异常=====");
            e.printStackTrace();
            return ResultUtil.error("查询路线失败：" + e.getMessage());
        }
    }

    /**
     * 经纬度格式校验工具方法（衢州区域范围：纬度28.4°-29.5°，经度118.4°-119.5°）
     * @param latLngStr 待校验的经纬度字符串（格式：纬度,经度）
     * @return 校验结果：true=合法，false=非法
     */
    private boolean isValidLatLng(String latLngStr) {
        // 快速判空和格式校验
        if (latLngStr == null || !latLngStr.contains(",")) {
            return false;
        }

        String[] parts = latLngStr.split(",");
        if (parts.length != 2) {
            return false;
        }

        try {
            double lat = Double.parseDouble(parts[0]); // 纬度（第一个参数）
            double lng = Double.parseDouble(parts[1]); // 经度（第二个参数）
            // 校验是否在衢州合理地理范围内
            return lat >= MIN_LAT && lat <= MAX_LAT && lng >= MIN_LNG && lng <= MAX_LNG;
        } catch (NumberFormatException e) {
            // 数字格式转换失败，返回非法
            return false;
        }
    }

    /**
     * 解码高德polyline的工具方法（保持原有核心逻辑不变，优化变量命名）
     * @param polyline 高德加密的经纬度串
     * @return 解码后的经纬度数组列表（每个元素：[经度, 纬度]）
     */
    private List<double[]> decodePolyline(String polyline) {
        JSONArray resultArray = new JSONArray();
        int index = 0, length = polyline.length();
        int lat = 0, lng = 0;

        while (index < length) {
            int b, shift = 0, resultNum = 0;
            do {
                b = polyline.charAt(index++) - 63;
                resultNum |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((resultNum & 1) != 0 ? ~(resultNum >> 1) : (resultNum >> 1));
            lat += dlat;

            shift = 0;
            resultNum = 0;
            do {
                b = polyline.charAt(index++) - 63;
                resultNum |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((resultNum & 1) != 0 ? ~(resultNum >> 1) : (resultNum >> 1));
            lng += dlng;

            resultArray.add(new double[]{lng / 1000000.0, lat / 1000000.0});
        }

        return resultArray.toJavaList(double[].class);
    }

    /**
     * 提取经纬度顺序转换（纬度,经度 → 经度,纬度）
     * 封装重复逻辑，提升代码复用性
     * @param latLngStr 原始经纬度字符串（纬度,经度）
     * @return 转换后的经纬度字符串（经度,纬度）
     */
    private String convertLatLngOrder(String latLngStr) {
        String[] parts = latLngStr.split(",");
        return parts[1] + "," + parts[0];
    }

    /**
     * 根据路线类型获取高德API返回的原始JSON数据
     * 封装switch逻辑，简化主方法代码结构
     * @param origin 起点（高德格式：经度,纬度）
     * @param destination 终点（高德格式：经度,纬度）
     * @param routeType 路线类型
     * @return 高德API返回的原始JSON字符串
     * 修复：方法声明抛出Exception，由调用方（getRoute）的try-catch统一处理
     */
    private String getRouteJsonByType(String origin, String destination, String routeType) throws Exception {
        switch (routeType) {
            case "walking":
                return amapUtil.getWalkingRoute(origin, destination);
            case "transit":
                return amapUtil.getTransitRoute(origin, destination);
            default: // driving（默认）
                return amapUtil.getDrivingRoute(origin, destination);
        }
    }

    /**
     * 从高德返回的JSON中提取polyline数据
     * 封装复杂的JSON解析逻辑，提升主方法可读性
     * @param jsonObject 高德返回的JSON对象
     * @param routeType 路线类型
     * @return 提取后的polyline字符串
     * 修复：解决“将JSON对象误当JSON数组处理”的核心问题
     */
    private String extractPolylineFromJson(JSONObject jsonObject, String routeType) {
        // 核心修复：先尝试解析为JSONArray，若失败则尝试解析为JSONObject
        Object routeObj = jsonObject.get("route");
        if (routeObj == null) {
            return "";
        }

        JSONObject firstRoute = null;
        // 情况1：route是JSONArray（驾车/步行路线的标准返回）
        if (routeObj instanceof JSONArray) {
            JSONArray routes = (JSONArray) routeObj;
            if (routes.size() == 0) {
                return "";
            }
            firstRoute = routes.getJSONObject(0);
        }
        // 情况2：route是JSONObject（部分场景的返回格式）
        else if (routeObj instanceof JSONObject) {
            firstRoute = (JSONObject) routeObj;
        }
        // 情况3：既不是数组也不是对象，直接返回空
        else {
            return "";
        }

        // 后续提取polyline的逻辑（统一基于firstRoute）
        if ("transit".equals(routeType)) {
            // 公交路线：拼接所有steps的polyline
            JSONArray steps = firstRoute.getJSONArray("steps");
            if (steps == null || steps.size() == 0) {
                return "";
            }

            StringBuilder polylineSb = new StringBuilder();
            for (int i = 0; i < steps.size(); i++) {
                JSONObject step = steps.getJSONObject(i);
                String stepPolyline = step.getString("polyline");
                if (stepPolyline != null && !stepPolyline.isEmpty()) {
                    if (polylineSb.length() > 0) {
                        polylineSb.append(POLYLINE_SEPARATOR); // 高德多段polyline用分号分隔
                    }
                    polylineSb.append(stepPolyline);
                }
            }
            return polylineSb.toString();
        } else {
            // 驾车/步行路线：提取第一条路径的polyline
            JSONArray paths = firstRoute.getJSONArray("paths");
            if (paths == null || paths.size() == 0) {
                return "";
            }
            return paths.getJSONObject(0).getString("polyline");
        }
    }

    /**
     * 转换为前端适配的格式（List<double[]> → List<String>）
     * 封装格式转换逻辑，提升代码可读性
     * @param routePoints 解码后的经纬度数组列表
     * @return 前端可直接解析的"lng,lat"字符串列表
     */
    private List<String> convertToFrontendFormat(List<double[]> routePoints) {
        return routePoints.stream()
                .map(point -> point[0] + "," + point[1]) // 拼接经度和纬度为字符串
                .collect(Collectors.toList());
    }
}