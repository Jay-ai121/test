<template>
  <div class="home-container min-h-screen bg-[#f5f7fa]">
    <!-- 公共导航栏 -->
    <NavBar />

    <!-- 轮播图区域（衢州特色风景） -->
    <div class="banner-container w-full h-[500px] md:h-[600px] overflow-hidden">
      <div class="banner-slider relative w-full h-full">
        <img 
          v-for="(banner, index) in bannerList" 
          :key="index"
          :src="banner.imgUrl"
          :alt="banner.title"
          class="w-full h-full object-cover absolute top-0 left-0 transition-opacity duration-1000"
          :class="{ 'opacity-100': currentBannerIndex === index, 'opacity-0': currentBannerIndex !== index }"
        >
        <!-- 轮播文字遮罩 -->
        <div class="absolute bottom-0 left-0 w-full bg-gradient-to-t from-black/70 to-transparent py-12 px-4 md:px-8">
          <h2 class="text-2xl md:text-4xl font-bold text-white mb-2">
            {{ bannerList[currentBannerIndex].title }}
          </h2>
          <p class="text-gray-200 md:text-lg max-w-2xl">
            {{ bannerList[currentBannerIndex].desc }}
          </p>
        </div>
        <!-- 轮播指示器 -->
        <div class="absolute bottom-4 left-1/2 -translate-x-1/2 flex space-x-2">
          <button 
            v-for="(banner, index) in bannerList" 
            :key="index"
            class="w-2 h-2 rounded-full transition-all duration-300"
            :class="{ 'bg-[#2ecc71] w-8': currentBannerIndex === index, 'bg-white/50': currentBannerIndex !== index }"
            @click="switchBanner(index)"
          ></button>
        </div>
      </div>
    </div>

    <!-- 核心内容区 -->
    <div class="content-wrapper max-w-7xl mx-auto px-4 md:px-8 py-8 md:py-12">
      <!-- 衢州概览 -->
      <div class="overview-container bg-white rounded-lg shadow-md p-6 md:p-8 mb-12">
        <h2 class="text-2xl md:text-3xl font-bold text-[#2c3e50] mb-4 flex items-center">
          <svg class="h-6 w-6 text-[#27ae60] mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
          </svg>
          衢州概览
        </h2>
        <p class="text-gray-600 leading-relaxed">
          {{ homeData.quzhouOverview }}
        </p>
      </div>

      <!-- 推荐景点 -->
      <div class="recommend-scenic-container mb-12">
        <h2 class="text-2xl md:text-3xl font-bold text-[#2c3e50] mb-6 flex items-center">
          <svg class="h-6 w-6 text-[#27ae60] mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
          推荐景点
        </h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 md:gap-6">
          <ScenicCard 
            v-for="scenic in homeData.recommendScenics" 
            :key="scenic.id"
            :scenic="scenic"
          />
        </div>
        <div class="mt-6 text-center">
          <router-link 
            to="/scenic-list"
            class="inline-block bg-[#27ae60] text-white px-6 py-3 rounded-lg font-medium hover:bg-[#2ecc71] transition-colors"
          >
            查看全部景点
          </router-link>
        </div>
      </div>

      <!-- 热门攻略 -->
      <div class="hot-guide-container">
        <h2 class="text-2xl md:text-3xl font-bold text-[#2c3e50] mb-6 flex items-center">
          <svg class="h-6 w-6 text-[#27ae60] mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
          </svg>
          热门攻略
        </h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 md:gap-6">
          <div 
            v-for="guide in homeData.hotGuides" 
            :key="guide.id"
            class="guide-card bg-white rounded-lg shadow-md p-4 hover:shadow-lg transition-shadow"
          >
            <h3 class="text-lg font-semibold text-[#2c3e50] mb-2 line-clamp-2 hover:text-[#27ae60] transition-colors">
              {{ guide.guideTitle }}
            </h3>
            <div class="flex justify-between items-center text-gray-500 text-sm mb-3">
              <span>作者：{{ guide.author }}</span>
              <span>浏览：{{ guide.viewCount }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-400 text-xs">发布时间：{{ formatTime(guide.createTime) }}</span>
              <button class="text-[#27ae60] text-sm hover:underline">
                查看详情 →
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 公共页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'
import ScenicCard from '../components/ScenicCard.vue'
import { getHomeData } from '../api/homeApi'
import dayjs from 'dayjs' // 需安装：npm install dayjs
import { onUnmounted } from 'vue'

// 响应式数据
const homeData = ref({
  quzhouOverview: '',
  recommendScenics: [],
  hotGuides: []
})
const bannerList = ref([
  {
    id: 1,
    imgUrl: '@/assets/images/banner1.jpg', // 衢州江郎山图片
    title: '江郎山 - 中国丹霞第一奇峰',
    desc: '三峰耸立，形如石笋天柱，蔚为壮观，世界自然遗产地'
  },
  {
    id: 2,
    imgUrl: '@/assets/images/banner2.jpg', // 衢州南宗孔庙图片
    title: '南宗孔庙 - 东南阙里，儒风浩荡',
    desc: '孔子后裔南迁后的家庙，传承千年儒家文化'
  },
  {
    id: 3,
    imgUrl: '@/assets/images/banner3.jpg', // 衢州龙游石窟图片
    title: '龙游石窟 - 千古之谜，地下奇观',
    desc: '罕见的古代地下人工建筑群，至今未解建造之谜'
  }
])
const currentBannerIndex = ref(0)
let bannerTimer = null

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD')
}

// 切换轮播图
const switchBanner = (index) => {
  currentBannerIndex.value = index
}

// 自动轮播
const autoBanner = () => {
  bannerTimer = setInterval(() => {
    currentBannerIndex.value = (currentBannerIndex.value + 1) % bannerList.value.length
  }, 5000)
}

// 获取首页数据
const loadHomeData = async () => {
  try {
    const res = await getHomeData()
    if (res.code === 200) {
      homeData.value = res.data
    }
  } catch (error) {
    console.error('加载首页数据失败：', error)
  }
}

// 生命周期
onMounted(() => {
  loadHomeData()
  autoBanner()
})

// 组件卸载时清除定时器
onUnmounted(() => {
  clearInterval(bannerTimer)
})
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
}

.banner-slider {
  user-select: none;
}
</style>