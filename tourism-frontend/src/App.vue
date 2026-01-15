<template>
  <!-- 全局布局容器 -->
  <div class="app-container flex-col min-h-screen">
    <!-- 顶部导航栏（公共组件，全局复用） -->
    <NavBar />

    <!-- 主体内容区域：路由出口（页面跳转的容器） -->
    <main class="app-main flex-1 container mx-auto py-4 md:py-6">
      <router-view />
    </main>

    <!-- 底部页脚（公共组件，全局复用） -->
    <Footer />
  </div>
</template>

<script setup>
// 引入公共组件（导航栏、页脚）
import NavBar from './components/NavBar.vue'
import Footer from './components/Footer.vue'
</script>

<style scoped>
/* 根容器样式：垂直弹性布局，占满屏幕高度 */
.app-container {
  width: 100%;
  background-color: var(--bg-color-gray-light);
}

/* 主体内容：自动填充剩余高度，适配页面滚动 */
.app-main {
  width: 100%;
  overflow: hidden;
}
</style>