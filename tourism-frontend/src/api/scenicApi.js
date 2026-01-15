/**
 * 景点大全业务API
 * 路径：src/api/scenicApi.js
 * 依赖：src/utils/request.js
 */
import { get } from '@/utils/request'

/**
 * 分页获取景点列表
 * @param {Number} pageNum 页码（默认1）
 * @param {Number} pageSize 每页条数（默认10）
 * @returns Promise 响应数据（包含scenicList和totalCount）
 */
export const getScenicList = (pageNum = 1, pageSize = 10) => {
  // 构造URL参数
  const params = {
    pageNum,
    pageSize
  }
  // 对应后端接口：/scenic/list
  return get('/scenic/list', params)
}

/**
 * 根据景点ID获取景点详情
 * @param {Long} id 景点主键ID（必传）
 * @returns Promise 响应数据（ScenicDetailVO格式）
 */
export const getScenicDetail = (id) => {
  if (!id || id <= 0) {
    console.error('景点ID不能为空且必须为正整数')
    return Promise.reject(new Error('景点ID参数非法'))
  }
  // 构造URL参数
  const params = {
    id
  }
  // 对应后端接口：/scenic/detail
  return get('/scenic/detail', params)
}