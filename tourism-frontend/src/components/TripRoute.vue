<template>
  <div class="trip-route-container bg-white rounded-lg shadow-md p-4 md:p-6 overflow-hidden">
    <!-- 组件标题 -->
    <div class="route-header flex justify-between items-center mb-4">
      <h3 class="text-lg md:text-xl font-bold text-[#2c3e50] flex items-center">
        <svg class="h-5 w-5 text-[#27ae60] mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a2 2 0 01-2-2v-1a1 1 0 00-1-1H4a1 1 0 00-1 1v1a2 2 0 01-2 2H0v-6a2 2 0 012-2h2V6a2 2 0 012-2h6V2h4v2h6a2 2 0 012 2v2z" />
        </svg>
        {{ title || "衢州行程路线详情" }}
      </h3>
      <!-- 路线天数标签 -->
      <el-tag v-if="tripDays" type="success" size="small">
        {{ tripDays }}天行程
      </el-tag>
    </div>

    <!-- 路线列表 -->
    <div class="route-list space-y-3 md:space-y-4 max-h-[400px] overflow-y-auto pr-2">
      <!-- 空数据占位 -->
      <div v-if="routeList.length === 0" class="empty-route text-center py-8 text-gray-500">
        <svg class="h-12 w-12 mx-auto mb-2 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
        </svg>
        <p>暂无行程路线数据，请先生成定制行程</p>
      </div>

      <!-- 路线项 -->
      <div 
        v-for="(route, index) in routeList" 
        :key="index"
        class="route-item border border-gray-200 rounded-lg p-3 hover:border-[#27ae60] hover:shadow-sm transition-all"
      >
        <!-- 天数标识 -->
        <div class="day-tag mb-2">
          <span class="inline-block bg-[#2ecc71] text-white text-xs px-2 py-1 rounded-full">
            第{{ route.day }}天
          </span>
        </div>

        <!-- 路线核心信息 -->
        <div class="route-info grid grid-cols-1 md:grid-cols-12 gap-3">
          <!-- 景点图片 -->
          <div class="md:col-span-3 scenic-img-container h-24 md:h-28 rounded overflow-hidden">
            <img 
              :src="route.scenicImg || defaultScenicImg" 
              :alt="route.scenicName"
              class="w-full h-full object-cover"
            >
          </div>

          <!-- 景点详情 -->
          <div class="md:col-span-9 route-detail">
            <div class="flex justify-between items-start mb-1">
              <h4 class="text-base md:text-lg font-semibold text-[#2c3e50] hover:text-[#27ae60] transition-colors">
                {{ route.scenicName }}
              </h4>
              <span class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded">
                {{ route.rating || "4.5" }}分
              </span>
            </div>

            <!-- 地址 -->
            <p class="text-sm text-gray-600 mb-2 flex items-center">
              <svg class="h-4 w-4 text-gray-400 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              {{ route.scenicAddress || "暂无详细地址" }}
            </p>

            <!-- 时间 -->
            <p class="text-sm text-gray-600 mb-2 flex items-center">
              <svg class="h-4 w-4 text-gray-400 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              游玩时间：{{ route.startTime || "09:00" }} - {{ route.endTime || "17:00" }}
            </p>

            <!-- 游玩描述 -->
            <div class="route-desc">
              <p class="text-sm text-gray-700 line-clamp-2">
                {{ route.description || "暂无详细游玩描述，可前往景点现场探索更多乐趣~" }}
              </p>
              <button 
                class="mt-1 text-xs text-[#27ae60] hover:underline"
                @click="toggleDescExpand(index)"
              >
                {{ isDescExpand[index] ? "收起" : "查看更多" }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, computed } from 'vue'
import { ElTag } from 'element-plus'

// 定义组件属性
const props = defineProps({
  // 组件标题（可选）
  title: {
    type: String,
    default: ''
  },
  // 行程天数（可选）
  tripDays: {
    type: Number,
    default: 0
  },
  // 行程路线列表（必传，与AI行程VO结构一致）
  routeList: {
    type: Array,
    required: true,
    default: () => []
  }
})

// 定义组件事件（可选，如需向外传递事件可扩展）
const emits = defineEmits(['viewScenicDetail'])

// 响应式数据：控制描述是否展开
const isDescExpand = ref([])

// 默认景点图片
const defaultScenicImg = ref('@/assets/images/default-scenic.jpg')

// 切换描述展开/收起状态
const toggleDescExpand = (index) => {
  // 初始化数组长度，避免索引越界
  if (isDescExpand.value.length <= index) {
    isDescExpand.value = new Array(props.routeList.length).fill(false)
  }
  isDescExpand.value[index] = !isDescExpand.value[index]
}
</script>

<style scoped>
.trip-route-container {
  transition: all 0.3s ease;
}

.route-list {
  scrollbar-width: thin;
  scrollbar-color: #27ae60 #f5f7fa;
}

.route-list::-webkit-scrollbar {
  width: 6px;
}

.route-list::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 3px;
}

.route-list::-webkit-scrollbar-thumb {
  background-color: #27ae60;
  border-radius: 3px;
}

.route-item {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 文字超出隐藏（兼容多浏览器） */
.line-clamp-2 {
  display: -webkit-box;
  /* 私有属性（兼容Chrome/Safari） */
  -webkit-line-clamp: 2;
  /* 补充标准属性（消除警告+兼容现代浏览器） */
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>