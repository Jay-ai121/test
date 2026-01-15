/**
 * 高德地图辅助工具类
 * 路径：src/utils/amapUtil.js
 * 依赖：高德地图 JS API（需在 index.html 中引入：<script type="text/javascript" src="https://webapi.amap.com/maps?v=2.0&key=你的高德地图Key"></script>）
 */

/**
 * 高德地图 Key（请替换为你自己的 Key，从高德开放平台申请：https://lbs.amap.com/）
 */
export const AMAP_KEY = '2209cb8cb1a64f132e39901c67ce9b90'

/**
 * 初始化地图实例
 * @param {String} containerId 地图容器ID（如：'amap-container'）
 * @param {Object} options 地图配置项（可选，默认居中衢州，缩放级别12）
 * @returns {AMap.Map} 高德地图实例
 */
export const initAmap = (containerId, options = {}) => {
  // 校验高德地图 API 是否加载
  if (!window.AMap) {
    console.error('高德地图 JS API 未加载，请先在 index.html 中引入')
    return null
  }

  // 默认配置
  const defaultOptions = {
    zoom: 12, // 地图缩放级别
    center: [118.88929, 28.97953], // 衢州默认经纬度（可根据需求调整）
    resizeEnable: true, // 允许地图跟随容器大小调整
    scrollWheel: true, // 允许鼠标滚轮缩放地图
    keyboard: true // 允许键盘控制地图
  }

  // 合并配置项
  const mapOptions = { ...defaultOptions, ...options }

  // 创建并返回地图实例
  const map = new window.AMap.Map(containerId, mapOptions)
  // 加载插件（如缩放控件、定位控件）
  map.addControl(new window.AMap.Scale()) // 比例尺控件
  map.addControl(new window.AMap.ToolBar({ position: 'RB' })) // 工具栏控件（右下角）
  return map
}

/**
 * 渲染行程路线（多点连续路线）
 * @param {AMap.Map} map 地图实例
 * @param {Array} routeList 行程路线列表（格式：[{ longitude: 118.88, latitude: 28.98 }, ...]）
 * @param {Object} lineOptions 路线样式配置（可选）
 * @returns {AMap.Polyline} 路线实例
 */
export const renderTripRoute = (map, routeList, lineOptions = {}) => {
  if (!map || !routeList || routeList.length < 2) {
    console.warn('地图实例不存在或路线点数量不足，无法渲染路线')
    return null
  }

  // 转换坐标格式（高德地图要求：[经度, 纬度] 数组集合）
  const path = routeList.map(item => [item.longitude, item.latitude])

  // 默认路线样式
  const defaultLineOptions = {
    path: path,
    strokeColor: '#27ae60', // 路线颜色（与项目主色调一致）
    strokeWeight: 4, // 路线宽度
    strokeOpacity: 0.8, // 路线透明度
    strokeStyle: 'solid', // 路线样式（实线）
    lineJoin: 'round' // 线段连接处样式
  }

  // 合并样式配置
  const polylineOptions = { ...defaultLineOptions, ...lineOptions }

  // 创建路线实例并添加到地图
  const polyline = new window.AMap.Polyline(polylineOptions)
  map.add(polyline)

  // 自动调整地图视野，使路线完全显示
  map.setFitView([polyline])

  return polyline
}

/**
 * 添加景点标记点
 * @param {AMap.Map} map 地图实例
 * @param {Array} routeList 行程路线列表（格式：[{ longitude: 118.88, latitude: 28.98, scenicName: '江郎山' }, ...]）
 * @param {Object} markerOptions 标记点样式配置（可选）
 * @returns {Array} 标记点实例集合
 */
export const addScenicMarkers = (map, routeList, markerOptions = {}) => {
  if (!map || !routeList || routeList.length === 0) {
    console.warn('地图实例不存在或无景点数据，无法添加标记点')
    return []
  }

  const markerList = [] // 存储标记点实例

  // 默认标记点样式
  const defaultMarkerOptions = {
    icon: new window.AMap.Icon({
      size: new window.AMap.Size(36, 36), // 标记点大小
      image: '/src/assets/images/scenic-marker.png', // 自定义标记点图片（可替换）
      imageSize: new window.AMap.Size(36, 36)
    }),
    offset: new window.AMap.Pixel(-18, -36) // 标记点偏移（使图标底部对准经纬度）
  }

  // 遍历路线列表，添加标记点
  routeList.forEach((item, index) => {
    const { longitude, latitude, scenicName } = item
    if (!longitude || !latitude) return

    // 合并标记点配置
    const markerConfig = {
      ...defaultMarkerOptions,
      ...markerOptions,
      position: [longitude, latitude], // 标记点位置
      title: scenicName || `景点${index + 1}` // 标记点标题
    }

    // 创建标记点实例
    const marker = new window.AMap.Marker(markerConfig)
    // 添加点击事件（显示景点名称弹窗）
    marker.on('click', () => {
      const infoWindow = new window.AMap.InfoWindow({
        content: `<div style="padding: 8px 12px;"><b>${scenicName || `景点${index + 1}`}</b></div>`,
        offset: new window.AMap.Pixel(0, -36)
      })
      infoWindow.open(map, marker.getPosition())
    })

    // 添加到地图和实例集合
    map.add(marker)
    markerList.push(marker)
  })

  return markerList
}

/**
 * 坐标转换（GPS坐标 -> 高德坐标，兼容部分设备定位偏差）
 * @param {Number} lng 经度
 * @param {Number} lat 纬度
 * @returns {Promise<Object>} 转换后的坐标 { longitude, latitude }
 */
export const convertCoord = (lng, lat) => {
  return new Promise((resolve, reject) => {
    if (!window.AMap) {
      reject(new Error('高德地图 JS API 未加载'))
      return
    }

    // 创建坐标转换实例
    const convertor = new window.AMap.Convertor()
    const coords = [[lng, lat]] // 待转换坐标

    // 转换类型：1-GPS坐标 -> 高德坐标
    convertor.convert(coords, 1, 3, (status, result) => {
      if (status === 'complete' && result.info === 'OK') {
        const convertedCoord = result.locations[0]
        resolve({
          longitude: convertedCoord.lng,
          latitude: convertedCoord.lat
        })
      } else {
        reject(new Error('坐标转换失败'))
      }
    })
  })
}