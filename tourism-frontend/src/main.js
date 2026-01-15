/**
 * 前端项目入口文件
 * 路径：src/main.js
 * 功能：创建Vue应用、全局注册依赖、配置全局属性
 */
import { createApp } from 'vue'
import App from './App.vue'
// 引入路由配置
import router from './router'
// 引入Element Plus UI组件库
import ElementPlus from 'element-plus'
// 引入Element Plus默认样式
import 'element-plus/dist/index.css'
// 引入Element Plus中文语言包（解决组件默认英文问题）
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
// 引入全局自定义样式
import './assets/styles/global.css'
// 可选：引入全局工具类（如需挂载到Vue原型上）
// import * as request from './utils/request'
// import * as amapUtil from './utils/amapUtil'

// 创建Vue应用实例
const app = createApp(App)

// 全局配置Element Plus
app.use(ElementPlus, {
  locale: zhCn, // 设置中文语言
  size: 'default' // 设置组件默认尺寸（可选：large / default / small）
})

// 挂载路由
app.use(router)

// 可选：全局挂载工具类（便于所有组件直接使用，无需重复import）
// app.config.globalProperties.$request = request
// app.config.globalProperties.$amapUtil = amapUtil

// 挂载应用到DOM节点（对应public/index.html中的<div id="app"></div>）
app.mount('#app')