/**
 * 前端请求工具类（axios 封装）
 * 路径：src/utils/request.js
 */
import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

// 1. 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '', // 基础接口地址（从环境变量读取，灵活切换开发/生产环境）
  timeout: 30000, // 请求超时时间（10秒）
  headers: {
    'Content-Type': 'application/json;charset=utf-8' // 默认请求头
  }
})

// 2. 加载动画实例（全局唯一）
let loadingInstance = null

// 3. 请求拦截器（发送请求前的处理）
service.interceptors.request.use(
  (config) => {
    // 开启加载动画（可通过 config.headers.noLoading 关闭单个请求的加载）
    if (!config.headers.noLoading) {
      loadingInstance = ElLoading.service({
        lock: true,
        text: '正在加载中...',
        background: 'rgba(0, 0, 0, 0.1)'
      })
    }

    // 可在此添加 token 等认证信息（如用户登录后携带身份令牌）
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }

    return config
  },
  (error) => {
    // 清除加载动画
    if (loadingInstance) {
      loadingInstance.close()
    }
    // 打印请求错误信息
    console.error('请求拦截器错误：', error)
    ElMessage.error('请求发送失败，请稍后重试')
    return Promise.reject(error)
  }
)

// 4. 响应拦截器（接收响应后的处理）
service.interceptors.response.use(
  (response) => {
    // 清除加载动画
    if (loadingInstance) {
      loadingInstance.close()
    }

    // 获取响应数据（后端返回的 ResultVO 格式）
    const res = response.data

    // 业务状态码判断（200 为成功，其他为失败）
    if (res.code !== 200) {
      // 错误提示（优先使用后端返回的 msg，无则使用默认提示）
      ElMessage.error(res.msg || '接口请求失败，请稍后重试')

      // 可在此添加特殊状态码处理（如 401 未登录、403 无权限等）
      // if (res.code === 401) {
      //   ElMessage.warning('登录状态已过期，请重新登录')
      //   // 清除本地缓存并跳转到登录页
      //   localStorage.removeItem('token')
      //   window.location.href = '/login'
      // }

      return Promise.reject(res || new Error('接口请求失败'))
    } else {
      // 请求成功，直接返回响应数据（简化前端调用，无需再通过 res.data 获取）
      return res
    }
  },
  (error) => {
    // 清除加载动画
    if (loadingInstance) {
      loadingInstance.close()
    }

    // 网络错误/超时等异常处理
    let errorMsg = '网络异常，请检查网络连接后重试'
    if (error.code === 'ECONNABORTED') {
      errorMsg = '请求超时，请稍后重试'
    } else if (error.response) {
      // 服务器返回错误状态码
      const status = error.response.status
      switch (status) {
        case 404:
          errorMsg = '请求的接口不存在'
          break
        case 500:
          errorMsg = '服务器内部错误，请稍后重试'
          break
        default:
          errorMsg = `请求失败，状态码：${status}`
      }
    }

    // 提示错误信息
    ElMessage.error(errorMsg)
    console.error('响应拦截器错误：', error)
    return Promise.reject(error)
  }
)

// 5. 暴露常用请求方法（GET/POST/PUT/DELETE）
/**
 * GET 请求
 * @param {String} url 接口地址
 * @param {Object} params URL参数
 * @param {Object} config 额外配置（如关闭加载动画：{ headers: { noLoading: true } }）
 * @returns Promise
 */
export const get = (url, params = {}, config = {}) => {
  return service.get(url, {
    params,
    ...config
  })
}

/**
 * POST 请求
 * @param {String} url 接口地址
 * @param {Object} data 请求体数据
 * @param {Object} config 额外配置
 * @returns Promise
 */
export const post = (url, data = {}, config = {}) => {
  return service.post(url, data, config)
}

/**
 * PUT 请求
 * @param {String} url 接口地址
 * @param {Object} data 请求体数据
 * @param {Object} config 额外配置
 * @returns Promise
 */
export const put = (url, data = {}, config = {}) => {
  return service.put(url, data, config)
}

/**
 * DELETE 请求
 * @param {String} url 接口地址
 * @param {Object} params URL参数
 * @param {Object} config 额外配置
 * @returns Promise
 */
export const del = (url, params = {}, config = {}) => {
  return service.delete(url, {
    params,
    ...config
  })
}

// 6. 暴露 axios 实例（如需自定义请求可直接使用）
export default service