<template>
  <div class="ai-trip-container">
    <!-- 公共导航栏 -->
    <NavBar />

    <!-- 核心内容区 -->
    <div class="content-wrapper flex flex-col md:flex-row gap-6 p-4 md:p-8 max-w-7xl mx-auto">
      <!-- 左侧：用户问答区 -->
      <div class="w-full md:w-1/3 bg-white rounded-lg shadow-lg p-6 h-fit">
        <h2 class="text-2xl font-bold text-[#2c3e50] mb-4">AI衢州行程定制</h2>
        <div class="question-box mb-4">
          <textarea
            v-model="userQuestion"
            placeholder="请输入你的衢州旅游需求（如：我想玩3天，偏好人文景点，预算500元/人）"
            class="w-full h-32 p-3 border border-gray-300 rounded-lg resize-none focus:outline-none focus:border-[#2ecc71]"
          ></textarea>
        </div>
        <button
          @click="submitQuestion"
          class="w-full bg-[#2ecc71] text-white py-3 rounded-lg font-medium hover:bg-[#27ae60] transition-colors"
          :disabled="isLoading"
        >
          <span v-if="!isLoading">生成定制行程</span>
          <span v-if="isLoading">生成中...</span>
        </button>

        <!-- AI行程结果展示 -->
        <div v-if="tripData" class="trip-result mt-6">
          <h3 class="text-xl font-semibold text-[#27ae60] mb-2">{{ tripData.tripName }}</h3>
          <p class="text-gray-600 mb-3">行程天数：{{ tripData.days }}天</p>
          <!-- 改为展示每日行程（dailySchedules），匹配后端VO字段 -->
          <div class="routes-list space-y-4">
            <div v-for="(daily, index) in tripData.dailySchedules" :key="index" class="route-item p-3 border border-gray-200 rounded-lg">
              <p class="font-medium">{{ daily.dateDesc }}</p>
              <div class="ml-3 mt-2 space-y-2">
                <div v-for="(item, idx) in daily.scheduleItems" :key="idx" class="text-sm">
                  <p class="text-[#27ae60]">📅 {{ item.timeSlot }}</p>
                  <p class="font-medium">{{ item.scenicName }}</p>
                  <p class="text-gray-600">地址：{{ item.scenicAddress }}</p>
                  <p class="mt-1">{{ item.description }}</p>
                  <p class="text-gray-500 italic">小贴士：{{ item.tips }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：高德地图渲染区 -->
      <div class="w-full md:w-2/3 bg-white rounded-lg shadow-lg p-4 h-[600px]">
        <h2 class="text-2xl font-bold text-[#2c3e50] mb-4">行程路线可视化</h2>
        
        <!-- 新增：路线类型切换按钮 -->
        <div class="route-type-buttons mb-4">
          <button
            :class="{ active: selectedRouteType === 'driving' }"
            @click="changeRouteType('driving')"
            class="px-4 py-2 mr-2 rounded-lg border"
          >
            驾车
          </button>
          <button
            :class="{ active: selectedRouteType === 'walking' }"
            @click="changeRouteType('walking')"
            class="px-4 py-2 mr-2 rounded-lg border"
          >
            步行
          </button>
          <button
            :class="{ active: selectedRouteType === 'transit' }"
            @click="changeRouteType('transit')"
            class="px-4 py-2 rounded-lg border"
          >
            公交
          </button>
        </div>
        
        <!-- 高德地图容器 -->
        <div id="amap-container" class="w-full h-[450px] rounded-lg border border-gray-200"></div>
      </div>
    </div>

    <!-- 公共页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'
import { generateAiTrip } from '../api/aiTripApi'
// 新增：导入axios（如果你的项目用fetch，可替换）
import axios from 'axios'

// 响应式数据
const userQuestion = ref('')
const isLoading = ref(false)
const tripData = ref(null)
let amapInstance = null // 高德地图实例
let markerList = [] // 标记点列表
// 新增：存储所有路线折线（支持多段路线，切换类型时清空）
let routePolylineList = [] 
// 新增：选中的路线类型（默认驾车）
const selectedRouteType = ref('driving')

// 提交用户问题，生成AI行程
const submitQuestion = async () => {
  // 1. 空值校验
  const trimmedQuestion = userQuestion.value?.trim() || ''
  if (!trimmedQuestion) {
    alert('请输入有效的旅游需求！')
    return
  }

  isLoading.value = true
  try {
    // 2. 调用接口：后端接收的是userQuestion参数，按接口要求传对象（修正传参格式）
    const res = await generateAiTrip({ userQuestion: trimmedQuestion })
    
    // 3. 关键修改：后端返回的res.data已经是解析好的对象，无需JSON.parse
    if (res.code === 200 && res.data) {
      tripData.value = res.data // 直接赋值，不用解析
      console.log('行程数据：', tripData.value)
      
      // 渲染高德地图路线（用routeList字段，匹配后端VO）
      if (tripData.value?.routeList && tripData.value.routeList.length > 0) {
        // 新增：调用带路线类型的渲染方法
        renderAmapRoute(tripData.value.routeList, selectedRouteType.value)
      } else {
        alert('AI行程数据中无有效路线信息')
      }
    } else {
      alert(res.msg || '生成行程失败，请检查需求描述')
    }
  } catch (error) {
    console.error('生成行程失败：', error)
    alert('生成行程失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 新增：切换路线类型
const changeRouteType = (type) => {
  selectedRouteType.value = type
  // 重新渲染路线（基于已有的行程数据）
  if (tripData.value?.routeList && tripData.value.routeList.length > 0) {
    renderAmapRoute(tripData.value.routeList, type)
  } else {
    alert('暂无行程数据，请先生成AI行程！')
  }
}

// 初始化高德地图
const initAmap = () => {
  if (window.AMap) {
    createAmapInstance()
  } else {
    const script = document.createElement('script')
    // 注意：替换成你的前端高德Key（如果和后端Key不同）
    script.src = `https://webapi.amap.com/maps?v=2.0&key=2209cb8cb1a64f132e39901c67ce9b90`
    script.onload = createAmapInstance
    document.body.appendChild(script)
  }
}

// 创建高德地图实例
const createAmapInstance = () => {
  if (!window.AMap) {
    console.error('高德地图API加载失败，无法创建地图实例')
    return
  }

  // 初始化地图实例
  amapInstance = new window.AMap.Map('amap-container', {
    zoom: 12,
    center: [118.8750, 28.9783], // 衢州经纬度
    resizeEnable: true
  })

  // 加载插件并添加控件
  window.AMap.plugin(['AMap.Scale', 'AMap.ToolBar', 'AMap.Polyline'], () => {
    const scaleControl = new window.AMap.Scale()
    const toolBarControl = new window.AMap.ToolBar({ position: 'RB' })
    amapInstance.addControl(scaleControl)
    amapInstance.addControl(toolBarControl)
  })

  // 消除Canvas性能提示
  setTimeout(() => {
    const mapCanvas = document.querySelector('#amap-container canvas')
    if (mapCanvas) {
      mapCanvas.setAttribute('willReadFrequently', 'true')
    }
  }, 500)
}

// 新增：调用后端接口获取真实路线数据（核心修复）
const getRealRouteData = async (origin, destination, routeType) => {
  try {
    // 关键修复1：添加 /api 前缀（匹配后端 server.context-path=/api）
    // 完整接口地址：http://localhost:8080/api/route/getRoute
    const res = await axios.get('/api/route/getRoute', {
      params: {
        // 关键修复2：确认参数格式（后端要求：纬度,经度 → lat,lng）
        // origin是 [lng, lat] → 转成 lat,lng 格式
        origin: `${origin[1]},${origin[0]}`, 
        destination: `${destination[1]},${destination[0]}`,
        routeType: routeType
      }
    })
    
    // 关键修复3：适配后端返回格式（ResultVO 的 code 和 data 字段）
    if (res.data.code === 200 && res.data.data) {
      console.log(`成功获取${routeType}路线数据：`, res.data.data)
      return res.data.data // 返回解码后的经纬度数组（["lng1,lat1", "lng2,lat2", ...]）
    } else {
      const errorMsg = res.data.msg || '未知错误'
      console.error(`获取${routeType}路线失败：`, errorMsg)
      alert(`获取${routeType === 'driving' ? '驾车' : routeType === 'walking' ? '步行' : '公交'}路线失败：${errorMsg}`)
      return null
    }
  } catch (error) {
    console.error('调用路线接口异常：', error)
    // 区分网络错误和接口错误
    const errorMsg = error.message.includes('Network Error') ? '网络连接失败，请检查后端服务是否启动' : '接口调用异常'
    alert(`获取${routeType === 'driving' ? '驾车' : routeType === 'walking' ? '步行' : '公交'}路线失败：${errorMsg}`)
    return null
  }
}

// 渲染行程路线到高德地图（核心修改：替换直线为真实交通路线）
const renderAmapRoute = async (routeList, routeType) => {
  if (!amapInstance || !routeList || routeList.length === 0) return

  // 清除之前的标记点和路线
  markerList.forEach(marker => amapInstance.remove(marker))
  markerList = []
  routePolylineList.forEach(polyline => amapInstance.remove(polyline))
  routePolylineList = []

  // 提取经纬度坐标数组（匹配后端routeList字段）
  const lngLatList = []
  routeList.forEach(route => {
    const lng = parseFloat(route.longitude)
    const lat = parseFloat(route.latitude)
    // 过滤无效经纬度
    if (isNaN(lng) || isNaN(lat)) {
      console.warn('无效经纬度：', route)
      return
    }
    lngLatList.push([lng, lat])

    // 创建景点标记点
    const marker = new window.AMap.Marker({
      position: [lng, lat],
      title: route.name || route.scenicName, // 兼容两种字段名
      label: {
        content: route.name || route.scenicName,
        offset: new window.AMap.Pixel(0, 30),
        direction: 'top'
      },
      icon: new window.AMap.Icon({
        image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
        size: new window.AMap.Size(30, 30)
      })
    })
    markerList.push(marker)
    amapInstance.add(marker)

    // 标记点点击事件：显示详情
    marker.on('click', () => {
      const infoWindow = new window.AMap.InfoWindow({
        content: `
          <div style="padding: 10px; width: 200px;">
            <h3 style="margin: 0 0 5px 0; color: #27ae60;">${route.name || route.scenicName}</h3>
            <p style="margin: 0; font-size: 12px;">日期：第${route.day}天</p>
            <p style="margin: 0; font-size: 12px;">时间：${route.startTime} - ${route.endTime}</p>
            <p style="margin: 0; font-size: 12px;">地址：${route.scenicAddress || '暂无地址'}</p>
            <p style="margin: 5px 0 0 0; font-size: 12px;">描述：${route.description || '暂无描述'}</p>
          </div>
        `,
        offset: new window.AMap.Pixel(0, -30)
      })
      infoWindow.open(amapInstance, [lng, lat])
    })
  })

  // 新增：绘制真实交通路线（替代原来的直线）
  if (lngLatList.length >= 2) {
    // 循环绘制每两个景点之间的路线（A→B，B→C...）
    for (let i = 0; i < lngLatList.length - 1; i++) {
      const start = lngLatList[i] // [lng, lat]
      const end = lngLatList[i + 1]
      console.log(`请求路线：${start} → ${end}，类型：${routeType}`)
      // 调用后端接口获取真实路线数据
      const realRoutePoints = await getRealRouteData(start, end, routeType)
      if (realRoutePoints && realRoutePoints.length > 0) {
        // 转换为高德地图需要的 [[lng1, lat1], [lng2, lat2]] 格式
        const path = realRoutePoints.map(point => {
          const [lng, lat] = point.split(',').map(Number)
          return [lng, lat]
        }).filter(([lng, lat]) => !isNaN(lng) && !isNaN(lat))

        if (path.length === 0) {
          console.warn('路线数据解析失败，无有效坐标')
          continue
        }

        // 设置不同路线类型的样式
        let lineStyle = {}
        switch (routeType) {
          case 'driving': // 驾车：绿色实线
            lineStyle = {
              strokeColor: '#2ecc71',
              strokeWeight: 5,
              strokeOpacity: 0.8,
              strokeStyle: 'solid'
            }
            break
          case 'walking': // 步行：橙色虚线
            lineStyle = {
              strokeColor: '#FF6700',
              strokeWeight: 4,
              strokeOpacity: 0.8,
              strokeStyle: 'dashed'
            }
            break
          case 'transit': // 公交：蓝色实线
            lineStyle = {
              strokeColor: '#1E90FF',
              strokeWeight: 4,
              strokeOpacity: 0.8,
              strokeStyle: 'solid'
            }
            break
        }
        // 创建真实路线折线
        const polyline = new window.AMap.Polyline({
          path: path, // 真实路线经纬度数组（修正格式）
          ...lineStyle
        })
        routePolylineList.push(polyline)
        amapInstance.add(polyline)
      }
    }
  }

  // 地图视野自适应所有标记点
  amapInstance.setFitView(markerList)
}

// 生命周期：挂载时初始化地图
onMounted(() => {
  initAmap()
})

// 生命周期：卸载时销毁地图实例
onUnmounted(() => {
  if (amapInstance) {
    amapInstance.destroy()
    amapInstance = null
  }
})
</script>

<style scoped>
.ai-trip-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 新增：路线类型切换按钮样式 */
.route-type-buttons button {
  cursor: pointer;
  transition: all 0.2s;
}
.route-type-buttons button.active {
  background-color: #2ecc71;
  color: white;
  border-color: #2ecc71;
}
.route-type-buttons button:hover:not(.active) {
  border-color: #2ecc71;
  color: #2ecc71;
}
</style>