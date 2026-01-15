<template>
  <nav class="nav-container bg-white shadow-md sticky top-0 z-10">
    <div class="max-w-7xl mx-auto px-4 md:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- 网站Logo & 名称 -->
        <div class="flex items-center">
          <img src="@/assets/images/quzhou-logo.png" alt="衢州旅游网" class="h-10 w-10 rounded-md">
          <span class="ml-3 text-xl font-bold text-[#27ae60]">衢州旅游网</span>
        </div>

        <!-- 导航菜单 -->
        <div class="hidden md:flex space-x-8">
          <router-link 
            to="/" 
            class="nav-link"
            :class="{ 'text-[#27ae60] font-medium': $route.name === 'Home' }"
          >
            首页
          </router-link>
          <router-link 
            to="/scenic-list" 
            class="nav-link"
            :class="{ 'text-[#27ae60] font-medium': $route.name === 'ScenicList' }"
          >
            景点大全
          </router-link>
          <router-link 
            to="/ai-trip" 
            class="nav-link"
            :class="{ 'text-[#27ae60] font-medium': $route.name === 'AiTrip' }"
          >
            AI定制行程
          </router-link>
        </div>

        <!-- 移动端菜单按钮（可选） -->
        <div class="md:hidden">
          <button class="text-gray-600 hover:text-[#27ae60]">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
// 无需额外逻辑，仅导航渲染
</script>

<style scoped>
.nav-container {
  transition: all 0.3s ease;
}

.nav-link {
  color: #2c3e50;
  font-size: 14px;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: #27ae60;
}
</style>