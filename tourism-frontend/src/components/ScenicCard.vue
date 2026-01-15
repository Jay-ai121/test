<template>
  <div class="scenic-card rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
    <!-- 景点图片 -->
    <div class="scenic-img-container h-48 w-full">
      <img 
        :src="scenic.scenicImg || '@/assets/images/default-scenic.jpg'" 
        :alt="scenic.scenicName"
        class="w-full h-full object-cover"
      >
    </div>
    <!-- 景点信息 -->
    <div class="p-4">
      <div class="flex justify-between items-start mb-2">
        <h3 class="text-lg font-semibold text-[#2c3e50] hover:text-[#27ae60] transition-colors">
          <router-link :to="`/scenic-detail?id=${scenic.id}`">{{ scenic.scenicName }}</router-link>
        </h3>
        <span class="bg-[#2ecc71] text-white text-xs px-2 py-1 rounded-full">
          {{ scenic.rating }}分
        </span>
      </div>
      <p class="text-gray-600 text-sm mb-3 truncate">{{ scenic.scenicAddress }}</p>
      <router-link 
        :to="`/scenic-detail?id=${scenic.id}`"
        class="inline-block text-[#27ae60] text-sm hover:underline"
      >
        查看详情 →
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

// 定义组件属性
const props = defineProps({
  scenic: {
    type: Object,
    required: true,
    default: () => ({})
  }
})
</script>

<style scoped>
.scenic-card {
  background-color: white;
  cursor: pointer;
}
</style>