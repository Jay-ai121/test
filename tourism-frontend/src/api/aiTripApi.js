/**
 * AI定制行程业务API
 * 路径：src/api/aiTripApi.js
 * 依赖：src/utils/request.js
 */
import { post, get } from '@/utils/request'

/**
 * 生成AI定制行程（核心接口）
 * @param {String} userQuestion 用户旅游需求（必传，如："衢州2天1晚山水游"）
 * @returns Promise 响应数据（AiTripVO格式，包含行程信息+高德路线数据）
 */
export const generateAiTrip = (userQuestion) => {
  // 核心修改：先做类型校验，确保是字符串后再调用trim()
  let trimmedQuestion = ''
  if (typeof userQuestion === 'string') {
    trimmedQuestion = userQuestion.trim()
  } else {
    // 非字符串类型：转换为字符串后再修剪（避免传对象/undefined导致报错）
    trimmedQuestion = String(userQuestion || '').trim()
  }

  // 空值校验（修剪后为空则拒绝请求）
  if (!trimmedQuestion) {
    console.error('用户旅游需求不能为空或仅含空格')
    return Promise.reject(new Error('用户旅游需求参数非法，请输入有效的旅游需求'))
  }

  // 构造请求体数据（与后端UserQuestionDTO格式匹配，保持不变）
  const data = {
    userQuestion: trimmedQuestion
  }

  // 对应后端接口：/ai-trip/generate（POST请求，传递JSON请求体）
  return post('/ai-trip/generate', data)
}

/**
 * （可选）根据ID查询用户历史AI行程
 * @param {Long} id 用户行程ID（必传）
 * @returns Promise 响应数据（UserTrip格式）
 */
export const getAiTripById = (id) => {
  // 加强ID校验：支持字符串类型的数字（如"123"），自动转换为Number
  const tripId = typeof id === 'string' ? Number(id) : id

  if (!tripId || tripId <= 0 || isNaN(tripId)) {
    console.error('用户行程ID不能为空且必须为正整数')
    return Promise.reject(new Error('用户行程ID参数非法，请传入有效的正整数'))
  }

  const params = {
    id: tripId
  }

  // 对应后端接口：/ai-trip/detail（如需扩展后端接口可启用）
  return get('/ai-trip/detail', params)
}