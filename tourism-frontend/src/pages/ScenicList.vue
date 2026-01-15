<template>
  <div class="scenic-list-container min-h-screen bg-[#f5f7fa]">
    <!-- 公共导航栏 -->
    <NavBar />

    <!-- 核心内容区 -->
    <div class="content-wrapper max-w-7xl mx-auto px-4 md:px-8 py-8 md:py-12">
      <!-- 页面标题 & 搜索框（可选） -->
      <div class="page-header flex flex-col md:flex-row justify-between items-start md:items-center mb-8">
        <h2 class="text-2xl md:text-3xl font-bold text-[#2c3e50] mb-4 md:mb-0">
          衢州景点大全
        </h2>
        <div class="search-box w-full md:w-64 relative">
          <input
            type="text"
            placeholder="搜索景点名称..."
            class="w-full p-3 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:border-[#27ae60]"
          >
          <svg class="h-5 w-5 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
        </div>
      </div>

      <!-- 景点列表 -->
      <div class="scenic-grid grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
        <ScenicCard 
          v-for="scenic in scenicList" 
          :key="scenic.id"
          :scenic="scenic"
        />
      </div>

      <!-- 分页组件 -->
      <div class="pagination-container mt-8 flex justify-center">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalCount"
          background
        >
        </el-pagination>
      </div>
    </div>

    <!-- 公共页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'
import ScenicCard from '../components/ScenicCard.vue'
import { ElPagination } from 'element-plus' // 引入Element Plus分页组件
import { getScenicList } from '../api/scenicApi'

// 响应式数据
const scenicList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadScenicList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadScenicList()
}

// 加载景点列表
const loadScenicList = async () => {
  try {
    const res = await getScenicList(pageNum.value, pageSize.value)
    if (res.code === 200) {
      scenicList.value = res.data.scenicList
      totalCount.value = res.data.totalCount
    }
  } catch (error) {
    console.error('加载景点列表失败：', error)
  }
}

// 生命周期
onMounted(() => {
  loadScenicList()
})
</script>

<style scoped>
.scenic-list-container {
  display: flex;
  flex-direction: column;
}
</style>