<template>
  <div class="route-detail-container">
    <!-- 顶部导航 -->
    <div class="page-header">
      <el-button icon="el-icon-arrow-left" @click="goBack" class="back-btn">返回</el-button>
      <h2 class="page-title">{{ routeDetail ? routeDetail.recommendationName : '行程详情' }}</h2>
      <el-button 
        v-if="routeDetail"
        type="primary" 
        icon="el-icon-download" 
        @click="exportPDF"
        :loading="exporting"
        class="export-btn">
        {{ exporting ? '导出中...' : '导出PDF' }}
      </el-button>
    </div>

    <div v-loading="loading">
      <template v-if="routeDetail">
        <!-- 顶部行程概览卡片 -->
        <div class="overview-card">
          <div class="card-content">
            <div class="overview-grid">
              <div class="grid-item days">
                <div class="icon-wrapper">
                  <i class="el-icon-time"></i>
                </div>
                <div class="info">
                  <div class="label">旅行天数</div>
                  <div class="value">{{ routeDetail.summary.days }}天</div>
                </div>
              </div>
              <div class="grid-item budget">
                <div class="icon-wrapper">
                  <i class="el-icon-wallet"></i>
                </div>
                <div class="info">
                  <div class="label">预算</div>
                  <div class="value">¥{{ routeDetail.summary.budget }}</div>
                </div>
              </div>
              <div class="grid-item cost">
                <div class="icon-wrapper">
                  <i class="el-icon-money"></i>
                </div>
                <div class="info">
                  <div class="label">预估费用</div>
                  <div :class="['value', getCostClass(routeDetail.summary)]">¥{{ routeDetail.summary.estimatedCost }}</div>
                </div>
              </div>
              <div class="grid-item distance">
                <div class="icon-wrapper">
                  <i class="el-icon-odometer"></i>
                </div>
                <div class="info">
                  <div class="label">总距离</div>
                  <div class="value">{{ routeDetail.summary.totalDistance }}km</div>
                </div>
              </div>
              <div class="grid-item attractions">
                <div class="icon-wrapper">
                  <i class="el-icon-location-outline"></i>
                </div>
                <div class="info">
                  <div class="label">景点数量</div>
                  <div class="value">{{ routeDetail.summary.totalAttractions }}个</div>
                </div>
              </div>
              <div class="grid-item hotels">
                <div class="icon-wrapper">
                  <i class="el-icon-office-building"></i>
                </div>
                <div class="info">
                  <div class="label">酒店数量</div>
                  <div class="value">{{ routeDetail.summary.totalHotels }}家</div>
                </div>
              </div>
              <div class="grid-item duration">
                <div class="icon-wrapper">
                  <i class="el-icon-timer"></i>
                </div>
                <div class="info">
                  <div class="label">总时长</div>
                  <div class="value">{{ formatDuration(routeDetail.summary.totalDuration) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- PDF 导出专用容器 - 包含天数标签和所有天的内容 -->
        <div ref="pdfContent" class="pdf-export-container">
          <!-- 天数切换标签 -->
          <div class="day-tabs-wrapper print-include">
            <div class="day-tabs-container">
              <div 
                v-for="day in routeDetail.itinerary" 
                :key="day.dayNum"
                :class="['day-tab', { 'active': activeDay === day.dayNum.toString() }]"
                @click="activeDay = day.dayNum.toString()">
                第{{ day.dayNum }}天
              </div>
            </div>
          </div>

          <!-- 显示当前选中天的行程内容（PDF 导出时显示全部） -->
          <div class="all-days-content">
            <div v-if="currentDay" class="day-section">
              <!-- 天数标题 -->
              <div class="day-title">
                <h3>第{{ currentDay.dayNum }}天行程</h3>
              </div>
              
              <!-- 行程时间线 -->
              <div class="itinerary-section">
                <div class="timeline-container">
                  <div v-for="(item, index) in currentDay.items" :key="index" class="timeline-item">
                      <!-- 时间线标记 -->
                      <div class="timeline-marker">
                        <div :class="['marker-dot', getTypeClass(item.type)]">
                          <i :class="getTypeIcon(item.type)"></i>
                        </div>
                        <div class="marker-line" v-if="index < currentDay.items.length - 1"></div>
                      </div>
                    
                      <!-- 行程卡片内容 -->
                      <div class="timeline-content">
                        <!-- 景点节点 -->
                        <div v-if="item.type === 'attraction'" class="content-card attraction-card" @click="goToAttractionDetail(item.id || item.attractionId)">
                          <div class="card-category-tag attraction">
                            <i class="el-icon-location-outline"></i>
                            <span>景点</span>
                          </div>
                          <div class="card-main">
                            <div class="card-header">
                              <h3 class="card-title">{{ item.attractionName || '暂无数据' }}</h3>
                            </div>
                            <div class="card-body">
                              <div class="info-tags">
                                <div class="info-tag">
                                  <i class="el-icon-time"></i>
                                  <span>建议游玩：{{ formatDuration(item.playDuration) }}</span>
                                </div>
                                <div class="info-tag price">
                                  <i class="el-icon-ticket"></i>
                                  <span v-if="item.ticketPrice == 0" class="free-badge">免费</span>
                                  <span v-else>¥{{ item.ticketPrice || '暂无数据' }}</span>
                                </div>
                              </div>
                              <div v-if="item.description" class="description" :class="{ 'collapsed': !item.showFull }">
                                {{ item.description }}
                              </div>
                              <div v-if="item.description && item.description.length > 100" class="expand-btn" @click.stop="toggleDescription(item)">
                                {{ item.showFull ? '收起' : '展开' }}
                                <i :class="item.showFull ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                              </div>
                              <div v-else-if="!item.description" class="description empty-text">暂无简介</div>
                              <div v-if="item.images && getImages(item.images).length > 0" class="images">
                                <el-carousel :interval="4000" type="card" height="200px">
                                  <el-carousel-item v-for="(img, imgIndex) in getImages(item.images)" :key="imgIndex">
                                    <img :src="img" class="carousel-image" />
                                  </el-carousel-item>
                                </el-carousel>
                              </div>
                            </div>
                          </div>
                        </div>

                        <!-- 美食节点 -->
                        <div v-else-if="item.type === 'food'" class="content-card food-card" @click="handleFoodClick(item)">
                          <div class="card-category-tag food">
                            <i class="el-icon-food"></i>
                            <span>美食</span>
                          </div>
                          <div class="card-main">
                            <div class="card-header">
                              <h3 class="card-title">{{ item.foodName || '暂无数据' }}</h3>
                            </div>
                            <div class="card-body">
                              <div class="info-tags">
                                <div class="info-tag">
                                  <i class="el-icon-shop"></i>
                                  <span>{{ item.shopName || '暂无数据' }}</span>
                                </div>
                                <div class="info-tag price">
                                  <i class="el-icon-money"></i>
                                  <span>¥{{ item.foodPrice || '暂无数据' }}/人</span>
                                </div>
                              </div>
                              <div v-if="item.description" class="description" :class="{ 'collapsed': !item.showFull }">
                                {{ item.description }}
                              </div>
                              <div v-if="item.description && item.description.length > 100" class="expand-btn" @click.stop="toggleDescription(item)">
                                {{ item.showFull ? '收起' : '展开' }}
                                <i :class="item.showFull ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                              </div>
                              <div v-else-if="!item.description" class="description empty-text">暂无简介</div>
                            </div>
                          </div>
                        </div>

                        <!-- 酒店节点 -->
                        <div v-else-if="item.type === 'hotel'" class="content-card hotel-card" @click="goToHotelDetail(item.id || item.hotelId)">
                          <div class="card-category-tag hotel">
                            <i class="el-icon-office-building"></i>
                            <span>住宿</span>
                          </div>
                          <div class="card-main">
                            <div class="card-header">
                              <h3 class="card-title">{{ item.hotelName || '暂无数据' }}</h3>
                            </div>
                            <div class="card-body">
                              <div class="info-tags">
                                <div class="info-tag">
                                  <i class="el-icon-s-home"></i>
                                  <span>{{ item.roomName || '暂无数据' }}</span>
                                </div>
                                <div class="info-tag price">
                                  <i class="el-icon-money"></i>
                                  <span>¥{{ item.roomPrice || '暂无数据' }}/晚</span>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>

                        <!-- 交通路线节点 -->
                        <div v-else-if="item.type === 'route'" class="content-card route-card">
                          <div class="card-category-tag route">
                            <i class="el-icon-direction"></i>
                            <span>交通</span>
                          </div>
                          <div class="card-main">
                            <div class="card-header">
                              <h3 class="card-title">交通路线</h3>
                              <div class="card-actions">
                                <el-button 
                                  v-if="item.toLocation"
                                  size="small" 
                                  type="primary" 
                                  icon="el-icon-position"
                                  :loading="item.navLoading"
                                  @click.stop="openNavigation(item)">
                                  查看导航
                                </el-button>
                              </div>
                            </div>
                            <div class="card-body">
                              <div class="route-path">
                                <span class="from">{{ item.fromLocation || '起点' }}</span>
                                <i class="el-icon-right"></i>
                                <span class="to">{{ item.toLocation || '终点' }}</span>
                              </div>
                              <div class="info-tags">
                                <div class="info-tag">
                                  <i class="el-icon-truck"></i>
                                  <el-tag size="mini">{{ getTransportText(item.transportType) }}</el-tag>
                                </div>
                                <div class="info-tag">
                                  <i class="el-icon-map-location"></i>
                                  <span>{{ item.distance ? item.distance + 'km' : '暂无数据' }}</span>
                                </div>
                                <div class="info-tag">
                                  <i class="el-icon-time"></i>
                                  <span>{{ item.duration ? formatDuration(item.duration) : '暂无数据' }}</span>
                                </div>
                                <div class="info-tag price">
                                  <i class="el-icon-money"></i>
                                  <span>¥{{ item.cost || '0' }}</span>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
          </div>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="暂无数据">
        <el-button type="primary" @click="goBack">返回上一页</el-button>
      </el-empty>
      
      <div v-else-if="loadError" class="error-state">
        <i class="el-icon-warning-outline error-icon"></i>
        <p class="error-text">加载失败，请稍后重试</p>
        <el-button type="primary" @click="loadRouteDetail">重新加载</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import config from '@/config/config'

export default {
  name: 'RouteDetail',
  data() {
    return {
      routeDetail: null,
      loading: false,
      loadError: false,
      activeDay: '1',
      exporting: false // PDF 导出状态
    }
  },
  computed: {
    // 当前选中的天的行程项
    currentDayItems() {
      if (!this.routeDetail || !this.routeDetail.itinerary) return []
      const day = this.routeDetail.itinerary.find(d => d.dayNum.toString() === this.activeDay)
      return day ? day.items : []
    },
    // 当前选中的天
    currentDay() {
      if (!this.routeDetail || !this.routeDetail.itinerary) return null
      return this.routeDetail.itinerary.find(d => d.dayNum.toString() === this.activeDay)
    }
  },
  mounted() {
    this.loadRouteDetail()
  },
  methods: {
    // 获取类型样式类
    getTypeClass(type) {
      const classMap = {
        'attraction': 'attraction',
        'food': 'food',
        'hotel': 'hotel',
        'route': 'route'
      }
      return classMap[type] || ''
    },

    // 获取类型图标
    getTypeIcon(type) {
      const iconMap = {
        'attraction': 'el-icon-location-outline',
        'food': 'el-icon-food',
        'hotel': 'el-icon-office-building',
        'route': 'el-icon-direction'
      }
      return iconMap[type] || 'el-icon-more'
    },

    // 切换描述展开/收起
    toggleDescription(item) {
      this.$set(item, 'showFull', !item.showFull)
    },

    // 加载路线详情
    async loadRouteDetail() {
      const recommendationId = this.$route.params.id
      if (!recommendationId) {
        this.$message.error('缺少路线ID')
        this.goBack()
        return
      }

      this.loading = true
      this.loadError = false
      try {
        const res = await request({
          url: config.backHost + `/api/routeRecommendation/detail/${recommendationId}`,
          method: 'POST'
        })
        if (res.code === 200 && res.data) {
          this.routeDetail = res.data
          this.activeDay = '1'
        } else {
          this.$message.error(res.msg || '加载失败')
          this.loadError = true
        }
      } catch (error) {
        console.error('加载路线详情失败:', error)
        this.$message.error('加载路线详情失败')
        this.loadError = true
      } finally {
        this.loading = false
      }
    },

    // 返回
    goBack() {
      this.$router.go(-1)
    },

    // 删除路线
    deleteRoute() {
      const recommendationId = this.$route.params.id
      this.$confirm('确定要删除这条行程记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await request({
            url: config.backHost + `/api/routeRecommendation/delete/${recommendationId}`,
            method: 'POST'
          })
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.goBack()
          } else {
            this.$message.error(res.msg || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    // 获取图片数组
    getImages(imagesStr) {
      if (!imagesStr) return []
      try {
        return JSON.parse(imagesStr)
      } catch (e) {
        return imagesStr.split(',').filter(img => img)
      }
    },

    // 获取交通方式文本
    getTransportText(type) {
      const map = {
        'drive': '自驾',
        'bus': '公交',
        'taxi': '打车',
        'walking': '步行',
        'walk': '步行'
      }
      return map[type] || type
    },

    // 格式化时长
    formatDuration(minutes) {
      if (!minutes && minutes !== 0) return '暂无数据'
      const hours = Math.floor(minutes / 60)
      const mins = minutes % 60
      if (hours > 0 && mins > 0) {
        return `${hours}小时${mins}分钟`
      } else if (hours > 0) {
        return `${hours}小时`
      } else {
        return `${mins}分钟`
      }
    },

    // 获取费用样式类
    getCostClass(summary) {
      if (summary.estimatedCost <= summary.budget) {
        return 'cost under-budget'
      } else {
        return 'cost over-budget'
      }
    },

    // 跳转到景点详情
    goToAttractionDetail(attractionId) {
      if (attractionId) {
        this.$router.push({
          name: 'attractionDetail',
          query: { id: attractionId }
        })
      } else {
        this.$message.warning('景点信息不完整')
      }
    },

    // 跳转到酒店详情
    goToHotelDetail(hotelId) {
      if (hotelId) {
        this.$router.push({
          name: 'hotelInfoDetail',
          query: { id: hotelId }
        })
      } else {
        this.$message.warning('酒店信息不完整')
      }
    },

    // 跳转到美食详情
    goToFoodDetail(foodId) {
      if (foodId) {
        this.$router.push({
          name: 'foodDetail',
          query: { id: foodId }
        })
      } else {
        this.$message.warning('美食信息不完整')
      }
    },

    // 处理美食卡片点击
    handleFoodClick(item) {
      // 后端返回的字段名是 foodInfoId
      const foodId = item.foodInfoId || item.id || item.foodId
      this.goToFoodDetail(foodId)
    },

    // 打开地图导航
    openNavigation(item) {
      // 验证坐标数据完整性
      if (!item.fromLongitude || !item.fromLatitude || !item.toLongitude || !item.toLatitude) {
        this.$message.warning('路线坐标信息不完整，无法导航')
        return
      }
      
      // 构建坐标字符串（经度,纬度）
      const fromCoord = `${item.fromLongitude},${item.fromLatitude}`
      const toCoord = `${item.toLongitude},${item.toLatitude}`
      
      // 确定交通方式
      let mode = 'drive'
      if (item.transportType === 'bus') {
        mode = 'bus'
      } else if (item.transportType === 'walk' || item.transportType === 'walking') {
        mode = 'walk'
      }
      
      // 构建高德地图导航URL
      const amapUrl = `https://uri.amap.com/navigation?from=${fromCoord}&to=${toCoord}&mode=${mode}&policy=0&src=贵阳旅游攻略系统`
      
      // 在新窗口打开导航
      window.open(amapUrl, '_blank')
    },

    // 导出PDF
    async exportPDF() {
      if (!this.routeDetail) {
        this.$message.warning('暂无数据可导出')
        return
      }

      this.exporting = true
      const loading = this.$loading({ text: '正在生成PDF...' })

      try {
        // 动态导入库
        const html2canvas = (await import('html2canvas')).default
        const jsPDF = (await import('jspdf')).default
        
        // 1. 锁定截图范围，必须包含「天数标签」的外层容器
        const element = this.$refs.pdfContent
        
        if (!element) {
          throw new Error('无法获取导出内容')
        }

        // 2. 关键配置：防止固定/定位元素被裁剪
        const canvas = await html2canvas(element, {
          scale: 2, // 提升清晰度
          useCORS: true, // 处理跨域图片
          scrollX: 0,
          scrollY: -window.scrollY, // 把页面滚动条归位，避免顶部元素被裁
          windowWidth: element.scrollWidth,
          windowHeight: element.scrollHeight,
          backgroundColor: '#fcf9f7', // 保持页面背景色
          logging: false,
          allowTaint: true,
          ignoreElements: (el) => {
            // 隐藏不需要的元素
            return el.classList.contains('no-print') || 
                   el.classList.contains('card-actions') ||
                   el.classList.contains('expand-btn')
          }
        })

        // 3. 生成PDF，A4分页适配
        const imgData = canvas.toDataURL('image/png')
        const pdf = new jsPDF('p', 'mm', 'a4')
        
        const imgWidth = 210 // A4宽度
        const pageHeight = 297 // A4高度
        const imgHeight = (canvas.height * imgWidth) / canvas.width
        
        let heightLeft = imgHeight
        let position = 0

        // 第一页
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
        heightLeft -= pageHeight

        // 后续分页（如果行程太长超过A4）
        while (heightLeft >= 0) {
          position = heightLeft - imgHeight
          pdf.addPage()
          pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
          heightLeft -= pageHeight
        }

        // 4. 下载PDF
        pdf.save(`${this.routeDetail.recommendationName || '贵阳旅游行程'}.pdf`)
        
        this.$message.success('PDF导出成功！')
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败: ' + (error.message || '请重试'))
      } finally {
        loading.close()
        this.exporting = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
// 品牌主色
$primary-color: #FF7A2F;
$primary-hover: #e66a24;

// 辅助色
$attraction-color: #67c23a;
$food-color: #e6a23c;
$hotel-color: #409eff;
$route-color: #909399;

// 文字颜色
$text-primary: #333;
$text-secondary: #666;
$text-tertiary: #999;
$text-placeholder: #ccc;

// 背景色
$bg-light: #f8f9fa;
$bg-white: #fff;
$pageBack: #fcf9f7;

// 边框
$border-color: #ebeef5;
$border-light: #e0e0e0;

// 阴影
$shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.05);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.1);

.route-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: $pageBack;
  min-height: calc(100vh - 72px);

  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding: 15px 0;
    
    .back-btn {
      margin-right: 15px;
      border-radius: 8px;
      transition: all 0.3s;
      
      &:hover {
        transform: translateX(-2px);
        box-shadow: $shadow-sm;
      }
    }
    
    .page-title {
      color: $text-primary;
      margin: 0;
      font-size: 20px;
      font-weight: bold;
      flex: 1;
    }
    
    .export-btn {
      margin-left: 15px;
      border-radius: 8px;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: $shadow-sm;
      }
    }
  }

  // 顶部行程概览卡片
  .overview-card {
    background: $bg-white;
    border-radius: 8px;
    box-shadow: $shadow-sm;
    border: 1px solid $border-color;
    margin-bottom: 20px;
    transition: all 0.3s;
    
    &:hover {
      box-shadow: $shadow-md;
      transform: translateY(-2px);
    }
    
    .card-content {
      padding: 24px;
      
      .overview-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
        gap: 20px;
        
        .grid-item {
          display: flex;
          align-items: center;
          padding: 12px;
          border-radius: 8px;
          transition: all 0.3s;
          
          &:hover {
            background-color: $bg-light;
          }
          
          .icon-wrapper {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 12px;
            flex-shrink: 0;
            background-color: $bg-light;
            
            i {
              font-size: 24px;
              color: $primary-color;
            }
          }
          
          .info {
            flex: 1;
            
            .label {
              font-size: 12px;
              color: $text-secondary;
              margin-bottom: 4px;
            }
            
            .value {
              font-size: 18px;
              font-weight: bold;
              color: $text-primary;
              
              &.cost {
                &.under-budget {
                  color: #27AE60;
                }
                
                &.over-budget {
                  color: #E53E3E;
                }
              }
            }
          }
        }
      }
    }
  }

  // 日期切换Tab栏
  .day-tabs-wrapper {
    margin-bottom: 20px;
    
    .day-tabs-container {
      display: flex;
      gap: 10px;
      overflow-x: auto;
      padding: 5px;
      scrollbar-width: none;
      
      &::-webkit-scrollbar {
        display: none;
      }
      
      .day-tab {
        padding: 10px 20px;
        border-radius: 8px;
        background-color: $bg-light;
        color: $text-secondary;
        font-size: 14px;
        cursor: pointer;
        transition: all 0.3s;
        white-space: nowrap;
        border: 1px solid transparent;
        user-select: none;
        
        &:hover {
          background-color: lighten($primary-color, 40%);
          color: $primary-color;
          border-color: lighten($primary-color, 30%);
        }
        
        &.active {
          background-color: $primary-color;
          color: $bg-white;
          font-weight: bold;
          box-shadow: $shadow-sm;
        }
      }
    }
  }

  // PDF导出专用容器
  .pdf-export-container {
    // 所有天的内容
    .all-days-content {
      .day-section {
        margin-bottom: 40px;
        
        .day-title {
          margin: 30px 0 20px 0;
          padding-bottom: 10px;
          border-bottom: 2px solid $primary-color;
          
          h3 {
            font-size: 20px;
            font-weight: bold;
            color: $text-primary;
            margin: 0;
          }
        }
      }
    }
  }

  // 行程内容区域
  .itinerary-section {
    .timeline-container {
      .timeline-item {
        display: flex;
        margin-bottom: 24px;
        position: relative;

        .timeline-marker {
          display: flex;
          flex-direction: column;
          align-items: center;
          margin-right: 16px;
          position: relative;

          .marker-dot {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            transition: all 0.3s;
            box-shadow: $shadow-sm;
            
            i {
              font-size: 18px;
              color: $bg-white;
            }
            
            &.attraction {
              background-color: $attraction-color;
              
              &:hover {
                transform: scale(1.1);
                box-shadow: 0 4px 12px rgba($attraction-color, 0.3);
              }
            }
            
            &.food {
              background-color: $food-color;
              
              &:hover {
                transform: scale(1.1);
                box-shadow: 0 4px 12px rgba($food-color, 0.3);
              }
            }
            
            &.hotel {
              background-color: $hotel-color;
              
              &:hover {
                transform: scale(1.1);
                box-shadow: 0 4px 12px rgba($hotel-color, 0.3);
              }
            }
            
            &.route {
              background-color: $route-color;
              
              &:hover {
                transform: scale(1.1);
                box-shadow: 0 4px 12px rgba($route-color, 0.3);
              }
            }
          }

          .marker-line {
            width: 2px;
            flex: 1;
            background: linear-gradient(to bottom, $border-light, transparent);
            margin-top: 8px;
            min-height: 40px;
          }
        }

        .timeline-content {
          flex: 1;
          min-width: 0;

          .content-card {
            border: 1px solid $border-color;
            border-radius: 8px;
            padding: 0;
            background-color: $bg-white;
            box-shadow: $shadow-sm;
            transition: all 0.3s;
            overflow: hidden;
            
            &:hover {
              box-shadow: $shadow-md;
              transform: translateY(-2px);
            }
            
            .card-category-tag {
              display: flex;
              align-items: center;
              gap: 6px;
              padding: 8px 16px;
              font-size: 12px;
              font-weight: bold;
              color: $bg-white;
              
              i {
                font-size: 14px;
              }
              
              &.attraction {
                background-color: $attraction-color;
              }
              
              &.food {
                background-color: $food-color;
              }
              
              &.hotel {
                background-color: $hotel-color;
              }
              
              &.route {
                background-color: $route-color;
              }
            }
            
            .card-main {
              padding: 16px;
              
              .card-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 12px;
                
                .card-title {
                  font-size: 16px;
                  font-weight: bold;
                  color: $text-primary;
                  margin: 0;
                  flex: 1;
                }
                
                .card-actions {
                  display: flex;
                  align-items: center;
                  gap: 8px;
                  
                  .action-icon {
                    cursor: pointer;
                    padding: 6px;
                    border-radius: 50%;
                    transition: all 0.3s;
                    
                    i {
                      font-size: 18px;
                      color: $text-tertiary;
                    }
                    
                    &:hover {
                      background-color: lighten($primary-color, 40%);
                      
                      i {
                        color: $primary-color;
                      }
                    }
                  }
                  
                  .el-button {
                    background-color: $primary-color;
                    border-color: $primary-color;
                    border-radius: 8px;
                    
                    &:hover {
                      background-color: $primary-hover;
                      border-color: $primary-hover;
                    }
                  }
                }
              }

              .card-body {
                .info-tags {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 10px;
                  margin-bottom: 12px;
                  
                  .info-tag {
                    display: flex;
                    align-items: center;
                    gap: 6px;
                    padding: 6px 12px;
                    background-color: $bg-light;
                    border-radius: 6px;
                    font-size: 13px;
                    color: $text-secondary;
                    transition: all 0.3s;
                    
                    i {
                      font-size: 14px;
                      color: $text-tertiary;
                    }
                    
                    &:hover {
                      background-color: lighten($primary-color, 45%);
                      color: $primary-color;
                      
                      i {
                        color: $primary-color;
                      }
                    }
                    
                    &.price {
                      color: #f56c6c;
                      font-weight: bold;
                      
                      i {
                        color: #f56c6c;
                      }
                      
                      .free-badge {
                        color: $attraction-color;
                        background-color: lighten($attraction-color, 40%);
                        padding: 2px 8px;
                        border-radius: 4px;
                        font-size: 12px;
                      }
                    }
                  }
                }

                .description {
                  margin: 12px 0;
                  color: $text-secondary;
                  line-height: 1.8;
                  font-size: 14px;
                  transition: all 0.3s;
                  
                  &.collapsed {
                    display: -webkit-box;
                    -webkit-line-clamp: 3;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                  }
                  
                  &.empty-text {
                    color: $text-placeholder;
                    font-style: italic;
                  }
                }
                
                .expand-btn {
                  display: inline-flex;
                  align-items: center;
                  gap: 4px;
                  color: $primary-color;
                  font-size: 13px;
                  cursor: pointer;
                  margin-top: 8px;
                  transition: all 0.3s;
                  user-select: none;
                  
                  &:hover {
                    opacity: 0.8;
                  }
                  
                  i {
                    font-size: 12px;
                  }
                }

                .images {
                  margin-top: 12px;
                  
                  .carousel-image {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    border-radius: 6px;
                  }
                }

                .route-path {
                  display: flex;
                  align-items: center;
                  margin-bottom: 16px;
                  font-size: 15px;
                  padding: 12px;
                  background-color: lighten($primary-color, 45%);
                  border-radius: 8px;
                  border-left: 3px solid $primary-color;
                  
                  .from, .to {
                    font-weight: bold;
                    color: $text-primary;
                  }
                  
                  i {
                    margin: 0 12px;
                    color: $primary-color;
                    font-size: 18px;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

// 错误状态
.error-state {
  text-align: center;
  padding: 80px 20px;
  background: $bg-white;
  border-radius: 8px;
  box-shadow: $shadow-sm;
  
  .error-icon {
    font-size: 64px;
    color: #f56c6c;
    margin-bottom: 20px;
  }
  
  .error-text {
    font-size: 16px;
    color: $text-secondary;
    margin-bottom: 24px;
  }
  
  .el-button {
    background-color: $primary-color;
    border-color: $primary-color;
    border-radius: 8px;
    
    &:hover {
      background-color: $primary-hover;
      border-color: $primary-hover;
    }
  }
}

// 移动端适配
@media (max-width: 768px) {
  .route-detail-container {
    padding: 10px;
    
    .page-header {
      .page-title {
        font-size: 18px;
      }
    }
    
    .overview-card {
      .card-content {
        padding: 16px;
        
        .overview-grid {
          grid-template-columns: repeat(2, 1fr);
          gap: 12px;
          
          .grid-item {
            padding: 10px;
            
            .icon-wrapper {
              width: 40px;
              height: 40px;
              margin-right: 8px;
              
              i {
                font-size: 20px;
              }
            }
            
            .info {
              .label {
                font-size: 11px;
              }
              
              .value {
                font-size: 16px;
              }
            }
          }
        }
      }
    }
    
    .sticky-tabs-wrapper {
      .day-tabs-container {
        .day-tab {
          padding: 8px 16px;
          font-size: 13px;
        }
      }
    }
    
    .itinerary-section {
      .timeline-container {
        .timeline-item {
          margin-bottom: 16px;
          
          .timeline-marker {
            margin-right: 12px;
            
            .marker-dot {
              width: 36px;
              height: 36px;
              
              i {
                font-size: 16px;
              }
            }
          }
          
          .timeline-content {
            .content-card {
              .card-main {
                padding: 12px;
                
                .card-header {
                  .card-title {
                    font-size: 15px;
                  }
                  
                  .card-actions {
                    .action-icon {
                      padding: 4px;
                      
                      i {
                        font-size: 16px;
                      }
                    }
                    
                    .el-button {
                      padding: 8px 12px;
                      font-size: 13px;
                    }
                  }
                }
                
                .card-body {
                  .info-tags {
                    gap: 8px;
                    
                    .info-tag {
                      padding: 5px 10px;
                      font-size: 12px;
                      
                      i {
                        font-size: 13px;
                      }
                    }
                  }
                  
                  .description {
                    font-size: 13px;
                    line-height: 1.6;
                  }
                  
                  .route-path {
                    font-size: 14px;
                    padding: 10px;
                    
                    i {
                      margin: 0 8px;
                      font-size: 16px;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .route-detail-container {
    .overview-card {
      .card-content {
        .overview-grid {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>