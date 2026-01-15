import { createRouter, createWebHistory } from 'vue-router'
// 导入3个核心页面
import Home from '../pages/Home.vue'
import ScenicList from '../pages/ScenicList.vue'
import AiTrip from '../pages/AiTrip.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '衢州旅游网 - 首页' }
  },
  {
    path: '/scenic-list',
    name: 'ScenicList',
    component: ScenicList,
    meta: { title: '衢州旅游网 - 景点大全' }
  },
  {
    path: '/ai-trip',
    name: 'AiTrip',
    component: AiTrip,
    meta: { title: '衢州旅游网 - AI定制行程' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：设置页面标题
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router