<template>
  <div class="route-generate-container">
    <div class="page-header">
      <h2>智能行程规划</h2>
      <p>根据您的偏好和预算，为您定制专属旅行路线</p>
    </div>

    <!-- 模块A：行程基础信息（必填项） -->
    <el-card class="form-card base-info-card">
      <div slot="header" class="card-header">
        <i class="el-icon-document"></i>
        <span class="card-title">行程基础信息</span>
        <span class="required-hint">*为必填项</span>
      </div>
      <el-form :model="form" :rules="rules" ref="routeForm" label-width="120px">
        <el-form-item label="行程名称">
          <el-input 
            v-model="form.recommendationName" 
            placeholder="如：贵阳三日游（选填）"
            maxlength="50"
            show-word-limit>
          </el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="旅行天数" prop="days">
              <el-input-number 
                v-model="form.days" 
                :min="1" 
                :max="7" 
                placeholder="1-7天"
                controls-position="right"
                style="width: 100%">
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总预算(元)" prop="budget">
              <el-input-number 
                v-model="form.budget" 
                :min="0" 
                :precision="2"
                placeholder="请输入预算"
                controls-position="right"
                style="width: 100%">
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="起点地址" prop="address">
              <el-input 
                v-model="form.address" 
                placeholder="如：贵阳市观山湖区">
                <el-button 
                  slot="append" 
                  icon="el-icon-location" 
                  @click="getCurrentLocation"
                  :loading="locating">
                  定位
                </el-button>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 模块B：个性化偏好设置（选填项） -->
    <el-card class="form-card preference-card">
      <div slot="header" class="card-header">
        <i class="el-icon-setting"></i>
        <span class="card-title">个性化偏好设置</span>
        <span class="optional-hint">选填项</span>
      </div>
      <el-form label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="交通偏好">
              <el-select v-model="form.transportPreference" placeholder="请选择交通方式" style="width: 100%">
                <el-option label="🚗 自驾" value="drive"></el-option>
                <el-option label="🚌 公交" value="bus"></el-option>
                <el-option label="🚕 打车" value="taxi"></el-option>
                <el-option label="🚶 步行" value="walk"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="景点偏好">
              <div class="preference-tags">
                <el-tag
                  v-for="item in attractionTypes"
                  :key="item.id"
                  :type="form.preferenceTypes.includes(item.id.toString()) ? '' : 'info'"
                  :effect="form.preferenceTypes.includes(item.id.toString()) ? 'dark' : 'plain'"
                  @click="togglePreference(item.id.toString())"
                  class="preference-tag">
                  {{ item.typeName }}
                </el-tag>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 操作按钮区 -->
    <div class="action-buttons">
      <el-button 
        type="primary" 
        @click="generateRoute" 
        :loading="generating" 
        :disabled="!canGenerateToday()"
        size="large">
        <i class="el-icon-magic-stick"></i> 
        生成路线推荐
        <span v-if="canGenerateToday()" class="remaining-count">
          （今日剩余 {{ getRemainingCount() }} 次）
        </span>
        <span v-else class="exhausted-count">
          （今日已用完）
        </span>
      </el-button>
      <el-button @click="resetForm" size="large">
        <i class="el-icon-refresh"></i> 重置
      </el-button>
      <el-button @click="$router.push({name: 'myRoutes'})" size="large">
        <i class="el-icon-time"></i> 查看历史记录
      </el-button>
    </div>

    <!-- 路线结果展示 -->
    <div v-if="routeResult" class="result-section">
      <el-card class="result-card">
        <div slot="header" class="clearfix">
          <span>路线推荐结果</span>
          <el-button 
            style="float: right; padding: 3px 0" 
            type="text" 
            @click="saveRoute"
            :loading="saving"
            :disabled="saving || saved">
            <span v-if="saved">✓ 已保存</span>
            <span v-else>{{ saving ? '保存中...' : '保存路线' }}</span>
          </el-button>
        </div>
        
        <div class="summary-info">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="info-item">
                <div class="label">旅行天数</div>
                <div class="value">{{ routeResult.summary.days }}天</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-item">
                <div class="label">预估费用</div>
                <div 
                  class="value" 
                  :class="{ 'over-budget': routeResult.summary.estimatedCost > form.budget }">
                  ¥{{ routeResult.summary.estimatedCost }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-item">
                <div class="label">景点数量</div>
                <div class="value">{{ routeResult.summary.totalAttractions }}个</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-item">
                <div class="label">酒店数量</div>
                <div class="value">{{ routeResult.summary.totalHotels }}家</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="itinerary-section">
          <el-tabs v-model="activeDay">
            <el-tab-pane 
              v-for="day in routeResult.itinerary" 
              :key="day.dayNum" 
              :label="`第${day.dayNum}天`" 
              :name="day.dayNum.toString()">
              
              <div class="day-content">
                <div class="date-info">日期：{{ day.date }}</div>
                
                <div v-for="(item, index) in day.items" :key="index" class="timeline-item">
                  <!-- 景点 -->
                  <div v-if="item.type === 'attraction'" class="attraction-card">
                    <div class="card-header">
                      <div class="header-left">
                        <i class="el-icon-location-outline"></i>
                        <span class="title attraction-title" @click="goToAttractionDetail(item.id)">{{ item.attractionName }}</span>
                        <span class="duration">游玩时长：{{ item.playDuration }}分钟</span>
                      </div>
                    </div>
                    <div class="card-body">
                      <div class="description" :class="{ 'collapsed': !item.showFullDesc }" @click="toggleDescription(item)">
                        {{ item.description }}
                        <span v-if="item.description && item.description.length > 50" class="expand-hint">
                          {{ item.showFullDesc ? '收起' : '展开' }}
                        </span>
                      </div>
                      <div class="price">
                        门票：
                        <span v-if="item.ticketPrice == 0" class="free-tag">免费</span>
                        <span v-else class="paid-tag">¥{{ item.ticketPrice }}</span>
                      </div>
                      <div v-if="item.images" class="images">
                        <el-carousel :interval="4000" type="card" height="150px" indicator-position="none">
                          <el-carousel-item v-for="(img, imgIndex) in getImages(item.images)" :key="imgIndex">
                            <img :src="img" class="carousel-image" />
                          </el-carousel-item>
                        </el-carousel>
                      </div>
                    </div>
                  </div>

                  <!-- 小吃 -->
                  <div v-else-if="item.type === 'food'" class="food-card">
                    <div class="card-header">
                      <i class="el-icon-food"></i>
                      <span class="title">{{ item.foodName }}</span>
                      <span class="shop">{{ item.shopName }}</span>
                    </div>
                    <div class="card-body">
                      <div class="price">价格：¥{{ item.foodPrice }}</div>
                    </div>
                  </div>

                  <!-- 酒店 -->
                  <div v-else-if="item.type === 'hotel'" class="hotel-card">
                    <div class="card-header">
                      <i class="el-icon-office-building"></i>
                      <span class="title">{{ item.hotelName }}</span>
                      <span class="room">{{ item.roomName }}</span>
                    </div>
                    <div class="card-body">
                      <div class="price">房价：¥{{ item.roomPrice }}/晚</div>
                    </div>
                  </div>

                  <!-- 路线 -->
                  <div v-else-if="item.type === 'route'" class="route-card">
                    <div class="route-info">
                      <div class="route-left">
                        <i class="el-icon-direction"></i>
                        <span class="route-label">前往</span>
                        <span class="route-destination">{{ item.toLocation }}</span>
                      </div>
                      <div class="route-right">
                        <span class="transport">{{ getTransportText(item.transportType) }}</span>
                        <span class="distance">{{ item.distance }}km</span>
                        <span class="duration">{{ formatDuration(item.duration) }}</span>
                        <span class="cost">¥{{ item.cost }}</span>
                        <el-button 
                          type="text" 
                          size="mini" 
                          class="nav-btn"
                          @click="openNavigation(item)">
                          <i class="el-icon-location"></i> 查看导航
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import config from '@/config/config'

export default {
  name: 'RouteGenerate',
  data() {
    return {
      form: {
        recommendationName: '',
        days: 3,
        budget: 3000,
        longitude: '',
        latitude: '',
        address: '',
        preferenceTypes: [],
        transportPreference: 'drive'
      },
      rules: {
        days: [
          { required: true, message: '请输入旅行天数', trigger: 'blur' },
          { type: 'number', min: 1, max: 15, message: '天数范围为1-15天', trigger: 'blur' }
        ],
        budget: [
          { required: true, message: '请输入预算', trigger: 'blur' }
        ]
      },
      generating: false,
      saving: false,  // 保存状态
      saved: false,   // 是否已保存
      locating: false,
      routeResult: null,
      activeDay: '1',
      attractionTypes: [],
      defaultAddress: '贵阳市观山湖区', // 默认起点
      dailyGenerateCount: 0,  // 今日生成次数
      maxDailyGenerates: 5    // 每日最大生成次数
    }
  },
  mounted() {
    this.loadAttractionTypes()
    this.initDailyCount()  // 初始化每日计数
    
    // 等待用户信息加载完成后再恢复临时路线
    this.$nextTick(() => {
      setTimeout(() => {
        this.restoreTempRoute()
      }, 100)
    })
  },
  methods: {
    // 切换景点偏好标签
    togglePreference(id) {
      const index = this.form.preferenceTypes.indexOf(id)
      if (index > -1) {
        this.form.preferenceTypes.splice(index, 1)
      } else {
        this.form.preferenceTypes.push(id)
      }
    },

    // 加载景点类型
    async loadAttractionTypes() {
      try {
        const res = await request({
          url: config.backHost + '/attractionType/list',
          method: 'POST'
        })
        if (res.code === 200) {
          this.attractionTypes = res.data || []
        }
      } catch (error) {
        console.error('加载景点类型失败:', error)
      }
    },

    // 获取当前位置
    getCurrentLocation() {
      if (!navigator.geolocation) {
        this.$message.error('您的浏览器不支持地理定位功能，请手动输入地址')
        return
      }

      this.$message.info('正在获取您的位置，请允许浏览器定位请求...')
      this.locating = true
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          try {
            const { longitude, latitude } = position.coords
            
            // 保存经纬度到表单数据
            this.form.longitude = longitude.toFixed(6)
            this.form.latitude = latitude.toFixed(6)
            
            // 先尝试调用后端逆地址解析接口
            try {
              const res = await request({
                url: config.backHost + '/api/common/reverseGeoCode',
                method: 'POST',
                data: { longitude, latitude }
              })
              
              if (res.code === 200 && res.data && res.data.address) {
                this.form.address = res.data.address
                this.$message.success('获取位置成功')
                this.locating = false
                return
              }
            } catch (geoError) {
              console.warn('逆地址解析接口调用失败，将直接使用坐标:', geoError)
            }
            
            // 如果逆地址解析失败，提示用户输入地址
            this.$message.warning(`已获取坐标 (${longitude.toFixed(4)}, ${latitude.toFixed(4)})，但无法解析为地址。请在输入框中手动输入地址，如：贵阳市观山湖区`)
            this.form.address = `贵阳市 (${longitude.toFixed(4)}, ${latitude.toFixed(4)})`
          } catch (error) {
            console.error('获取位置失败:', error)
            this.$message.error('获取位置失败，请手动输入地址')
          } finally {
            this.locating = false
          }
        },
        (error) => {
          this.locating = false
          let errorMsg = '获取位置失败'
          switch(error.code) {
            case error.PERMISSION_DENIED:
              errorMsg = '您拒绝了位置请求。请点击“使用默认起点”按钮，或直接输入地址'
              break
            case error.POSITION_UNAVAILABLE:
              errorMsg = '位置信息不可用，请手动输入地址'
              break
            case error.TIMEOUT:
              errorMsg = '获取位置超时，请手动输入地址'
              break
            default:
              errorMsg = '获取位置失败，请手动输入地址或使用默认起点'
          }
          this.$message.error(errorMsg)
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        }
      )
    },

    // 使用默认起点
    useDefaultAddress() {
      this.form.address = this.defaultAddress
      this.$message.success(`已使用默认起点：${this.defaultAddress}`)
    },

    // 生成路线推荐
    async generateRoute() {
      // 检查每日生成次数限制
      if (!this.canGenerateToday()) {
        this.$message.warning(`今日生成次数已用完（${this.maxDailyGenerates}次/天），请明天再试`)
        return
      }
      
      this.$refs.routeForm.validate(async (valid) => {
        if (valid) {
          this.generating = true
          this.saved = false  // 生成新路线时，重置已保存状态
          try {
            // 构建请求参数
            const params = {
              days: this.form.days,
              budget: this.form.budget,
              transportPreference: this.form.transportPreference
            }

            // 添加位置信息
            // 如果用户没有输入地址，使用默认起点
            const address = this.form.address || this.defaultAddress
            params.address = address
            
            // 如果存在经纬度，则传递给后端
            if (this.form.longitude && this.form.latitude) {
              params.longitude = parseFloat(this.form.longitude)
              params.latitude = parseFloat(this.form.latitude)
            }

            // 添加行程名称（如果用户填写了）
            if (this.form.recommendationName && this.form.recommendationName.trim()) {
              params.recommendationName = this.form.recommendationName.trim()
            }

            // 添加偏好类型
            if (this.form.preferenceTypes.length > 0) {
              params.preferenceTypes = this.form.preferenceTypes
            }

            const res = await request({
              url: config.backHost + '/api/routeRecommendation/generate',
              method: 'POST',
              data: params
            })
            
            if (res.code === 200 && res.data) {
              // 生成成功，增加计数
              this.incrementDailyCount()
              
              // 处理日期：根据 dayNum 计算正确的日期
              if (res.data.itinerary && Array.isArray(res.data.itinerary)) {
                const startDate = new Date()
                res.data.itinerary.forEach(day => {
                  // 根据 dayNum 计算日期（dayNum 从 1 开始）
                  const currentDate = new Date(startDate)
                  currentDate.setDate(currentDate.getDate() + (day.dayNum - 1))
                  // 格式化为 YYYY-MM-DD
                  const year = currentDate.getFullYear()
                  const month = String(currentDate.getMonth() + 1).padStart(2, '0')
                  const dayOfMonth = String(currentDate.getDate()).padStart(2, '0')
                  day.date = `${year}-${month}-${dayOfMonth}`
                })
              }
              
              this.routeResult = res.data
              this.activeDay = '1'
              this.$message.success('路线推荐生成成功！')
              
              // 临时保存到 sessionStorage（防止切换页面丢失）
              this.saveTempRoute()
              
              // 自动滚动到结果区
              this.$nextTick(() => {
                const resultSection = document.querySelector('.result-section')
                if (resultSection) {
                  resultSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
                }
              })
            } else {
              this.$message.error(res.msg || '生成失败')
            }
          } catch (error) {
            console.error('生成路线推荐失败:', error)
            this.$message.error('生成路线推荐失败，请稍后重试')
          } finally {
            this.generating = false
          }
        }
      })
    },

    // 初始化每日计数 - 从后端获取真实数据
    async initDailyCount() {
      try {
        // 调用后端接口获取今日已使用次数
        const res = await request({
          url: config.backHost + '/api/routeRecommendation/getDailyCount',
          method: 'POST'
        })
        
        if (res.code === 200 && res.data !== undefined) {
          // 使用后端返回的真实计数
          this.dailyGenerateCount = res.data
          // 同步更新 localStorage（作为缓存）
          this.saveDailyCount()
        } else {
          // 如果接口失败，降级使用 localStorage
          this.initFromLocalStorage()
        }
      } catch (error) {
        // 降级方案：使用 localStorage
        this.initFromLocalStorage()
      }
    },

    // 从 localStorage 初始化（降级方案）
    initFromLocalStorage() {
      const today = new Date().toDateString()
      const stored = localStorage.getItem('routeGenerateCount')
          
      if (stored) {
        const { date, count } = JSON.parse(stored)
        if (date === today) {
          this.dailyGenerateCount = count
        } else {
          this.dailyGenerateCount = 0
          this.saveDailyCount()
        }
      } else {
        this.dailyGenerateCount = 0
      }
    },

    // 保存每日计数到 localStorage
    saveDailyCount() {
      const today = new Date().toDateString()
      const data = {
        date: today,
        count: this.dailyGenerateCount
      }
      localStorage.setItem('routeGenerateCount', JSON.stringify(data))
    },

    // 检查今天是否可以生成
    canGenerateToday() {
      return this.dailyGenerateCount < this.maxDailyGenerates
    },

    // 增加每日计数
    incrementDailyCount() {
      this.dailyGenerateCount++
      this.saveDailyCount()
    },

    // 获取剩余次数
    getRemainingCount() {
      return this.maxDailyGenerates - this.dailyGenerateCount
    },

    // 临时保存路线到 sessionStorage（与用户关联）
    saveTempRoute() {
      try {
        const userInfo = this.$store.getters.getUser || {}
        const userId = userInfo.userId || 'anonymous'
        
        const tempData = {
          routeResult: this.routeResult,
          form: {
            recommendationName: this.form.recommendationName,
            days: this.form.days,
            budget: this.form.budget,
            address: this.form.address,
            longitude: this.form.longitude,
            latitude: this.form.latitude,
            preferenceTypes: this.form.preferenceTypes,
            transportPreference: this.form.transportPreference
          },
          userId: userId,  // 记录用户 ID
          timestamp: Date.now()
        }
        // 使用 userId 作为 key 的一部分，实现用户隔离
        sessionStorage.setItem(`tempRouteGenerate_${userId}`, JSON.stringify(tempData))
      } catch (error) {
        console.error('临时保存路线失败:', error)
      }
    },

    // 从 sessionStorage 恢复临时保存的路线（与用户关联）
    restoreTempRoute() {
      try {
        const userInfo = this.$store.getters.getUser || {}
        const userId = userInfo.userId || 'anonymous'
        
        // 使用当前用户的 key 读取
        const stored = sessionStorage.getItem(`tempRouteGenerate_${userId}`)
        if (stored) {
          const tempData = JSON.parse(stored)
          
          // 检查是否是当前用户的数据
          if (tempData.userId !== userId) {
            // 不是当前用户的数据，清除
            this.clearTempRoute()
            return
          }
          
          // 检查是否是今天的数据（最多保留24小时）
          const now = Date.now()
          const oneDay = 24 * 60 * 60 * 1000
          if (now - tempData.timestamp < oneDay) {
            // 恢复路线数据
            this.routeResult = tempData.routeResult
            this.activeDay = '1'
            
            // 恢复表单数据
            if (tempData.form) {
              this.form.recommendationName = tempData.form.recommendationName || ''
              this.form.days = tempData.form.days || 3
              this.form.budget = tempData.form.budget || 3000
              this.form.address = tempData.form.address || ''
              this.form.longitude = tempData.form.longitude || ''
              this.form.latitude = tempData.form.latitude || ''
              this.form.preferenceTypes = tempData.form.preferenceTypes || []
              this.form.transportPreference = tempData.form.transportPreference || 'drive'
            }
            
            console.log('已恢复临时保存的路线')
          } else {
            // 超过24小时，清除旧数据
            this.clearTempRoute()
          }
        }
      } catch (error) {
        console.error('恢复临时路线失败:', error)
        this.clearTempRoute()
      }
    },

    // 清除临时保存的路线（与用户关联）
    clearTempRoute() {
      try {
        const userInfo = this.$store.getters.getUser || {}
        const userId = userInfo.userId || 'anonymous'
        sessionStorage.removeItem(`tempRouteGenerate_${userId}`)
      } catch (error) {
        console.error('清除临时路线失败:', error)
      }
    },

    // 重置表单
    resetForm() {
      this.$confirm('确定要重置所有表单内容吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$refs.routeForm.resetFields()
        this.routeResult = null
        this.saved = false  // 重置时清除已保存状态
        this.$message.success('重置成功')
      }).catch(() => {
        // 用户取消
      })
    },

    // 保存路线
    async saveRoute() {
      // 如果已保存，不允许重复保存
      if (this.saved) {
        this.$message.warning('该路线已保存，请勿重复保存')
        return
      }
      
      // 防重复提交
      if (this.saving) {
        this.$message.warning('正在保存中，请勿重复点击')
        return
      }
      
      if (!this.routeResult) {
        this.$message.warning('请先生成路线推荐')
        return
      }
      
      // 验证地址信息
      if (!this.form.address) {
        this.$message.warning('请提供起点地址')
        return
      }
      
      // 调用后端保存接口
      this.saving = true
      try {
        const res = await request({
          url: config.backHost + '/api/routeRecommendation/save',
          method: 'POST',
          data: {
            days: this.form.days,
            budget: this.form.budget,
            address: this.form.address,  // 必须传递地址
            recommendationName: this.form.recommendationName || null,
            longitude: this.form.longitude || null,
            latitude: this.form.latitude || null,
            preferenceTypes: this.form.preferenceTypes,
            transportPreference: this.form.transportPreference
          }
        })
        
        if (res.code === 200) {
          this.$message.success('路线保存成功！')
          // 更新路线 ID
          if (res.data) {
            this.$set(this.routeResult, 'recommendationId', res.data)
          }
          // 标记为已保存
          this.saved = true
          
          // 保存成功后，清除临时保存
          this.clearTempRoute()
        } else {
          this.$message.error(res.msg || '保存失败')
        }
      } catch (error) {
        console.error('保存路线失败:', error)
        this.$message.error('保存路线失败，请稍后重试')
      } finally {
        this.saving = false
      }
    },

    // 获取图片数组
    getImages(imagesStr) {
      if (!imagesStr) return []
      try {
        const parsed = JSON.parse(imagesStr)
        // 如果解析后是对象数组，使用 id 字段拼接完整 URL
        if (Array.isArray(parsed)) {
          return parsed.map(img => {
            if (typeof img === 'string') return img
            if (typeof img === 'object' && img !== null) {
              // 后端返回的是对象，使用 id 字段拼接下载 URL（项目统一规范）
              if (img.id) {
                return config.downloadUrl + img.id
              }
              return ''
            }
            return String(img || '')
          }).filter(img => img)
        }
        return [String(parsed)]
      } catch (e) {
        // 如果是逗号分隔的字符串
        if (typeof imagesStr === 'string') {
          return imagesStr.split(',').map(img => img.trim()).filter(img => img)
        }
        return []
      }
    },

    // 获取交通方式文本
    getTransportText(type) {
      const map = {
        'drive': '自驾',
        'bus': '公交',
        'taxi': '打车',
        'walking': '步行',  // 高德 API 返回 walking
        'walk': '步行'      // 兼容旧数据
      }
      return map[type] || type
    },

    // 跳转到景点详情
    goToAttractionDetail(attractionId) {
      if (attractionId) {
        this.$router.push({
          name: 'attractionDetail',
          query: { id: attractionId }
        })
      }
    },

    // 展开/收起景点描述
    toggleDescription(item) {
      if (item.description && item.description.length > 50) {
        this.$set(item, 'showFullDesc', !item.showFullDesc)
      }
    },

    // 打开地图导航
    openNavigation(item) {
      // 优先使用经纬度坐标，如果有的话
      if (item.fromLongitude && item.fromLatitude && item.toLongitude && item.toLatitude) {
        // 使用经纬度坐标导航（更精确）
        const fromCoord = `${item.fromLongitude},${item.fromLatitude}`
        const toCoord = `${item.toLongitude},${item.toLatitude}`
        
        // 根据交通方式确定导航模式
        let mode = 'drive' // 默认驾车
        if (item.transportType === 'bus') {
          mode = 'bus' // 公交
        } else if (item.transportType === 'walk' || item.transportType === 'walking') {
          mode = 'walk' // 步行
        }
        
        const amapUrl = `https://uri.amap.com/navigation?from=${fromCoord}&to=${toCoord}&mode=${mode}&src=贵阳旅游攻略系统`
        window.open(amapUrl, '_blank')
      } else if (item.fromLocation && item.toLocation) {
        // 如果没有坐标，使用地址名称（精度较低）
        const amapUrl = `https://uri.amap.com/navigation?from=${item.fromLocation}&to=${item.toLocation}`
        window.open(amapUrl, '_blank')
      } else {
        this.$message.warning('路线信息不完整，无法导航')
      }
    },

    // 格式化时长显示
    formatDuration(minutes) {
      if (!minutes && minutes !== 0) return '-'
      
      if (minutes < 60) {
        // 少于 1 小时，直接显示分钟
        return `${minutes}分钟`
      } else {
        // 超过 1 小时，显示小时和分钟
        const hours = Math.floor(minutes / 60)
        const mins = minutes % 60
        if (mins === 0) {
          return `约${hours}小时`
        } else {
          return `约${hours}小时${mins}分钟`
        }
      }
    }
  }
}
</script>

<style scoped lang="scss">
.route-generate-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .page-header {
    text-align: center;
    margin-bottom: 30px;
    
    h2 {
      color: #333;
      margin-bottom: 10px;
    }
    
    p {
      color: #666;
      font-size: 14px;
    }
  }

  .form-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      align-items: center;
      
      i {
        font-size: 18px;
        color: #FF7A2F;
        margin-right: 8px;
      }
      
      .card-title {
        font-size: 16px;
        font-weight: bold;
        color: #333;
      }
      
      .required-hint {
        margin-left: auto;
        font-size: 12px;
        color: #999;
      }
      
      .optional-hint {
        margin-left: auto;
        font-size: 12px;
        color: #67c23a;
        background: #f0f9eb;
        padding: 2px 8px;
        border-radius: 4px;
      }
    }
    
    .preference-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      .preference-tag {
        cursor: pointer;
        transition: all 0.3s;
        
        &:hover {
          transform: scale(1.05);
        }
      }
    }
  }

  .action-buttons {
    text-align: center;
    margin-bottom: 30px;
    
    .el-button {
      margin: 0 10px;
      
      i {
        margin-right: 5px;
      }
      
      .remaining-count {
        font-size: 12px;
        color: #67c23a;
        margin-left: 5px;
      }
      
      .exhausted-count {
        font-size: 12px;
        color: #f56c6c;
        margin-left: 5px;
      }
    }
  }

  .result-section {
    .result-card {
      .summary-info {
        margin-bottom: 20px;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 8px;

        .info-item {
          text-align: center;
          
          .label {
            font-size: 12px;
            color: #666;
            margin-bottom: 5px;
          }
          
          .value {
            font-size: 18px;
            font-weight: bold;
            color: #409eff;
          }
        }
      }

      .itinerary-section {
        .day-content {
          .date-info {
            margin-bottom: 15px;
            font-weight: bold;
            color: #333;
          }

          .timeline-item {
            margin-bottom: 15px;

            .attraction-card,
            .food-card,
            .hotel-card,
            .route-card {
              border: 1px solid #ebeef5;
              border-radius: 8px;
              padding: 15px;
              background-color: #fff;
              box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

              .card-header {
                display: flex;
                align-items: center;
                margin-bottom: 10px;
                
                .header-left {
                  display: flex;
                  align-items: center;
                  flex: 1;
                }
                
                i {
                  margin-right: 8px;
                  color: #409eff;
                }
                
                .title {
                  font-weight: bold;
                  color: #333;
                  margin-right: 10px;
                  
                  &.attraction-title {
                    cursor: pointer;
                    
                    &:hover {
                      color: #409eff;
                      text-decoration: underline;
                    }
                  }
                }
                
                .duration,
                .shop,
                .room {
                  font-size: 12px;
                  color: #666;
                }
              }

              .card-body {
                .description {
                  margin-bottom: 10px;
                  color: #666;
                  line-height: 1.5;
                  cursor: pointer;
                  
                  &.collapsed {
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                  }
                  
                  .expand-hint {
                    color: #409eff;
                    font-weight: normal;
                    margin-left: 5px;
                  }
                }
                
                .price {
                  color: #f56c6c;
                  font-weight: bold;
                  
                  .free-tag {
                    color: #67c23a;
                    background-color: #f0f9eb;
                    padding: 2px 8px;
                    border-radius: 4px;
                    font-size: 14px;
                  }
                  
                  .paid-tag {
                    color: #FF7A2F;
                  }
                }
                
                .images {
                  margin-top: 10px;
                  
                  .carousel-image {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                  }
                }
              }

              &.route-card {
                .route-info {
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
                  flex-wrap: wrap;
                  gap: 10px;
                  
                  .route-left {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    
                    i {
                      color: #67c23a;
                    }
                    
                    .route-label {
                      color: #67c23a;
                      font-weight: bold;
                      font-size: 14px;
                    }
                    
                    .route-destination {
                      color: #333;
                      font-weight: bold;
                      font-size: 14px;
                    }
                  }
                  
                  .route-right {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    
                    .transport {
                      background-color: #ecf5ff;
                      color: #409eff;
                      padding: 2px 8px;
                      border-radius: 4px;
                      font-size: 12px;
                    }
                    
                    .distance,
                    .duration,
                    .cost {
                      font-size: 12px;
                      color: #666;
                    }
                    
                    .cost {
                      color: #f56c6c;
                      font-weight: bold;
                      
                      &.free-cost {
                        color: #67c23a;
                        background-color: #f0f9eb;
                        padding: 2px 6px;
                        border-radius: 4px;
                        font-size: 12px;
                      }
                    }
                    
                    .nav-btn {
                      color: #409eff;
                      padding: 0;
                      
                      &:hover {
                        color: #66b1ff;
                      }
                      
                      i {
                        margin-right: 3px;
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
}

@media (max-width: 768px) {
  .route-generate-container {
    padding: 10px;
    
    .form-card {
      ::v-deep .el-form-item__label {
        width: 80px !important;
      }
      
      ::v-deep .el-form-item__content {
        margin-left: 80px !important;
      }
    }
  }
}
</style>