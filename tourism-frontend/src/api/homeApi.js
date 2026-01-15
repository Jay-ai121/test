/**
 * 首页业务API
 * 路径：src/api/homeApi.js
 * 依赖：src/utils/request.js
 */
import { get } from '@/utils/request'

/**
 * 获取首页聚合核心数据
 * 包含：衢州概览、推荐景点、热门攻略
 * @returns Promise 响应数据（HomeDataVO格式）
 */
export const getHomeData = () => {
  // 调用封装的get方法，对应后端接口：/home/home-data
  return get('/home/home-data', {}, {
    // 可选：关闭当前请求的加载动画（如需单独控制）
    // headers: { noLoading: true }
  })
}