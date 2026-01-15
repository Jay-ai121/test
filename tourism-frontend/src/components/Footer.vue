<template>
  <footer class="footer-container bg-[#2c3e50] text-white py-8">
    <div class="max-w-7xl mx-auto px-4 md:px-8 grid grid-cols-1 md:grid-cols-3 gap-8">
      <!-- 网站介绍 -->
      <div>
        <h3 class="text-lg font-bold mb-4 border-b border-gray-600 pb-2">衢州旅游网</h3>
        <p class="text-gray-300 text-sm">
          专注于衢州本地旅游资源整合，为您提供优质的旅游攻略、景点推荐和个性化行程定制服务，带您领略衢州的山水之美与人文底蕴。
        </p>
      </div>

      <!-- 快速链接 -->
      <div>
        <h3 class="text-lg font-bold mb-4 border-b border-gray-600 pb-2">快速链接</h3>
        <ul class="text-gray-300 text-sm space-y-2">
          <li><router-link to="/" class="hover:text-[#2ecc71] transition-colors">首页</router-link></li>
          <li><router-link to="/scenic-list" class="hover:text-[#2ecc71] transition-colors">景点大全</router-link></li>
          <li><router-link to="/ai-trip" class="hover:text-[#2ecc71] transition-colors">AI定制行程</router-link></li>
        </ul>
      </div>

      <!-- 联系我们 -->
      <div>
        <h3 class="text-lg font-bold mb-4 border-b border-gray-600 pb-2">联系我们</h3>
        <div class="text-gray-300 text-sm space-y-2">
          <p>地址：浙江省衢州市柯城区中山路1号</p>
          <p>电话：0570-1234567</p>
          <p>邮箱：service@quzhoutour.com</p>
        </div>
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="max-w-7xl mx-auto px-4 md:px-8 mt-8 pt-4 border-t border-gray-600 text-center text-gray-400 text-xs">
      © 2026 衢州旅游网 版权所有 | 浙ICP备12345678号
    </div>
  </footer>
</template>

<script setup>
// 无需额外逻辑，仅页脚渲染
</script>

<style scoped>
.footer-container {
  margin-top: auto;
}
</style>