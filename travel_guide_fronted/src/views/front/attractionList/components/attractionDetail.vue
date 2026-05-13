<template>
  <div class="attraction-detail">
    <div class="container">
      <el-page-header style="padding: 0 12.5px" @back="$router.go(-1)" content="景点详情" class="custom-header"></el-page-header>
      <div class="booking-sidebar shadow-card">
        <div class="sticky-box">
          <div class="top-action-bar">
                <span @click="handleCollect">
                  <span v-if="detail.isCollect == 1" style="margin-right: 4px;color: red">❤</span>
                  <span v-if="detail.isCollect != 1" style="margin-right: 4px">♡</span>
                  <span v-if="detail.isCollect == 1">已收藏</span>
                  <span v-if="detail.isCollect != 1">收藏</span>
                </span>
          </div>

          <div class="price-header-v2">
            <span class="price-label">单价</span>
            <div class="price-amount">
              <span class="val">￥{{ currentPrice }}</span>
              <span class="unit">每人</span>
            </div>
          </div>

          <div class="booking-form-v2">
            <el-popover
                placement="bottom"
                width="320"
                trigger="click"
                popper-class="visitor-picker-popover"
            >
              <div class="visitor-picker-panel">
                <div class="visitor-row" v-for="(type, index) in visitorTypes" :key="index">
                  <div class="v-info">
                    <div class="v-name">{{ type.label }}</div>
                  </div>
                  <div class="v-control">
                    <el-button icon="el-icon-minus" circle size="mini" :disabled="type.count <= type.min" @click="type.count--"></el-button>
                    <span class="v-num">{{ type.count }}</span>
                    <el-button icon="el-icon-plus" circle size="mini" @click="type.count++"></el-button>
                  </div>
                </div>
              </div>

              <div slot="reference" class="custom-select-box">
                <i class="el-icon-user"></i>
                <span class="display-text">{{ visitorSummary }}</span>
                <i class="el-icon-caret-bottom"></i>
              </div>
            </el-popover>
          </div>
        </div>
      </div>
      <el-row :gutter="25" style="margin: 0">
        <el-col :span="17">
          <div class="main-content shadow-card">
            <div v-if="parsedAttractionPics.length > 0" class="card-image" :class="'pic-count-' + parsedAttractionPics.length">
              <el-image v-if="parsedAttractionPics.length == 1" :src="getPicUrlByJson(detail.attractionPic,0)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,0)]"></el-image>
              <template v-else-if="parsedAttractionPics.length == 2">
                <div style="width: 50%;overflow: hidden">
                  <el-image :src="getPicUrlByJson(detail.attractionPic,0)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,0)]"></el-image>
                </div>
                <div style="width: 50%;overflow: hidden">
                  <el-image :src="getPicUrlByJson(detail.attractionPic,1)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,1)]"></el-image>
                </div>
              </template>

              <template v-else-if="parsedAttractionPics.length >= 3">
                <div style="width: 66%;overflow: hidden">
                  <el-image class="pic-main" :src="getPicUrlByJson(detail.attractionPic,0)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,0)]"></el-image>
                </div>
                <div class="side-pics">
                  <div style="width:100%;height: 50%;overflow: hidden">
                    <el-image :src="getPicUrlByJson(detail.attractionPic,1)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,1)]"></el-image>
                  </div>
                  <div style="width: 100%;height: 50%;overflow: hidden">
                    <el-image :src="getPicUrlByJson(detail.attractionPic,2)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,2)]"></el-image>
                  </div>
                </div>
              </template>
            </div>
            <div>
              <el-tabs v-model="activeName" @tab-click="selTab">
                <el-tab-pane label="景点信息" name="first">
                  <div class="article-body">
                    <h1 class="attr-title">{{ detail.attractionName }}</h1>
                    <div class="meta-info">
                      <span><i class="el-icon-time"></i>开放时间：{{ detail.businessHours || '暂无信息' }}</span>
                      <span><i class="el-icon-location-outline"></i> {{ detail.attractionPlace }}</span>
                      <span><i class="el-icon-menu"></i>{{ classify.find(item=>item.id == detail.typeId)?.typeName }}</span>
                    </div>

                    <div class="ticket-section">
                      <div class="section-label">门票选项</div>
                      <div v-if="ticketList.length > 0">
                        <div v-for="ticket in ticketList" :key="ticket.id" class="ticket-card" @click="selTicket(ticket)" :class="{ 'active': selectedTicketId === ticket.id }">
                          <div class="ticket-info">
                            <div class="t-name">{{ ticket.ticketName }}</div>
                            <div class="t-desc">{{ ticket.useScope }}</div>
                          </div>
                          <div class="ticket-price-action">
                            <div class="price-val">
                              <div style="font-size: 24px;font-weight: bold">
                                <span class="unit">￥</span>{{ ticket.ticketPrice * visitorTypes[0].count }}
                              </div>
                              <div style="font-size: 14px;color: #63687a">{{visitorTypes[0].count}}人 x ￥{{ticket.ticketPrice}}</div>
                            </div>
                            <div class="btns">
                              <el-button type="primary" plain style="border-radius: 20px" size="medium" @click="handleOrder(ticket)">立即预订</el-button>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div v-else class="free-ticket-badge">
                        <i class="el-icon-success"></i>
                        <span>免费开放，无需购票</span>
                      </div>
                    </div>

                    <div class="detail-html-content">
                      <div class="section-label">详细介绍</div>
                      <div v-html="detail.attractionDetail"></div>
                    </div>

                    <!-- 顺路行程推荐模块 -->
                    <div class="route-recommend-section">
                      <div class="section-label">顺路行程推荐</div>
                      
                      <!-- 加载状态 -->
                      <div v-if="routeLoading" class="loading-container">
                        <i class="el-icon-loading"></i>
                        <span>加载中...</span>
                      </div>

                      <!-- 无数据提示 -->
                      <el-empty v-else-if="!nextAttractions || nextAttractions.length === 0" description="暂无推荐内容" :image-size="80"></el-empty>

                      <!-- 推荐下一站景点 -->
                      <div v-else class="recommend-attractions">
                        <div 
                          v-for="item in nextAttractions" 
                          :key="item.id" 
                          class="attraction-card"
                          :class="{ 'active': selectedAttractionId === item.id }"
                          @click="selectAttraction(item)"
                        >
                          <div class="card-image">
                            <el-image 
                              v-if="item.attractionPic"
                              :src="getPicUrlByJson(item.attractionPic, 0)" 
                              fit="cover"
                              style="width: 100%; height: 100%;"
                            ></el-image>
                            <div v-else class="image-placeholder">
                              <i class="el-icon-picture-outline"></i>
                              <span>暂无图片</span>
                            </div>
                          </div>
                          <div class="card-info">
                            <div class="attraction-name">{{ item.attractionName }}</div>
                            <div class="attraction-location">
                              <i class="el-icon-location-outline"></i>
                              {{ item.attractionPlace }}
                            </div>
                            <div class="attraction-distance">
                              <span class="distance-label">距离</span>
                              <span class="distance-value">{{ item.distance }}km</span>
                            </div>
                            <div class="attraction-duration">
                              <span class="duration-label">驾车时长</span>
                              <span class="duration-value">{{ item.driveTime }}分钟</span>
                            </div>
                            <div class="attraction-playtime" v-if="item.playHour">
                              <span class="playtime-label">游玩时长</span>
                              <span class="playtime-value">{{ formatPlayTime(item.playHour) }}</span>
                            </div>
                          </div>
                        </div>
                      </div>

                      <!-- 交通方式展示区（选中景点后显示） -->
                      <div v-if="selectedAttractionId && trafficInfo" class="traffic-section">
                        <!-- 三种交通方式横向对比卡片 -->
                        <div class="traffic-compare-cards">
                          <div 
                            v-for="type in trafficTypes" 
                            :key="type.value"
                            class="traffic-card"
                            :class="{ 'active': currentTrafficType === type.value }"
                            @click="switchTrafficType(type.value)"
                          >
                            <div class="card-header">
                              <i :class="getTrafficIcon(type.value)"></i>
                              <span class="type-name">{{ type.label }}</span>
                            </div>
                            <transition name="fade-slide">
                              <div v-if="currentTrafficData && getTrafficTypeData(type.value)" class="card-body">
                                <div class="info-item">
                                  <span class="info-label">里程</span>
                                  <span class="info-value">{{ getTrafficTypeData(type.value).distance }}km</span>
                                </div>
                                <div class="info-item">
                                  <span class="info-label">耗时</span>
                                  <span class="info-value">{{ getTrafficTypeData(type.value).time }}分钟</span>
                                </div>
                                <div class="info-item">
                                  <span class="info-label">费用</span>
                                  <span class="info-value price">￥{{ getTrafficTypeData(type.value).cost }}</span>
                                </div>
                              </div>
                            </transition>
                          </div>
                        </div>

                        <div v-if="trafficLoading" class="loading-container">
                          <i class="el-icon-loading"></i>
                          <span>加载中...</span>
                        </div>

                        <!-- 当前选中交通方式的详细信息 -->
                        <transition v-else-if="currentTrafficData" name="fade-in">
                          <div class="traffic-detail-info">
                            <div class="detail-row">
                              <span class="label"><i class="el-icon-map-location"></i>里程：</span>
                              <span class="value">{{ currentTrafficData.distance }}km</span>
                            </div>
                            <div class="detail-row">
                              <span class="label"><i class="el-icon-time"></i>耗时：</span>
                              <span class="value">{{ currentTrafficData.duration }}</span>
                            </div>
                            <div class="detail-row">
                              <span class="label"><i class="el-icon-wallet"></i>预估费用：</span>
                              <span class="value">￥{{ currentTrafficData.cost }}</span>
                            </div>
                            
                            <!-- 公交路线详情 -->
                            <div v-if="currentTrafficType === 'bus'" class="route-description" v-loading="trafficLoading">
                              <!-- 没有找到公交路线 -->
                              <div v-if="hasNoBusRoute" class="no-route-message">
                                <i class="el-icon-warning-outline"></i>
                                <div class="message-text">抱歉，没有找到对应线路</div>
                              </div>
                              <!-- 有公交路线 -->
                              <template v-else>
                                <div class="route-title">
                                  <i class="el-icon-guide"></i>
                                  公交路线
                                </div>
                                <div class="route-content" :class="{ 'collapsed': !showFullRoute }">
                                  {{ currentTrafficData.routeDescription }}
                                </div>
                                <div class="route-toggle">
                                  <el-button 
                                    type="text" 
                                    size="mini" 
                                    @click="toggleRouteExpand"
                                    class="toggle-btn"
                                  >
                                    {{ showFullRoute ? '收起' : '展开详情' }}
                                    <i :class="showFullRoute ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                                  </el-button>
                                </div>
                              </template>
                            </div>
                            
                            <!-- 导航按钮组 -->
                            <div class="navigation-actions">
                              <el-button 
                                type="primary" 
                                icon="el-icon-location" 
                                @click="openAmapNavigation"
                                class="nav-btn"
                              >
                                打开高德地图导航
                              </el-button>
                              <el-button 
                                icon="el-icon-document-copy" 
                                @click="copyRouteAddress"
                                class="copy-btn"
                              >
                                复制起点/终点
                              </el-button>
                            </div>
                          </div>
                        </transition>

                        <el-empty v-else description="暂无交通信息" :image-size="60"></el-empty>
                      </div>

                      <!-- 高德地图容器 -->
                      <div class="amap-wrapper" ref="amapWrapper" v-show="selectedAttractionId">
                        <div v-if="mapLoading" class="map-loading">
                          <i class="el-icon-loading"></i>
                          <span>地图加载中...</span>
                        </div>
                        <div id="amap-container" ref="amapContainer" class="amap-container"></div>
                      </div>

                      <!-- 沿途特色小吃推荐（选中景点后显示） -->
                      <div v-if="selectedAttractionId && foodShops" class="food-section">
                        <div class="section-subtitle">
                          <i class="el-icon-food"></i>
                          沿途特色小吃
                        </div>
                        
                        <div v-if="foodLoading" class="loading-container">
                          <i class="el-icon-loading"></i>
                          <span>加载中...</span>
                        </div>

                        <div v-else-if="displayedFoodShops.length > 0" class="food-list">
                          <div 
                            v-for="shop in displayedFoodShops" 
                            :key="shop.id" 
                            class="food-card"
                            @click="goToFoodDetail(shop.foodId)"
                          >
                            <div class="food-image">
                              <el-image 
                                v-if="shop.images"
                                :src="getPicUrlByJson(shop.images, 0)" 
                                fit="cover"
                                style="width: 100%; height: 100%;"
                              ></el-image>
                              <div v-else class="image-placeholder-small">
                                <i class="el-icon-food"></i>
                              </div>
                            </div>
                            <div class="food-info">
                              <div class="shop-header">
                                <div class="shop-name">{{ shop.name }}</div>
                                <div v-if="shop.businessStatus" class="business-status" :class="shop.businessStatus === '营业中' ? 'open' : 'closed'">
                                  {{ shop.businessStatus }}
                                </div>
                              </div>
                              <div class="shop-address">
                                <i class="el-icon-location-outline"></i>
                                {{ shop.address }}
                              </div>
                              <div v-if="shop.recommendReason" class="recommend-reason">
                                <i class="el-icon-star-on"></i>
                                {{ shop.recommendReason }}
                              </div>
                              <div class="shop-meta">
                                <span class="meta-item">
                                  <span class="meta-label">人均：</span>
                                  <span class="meta-value">￥{{ shop.avgPrice }}</span>
                                </span>
                                <span class="meta-item">
                                  <span class="meta-label">距路线：</span>
                                  <span class="meta-value">{{ shop.distance }}km</span>
                                </span>
                              </div>
                              <div v-if="shop.convenienceIndex" class="convenience-index">
                                <span class="index-label">顺路指数：</span>
                                <div class="stars">
                                  <i v-for="n in 5" :key="n" class="el-icon-star-on" :class="{ 'active': n <= shop.convenienceIndex }"></i>
                                </div>
                              </div>
                              <div v-if="shop.businessHours" class="business-hours">
                                <i class="el-icon-time"></i>
                                {{ shop.businessHours }}
                              </div>
                            </div>
                          </div>
                        </div>

                        <el-empty v-else description="暂无推荐小吃" :image-size="60"></el-empty>
                        
                        <!-- 查看更多按钮 -->
                        <div v-if="foodShops.length > 4" class="show-more-wrapper">
                          <el-button 
                            type="text" 
                            @click="toggleShowAllFoodShops"
                            class="show-more-btn"
                          >
                            {{ showAllFoodShops ? '收起' : '查看更多沿途小吃' }}
                            <i :class="showAllFoodShops ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                          </el-button>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="景点评价" name="second">
                  <el-row :gutter="25" style="margin: 0">
                    <el-col :span="24">
                      <div class="comment-input-area">
                        <el-input type="textarea" :rows="3" placeholder="觉得景点怎么样？快来评价吧..." v-model="commentForm.commentDetail"></el-input>
                        <div class="comment-action-bar">
                          <upload-image-more v-model="commentForm.pictureUrl" :max="3"/>
                          <el-button type="primary" round @click="submitComment" :loading="submitting">发布评价</el-button>
                        </div>
                      </div>
                    </el-col>
                    <el-col :span="24">
                      <div class="comment-section">
                        <div class="comment-header">
                          <span  class="section-label">景点评论</span>
                        </div>

                        <div class="comment-list" v-if="comments.length > 0">
                          <div v-for="item in comments" :key="item.id" class="comment-item">
                            <div style="display: flex;gap: 20px;width: 100%;">
                              <el-avatar :src="getPicUrlByJson(item.creatorHead, 0)" :size="45"></el-avatar>
                              <div class="c-main">
                                <div class="c-user-info">
                                  <span class="user-name">{{ item.createName }}</span>
                                  <span class="user-time">{{ item.createTime }}</span>
                                </div>
                                <div class="c-content">
                                  <span v-if="item.replayCommentId" class="at-tag">@{{ item.replayName }}</span>
                                  {{ item.commentDetail }}
                                </div>
                                <div style="display: flex;gap: 12px" v-if="item.pictureUrl">
                                  <el-image v-for="(items,indexs) in JSON.parse(item.pictureUrl)" :key="indexs" style="width: 100px;height: 100px" :src="getPicUrlByJson(item.pictureUrl,indexs)" :preview-src-list="[getPicUrlByJson(item.pictureUrl,indexs)]"></el-image>
                                </div>
                                <div class="c-actions">
                                  <span v-if="item.creator === currentUserId" class="act-btn del" @click="handleDelete(item.id)">
                                    <i class="el-icon-delete"></i> 删除
                                  </span>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <el-empty v-else description="暂无评论，快来抢沙发" :image-size="80"></el-empty>
                      </div>
                    </el-col>
                  </el-row>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </el-col>

        <el-col :span="7">
          <!-- 预留右侧边栏空间 -->
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
/* global AMap */
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";
import uploadImageMore from "@/components/UploadImageMore.vue";

export default {
  components: {uploadImageMore},
  mixins: [common],
  data() {
    return {
      detail: {},
      ticketList: [],
      selectedTicketId: null,
      currentPrice: 0,
      visitorTypes: [
        { label: '人数', count: 1, min: 1 },
      ],
      activeName:'first',
      classify:[],
      comments:[],
      commentForm: {
        attractionId: this.$route.query.id,
        commentDetail: "",
        pictureUrl: "[]"
      },
      submitting: false,
      // 路线推荐相关数据
      nextAttractions: [],
      selectedAttractionId: null,
      trafficInfo: null,
      foodShops: [],
      routeLoading: false,
      trafficLoading: false,
      foodLoading: false,
      currentTrafficType: 'drive',
      trafficTypes: [
        { label: '自驾', value: 'drive' },
        { label: '公交', value: 'bus' },
        { label: '打车', value: 'taxi' }
      ],
      mapInstance: null, // 高德地图实例
      polylineInstance: null, // 路线覆盖物实例（旧版兼容）
      polylineInstances: [], // 多段路线覆盖物实例（公交分段绘制）
      startMarker: null, // 起点标记
      endMarker: null, // 终点标记
      foodMarkers: [], // 小吃店标记数组
      mapLoading: true, // 地图加载状态
      showAllFoodShops: false, // 是否显示所有小吃
      mapInitialized: false, // 地图是否已初始化
      mapObserver: null, // Intersection Observer 实例
      showFullRoute: false // 是否显示完整公交路线
    };
  },
  computed: {
    currentUserId() {
      const user = this.$store.getters.getUser;
      return user ? user.id : null;
    },
    visitorSummary() {
      const active = this.visitorTypes.filter(v => v.count > 0);
      return active.map(v => `${v.label} x ${v.count}`).join(', ');
    },
    // 解析后的景点图片数组
    parsedAttractionPics() {
      if (!this.detail.attractionPic) return [];
      try {
        return JSON.parse(this.detail.attractionPic);
      } catch (e) {
        return [];
      }
    },
    // 当前交通方式的数据
    currentTrafficData() {
      if (!this.trafficInfo) {
        return null;
      }
      
      // 获取当前交通方式的数据（drive/bus/taxi）
      const trafficTypeData = this.trafficInfo[this.currentTrafficType];
      
      // 特殊处理：公交模式即使没有数据也要返回对象，以便显示"没有找到线路"提示
      if (this.currentTrafficType === 'bus') {
        return {
          distance: this.trafficInfo.distance || 0,
          duration: trafficTypeData ? `${trafficTypeData.time}分钟` : '-',
          cost: trafficTypeData ? trafficTypeData.cost : 0,
          polyline: trafficTypeData ? trafficTypeData.polyline : null,
          routeDescription: trafficTypeData ? (trafficTypeData.description || '') : ''
        };
      }
      
      // 其他交通方式：没有数据则返回 null
      if (!trafficTypeData) {
        return null;
      }
      
      // 适配新的数据结构
      return {
        distance: this.trafficInfo.distance,
        duration: `${trafficTypeData.time}分钟`,
        cost: trafficTypeData.cost,
        polyline: trafficTypeData.polyline, // 添加 polyline 字段
        routeDescription: trafficTypeData.description || '' // 添加路线描述字段
      };
    },
    // 判断公交模式是否没有路线
    hasNoBusRoute() {
      if (this.currentTrafficType !== 'bus') return false;
      if (!this.trafficInfo || !this.trafficInfo.bus) return true;
      // 如果没有 polyline 或 routeDescription，说明没有公交路线
      return !this.trafficInfo.bus.polyline && !this.trafficInfo.bus.description;
    },
    // 显示的小吃列表（默认显示前4个）
    displayedFoodShops() {
      if (!this.foodShops || this.foodShops.length === 0) return [];
      return this.showAllFoodShops ? this.foodShops : this.foodShops.slice(0, 4);
    }
  },
  watch: {
    // 监听交通方式变化，重新绘制地图
    currentTrafficType() {
      if (this.trafficInfo) {
        this.$nextTick(() => {
          const polyline = this.currentTrafficData ? this.currentTrafficData.polyline : null;
          this.drawRouteOnMap(polyline);
        });
      }
    },
    // 监听 Tab 切换，切换到路线推荐时重新渲染地图
    activeName(newVal) {
      if (newVal === 'first' && this.trafficInfo && this.selectedAttractionId) {
        console.log('切换到路线推荐 Tab，重新渲染地图');
        this.$nextTick(() => {
          const polyline = this.currentTrafficData ? this.currentTrafficData.polyline : null;
          this.drawRouteOnMap(polyline);
        });
      }
    }
  },
  mounted() {
    this.getDetail();
    this.getComment()
    this.loadRouteRecommend();
    this.$nextTick(() => {
      const container = document.querySelector('.content');
      if (container) {
        setTimeout(()=>{
          container.scrollTop = 0;
        },10)
      }
    });
  },
  beforeDestroy() {
    // 组件销毁时清理地图实例
    if (this.mapInstance) {
      // 先清理所有小吃店标记
      this.foodMarkers.forEach(marker => {
        this.mapInstance.remove(marker);
      });
      this.foodMarkers = [];
      
      // 清理多段路线
      if (this.polylineInstances && this.polylineInstances.length > 0) {
        this.polylineInstances.forEach(line => {
          this.mapInstance.remove(line);
        });
        this.polylineInstances = [];
      }
      
      // 清理旧版单条路线
      if (this.polylineInstance) {
        this.mapInstance.remove(this.polylineInstance);
      }
      if (this.startMarker) {
        this.mapInstance.remove(this.startMarker);
      }
      if (this.endMarker) {
        this.mapInstance.remove(this.endMarker);
      }
      
      // 最后销毁地图实例
      this.mapInstance.destroy();
      this.mapInstance = null;
      this.polylineInstance = null;
      this.polylineInstances = [];
      this.startMarker = null;
      this.endMarker = null;
    }
  },
  methods: {
    selTab(val){
      if(val.index == 1){
        this.getComment()
      }
    },
    getComment(){
      request({
        url: config.backHost + "/attractionComment/getByAttractionId/"+this.$route.query.id,
      }).then(res => {
        if (res.code === 200) {
          this.comments = res.data
        }
      })
    },
    submitComment() {
      if (!this.commentForm.commentDetail) return this.$message.warning("请输入评价内容");
      this.submitting = true;
      request({
        url: config.backHost + "/attractionComment/addComment",
        data: this.commentForm
      }).then(res => {
        if (res.code === 200) {
          this.$message.success("发布成功");
          this.commentForm.commentDetail = "";
          this.commentForm.pictureUrl = "[]";
          location.reload();
        }
      }).finally(() => this.submitting = false);
    },
    handleDelete(id) {
      this.$confirm('确认删除？删除后不可回退', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({ url: config.backHost + `/attractionComment/delById/${id}` }).then(res => {
          if (res.code === 200) this.getComment();
        });
      });
    },
    selTicket(ticket){
      this.selectedTicketId = ticket.id
      this.currentPrice = ticket.ticketPrice;
    },
    async getDetail() {
      const id = this.$route.query.id
      const res = await request({ url: config.backHost + `/attractionInfo/getById/${id}` });
      if (res.code === 200) {
        this.detail = res.data;
        this.getClassify()
        this.getTickets(id);
      }
    },
    getClassify(){
      request({
        url: config.backHost + "/attractionType/list",
      }).then(res => {
        if (res.code === 200) {
          this.classify = res.data
        }
      });
    },
    async getTickets(id) {
      const res = await request({
        url: config.backHost + "/ticketInfo/listPage",
        method: "POST",
        data: {
          params: { 
            attractionId: id,
            status: 1  // 只查询上架的门票（1=上架，2=下架）
          },
          pageBean: { page: 1, pageSize: -1 }
        }
      });
      if (res.code === 200) {
        this.ticketList = res.data;
        if (this.ticketList.length > 0) {
          this.selTicket(this.ticketList[0])
        }
      }
    },
    handleCollect() {
      let url = ''
      if(this.detail.isCollect == 1){
        url = '/attractionCollection/noCollect/'+this.detail.id
      } else {
        url = '/attractionCollection/collect/'+this.detail.id
      }
      request({
        url: config.backHost + url,
      }).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg)
          this.getDetail();
        }
      });
    },
    handleOrder() {
      this.$confirm('是否确认预订?', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: config.backHost + '/attractionOrder/submit',
          data:{
            "attractionId":this.$route.query.id,
            "ticketId": this.selectedTicketId,
            "buyCount": this.visitorTypes[0].count,
            "totalPrice": this.currentPrice * this.visitorTypes[0].count
          }
        }).then(res => {
          document.querySelector("body").innerHTML = res.data;
          document.forms[0].submit();
        });
      })
    },
    // 加载路线推荐
    async loadRouteRecommend() {
      const attractionId = this.$route.query.id;
      if (!attractionId) return;
      
      this.routeLoading = true;
      try {
        const res = await request({
          url: config.backHost + '/api/route/getNextAttraction',
          method: 'POST',
          data: {
            attractionId: attractionId
          }
        });
        if (res.code === 200) {
          this.nextAttractions = res.data || [];
        }
      } catch (error) {
        console.error('加载路线推荐失败:', error);
        this.$message.error('加载路线推荐失败');
      } finally {
        this.routeLoading = false;
      }
    },
    // 选择景点
    async selectAttraction(item) {
      this.selectedAttractionId = item.id;
      
      // 加载交通信息和小吃信息
      await Promise.all([
        this.loadTrafficInfo(item),
        this.loadFoodShops(item)
      ]);
    },
    // 加载交通信息
    async loadTrafficInfo(item) {
      this.trafficLoading = true;
      try {
        const params = {
          fromAttractionId: this.$route.query.id,
          toAttractionId: item.id,
          transportType: 'all' // 获取所有交通方式
        };
            
        const res = await request({
          url: config.backHost + '/api/route/getTrafficInfoByType',
          method: 'POST',
          data: params
        });
            
        if (res.code === 200 && res.data && Object.keys(res.data).length > 0) {
          const data = res.data;
          this.trafficInfo = {
            distance: data.distance || 0,
            drive: data.drive || null,
            bus: data.bus || null,
            taxi: data.taxi || null
          };
              
          // 加载完成后，自动绘制地图（无论有没有 polyline 数据）
          setTimeout(() => {
            const polyline = this.currentTrafficData ? this.currentTrafficData.polyline : null;
            this.drawRouteOnMap(polyline);
          }, 100);
        } else {
          this.trafficInfo = null;
          this.$message.warning('该路线暂无交通信息');
        }
      } catch (error) {
        console.error('加载交通信息失败:', error);
        this.$message.error('加载交通信息失败');
        this.trafficInfo = null;
      } finally {
        this.trafficLoading = false;
      }
    },
    // 加载沿途小吃
    async loadFoodShops(item) {
      this.foodLoading = true;
      try {
        const params = {
          fromAttractionId: this.$route.query.id,
          toAttractionId: item.id
        };
        const res = await request({
          url: config.backHost + '/api/route/getNearFoodShop',
          method: 'POST',
          data: params
        });
        if (res.code === 200) {
          let shops = res.data || [];
          // 按顺路指数降序排序（优先展示离路线近的）
          shops.sort((a, b) => {
            const indexA = a.convenienceIndex || 0;
            const indexB = b.convenienceIndex || 0;
            return indexB - indexA; // 降序
          });
          this.foodShops = shops;
        }
      } catch (error) {
        console.error('加载小吃信息失败:', error);
        this.foodShops = [];
      } finally {
        this.foodLoading = false;
      }
    },
    // 切换交通方式
    switchTrafficType(type) {
      this.currentTrafficType = type;
    },
    // 格式化游玩时间（分钟转小时）
    formatPlayTime(minutes) {
      if (!minutes) return '';
      if (minutes < 60) {
        return `${minutes}分钟`;
      }
      const hours = Math.floor(minutes / 60);
      const mins = minutes % 60;
      if (mins === 0) {
        return `${hours}小时`;
      }
      return `${hours}小时${mins}分钟`;
    },
    // 跳转到小吃详情页
    goToFoodDetail(foodId) {
      this.$router.push({
        name: 'foodDetail',
        query: { id: foodId }
      });
    },
    // 打开高德地图导航
    openAmapNavigation() {
      if (!this.trafficInfo || !this.selectedAttractionId) {
        this.$message.warning('请先选择景点');
        return;
      }
      
      // 获取起点和终点信息
      const fromAttraction = this.detail; // 当前景点（起点）
      const toAttraction = this.nextAttractions.find(a => a.id === this.selectedAttractionId); // 目标景点（终点）
      
      if (!fromAttraction || !toAttraction) {
        this.$message.error('景点信息不完整');
        return;
      }
      
      // 检查坐标是否存在
      if (!fromAttraction.longitude || !fromAttraction.latitude || !toAttraction.longitude || !toAttraction.latitude) {
        this.$message.error('景点坐标信息缺失');
        return;
      }
      
      // 确定导航类型
      let mode = 'drive'; // 默认驾车
      if (this.currentTrafficType === 'bus') {
        mode = 'bus'; // 公交
      } else if (this.currentTrafficType === 'taxi') {
        mode = 'drive'; // 打车也用驾车模式
      }
      
      // 使用高德官方推荐的标准导航跳转链接
      // 格式：https://uri.amap.com/navigation?from=经度,纬度&to=经度,纬度&mode=交通方式&src=来源标识
      const fromCoord = `${fromAttraction.longitude},${fromAttraction.latitude}`;
      const toCoord = `${toAttraction.longitude},${toAttraction.latitude}`;
      
      const amapUrl = `https://uri.amap.com/navigation?from=${fromCoord}&to=${toCoord}&mode=${mode}&src=贵阳旅游攻略系统`;
      
      // 在新窗口打开
      const newWindow = window.open(amapUrl, '_blank');
      
      if (!newWindow) {
        this.$message.error('弹窗被浏览器拦截，请允许弹窗后重试');
      } else {
        this.$message.success('正在打开高德地图导航...');
      }
    },
    // 复制起点/终点地址
    copyRouteAddress() {
      const fromAttraction = this.detail;
      const toAttraction = this.nextAttractions.find(a => a.id === this.selectedAttractionId);
      
      if (!fromAttraction || !toAttraction) {
        this.$message.error('景点信息不完整');
        return;
      }
      
      const text = `起点：${fromAttraction.attractionName}（${fromAttraction.attractionPlace}）\n终点：${toAttraction.attractionName}（${toAttraction.attractionPlace}）`;
      
      // 使用 Clipboard API
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(() => {
          this.$message.success('复制成功！');
        }).catch(() => {
          this.fallbackCopyText(text);
        });
      } else {
        this.fallbackCopyText(text);
      }
    },
    // 降级复制方案
    fallbackCopyText(text) {
      const textarea = document.createElement('textarea');
      textarea.value = text;
      textarea.style.position = 'fixed';
      textarea.style.opacity = '0';
      document.body.appendChild(textarea);
      textarea.select();
      try {
        document.execCommand('copy');
        this.$message.success('复制成功！');
      } catch (err) {
        this.$message.error('复制失败，请手动复制');
      }
      document.body.removeChild(textarea);
    },
    // 获取交通方式图标
    getTrafficIcon(type) {
      const iconMap = {
        drive: 'el-icon-position',
        bus: 'el-icon-guide',
        taxi: 'el-icon-truck'
      };
      return iconMap[type] || 'el-icon-location';
    },
    // 获取指定交通方式的数据
    getTrafficTypeData(type) {
      if (!this.trafficInfo) {
        return null;
      }
      
      const data = this.trafficInfo[type];
      
      // 特殊处理：公交模式即使没有数据也要返回对象
      if (type === 'bus') {
        return {
          distance: this.trafficInfo.distance || 0,
          time: data ? data.time : '-',
          cost: data ? data.cost : 0
        };
      }
      
      // 其他交通方式：没有数据则返回 null
      if (!data) {
        return null;
      }
      
      return {
        distance: this.trafficInfo.distance,
        time: data.time,
        cost: data.cost
      };
    },
    // 切换显示所有小吃
    toggleShowAllFoodShops() {
      this.showAllFoodShops = !this.showAllFoodShops;
    },
    // 切换公交路线展开/收起
    toggleRouteExpand() {
      this.showFullRoute = !this.showFullRoute;
    },
    // 初始化高德地图
    initAMap() {
      return new Promise((resolve, reject) => {
        if (window.AMap) {
          resolve(window.AMap);
          return;
        }
        
        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = `https://webapi.amap.com/maps?v=2.0&key=${config.amapKeys.jsApi}&plugin=AMap.Scale,AMap.ToolBar`;
        script.onload = () => {
          resolve(window.AMap);
        };
        script.onerror = () => {
          reject(new Error('高德地图API加载失败，请检查Key是否正确'));
        };
        document.head.appendChild(script);
      });
    },
    // 在地图上绘制路线
    async drawRouteOnMap(polyline) {
      // 如果当前不在路线推荐 Tab，不创建地图
      if (this.activeName !== 'first') {
        return;
      }
      
      // 如果没有选中的景点，不创建地图
      if (!this.selectedAttractionId) {
        return;
      }
      
      this.mapLoading = true;
      
      try {
        await this.$nextTick();
        await this.$nextTick();
        await this.$nextTick();
        
        // 额外等待一段时间，确保 DOM 完全渲染
        await new Promise(resolve => setTimeout(resolve, 100));
        
        const container = this.$refs.amapContainer;
        const wrapper = this.$refs.amapWrapper;
        
        if (!container || !wrapper) {
          console.error('地图容器不存在');
          this.$message.error('地图容器未找到');
          this.mapLoading = false;
          return;
        }
        
        // 检查容器尺寸
        const containerRect = container.getBoundingClientRect();
        
        // 如果容器高度为 0，尝试强制设置高度
        if (containerRect.height === 0) {
          // 强制设置容器高度
          container.style.height = '400px';
          // 再次等待渲染
          await this.$nextTick();
          await new Promise(resolve => setTimeout(resolve, 100));
          
          // 重新检查尺寸
          const newRect = container.getBoundingClientRect();
          
          if (newRect.height === 0) {
            console.error('容器高度仍然为 0，无法初始化地图');
            this.$message.error('地图容器尺寸异常');
            this.mapLoading = false;
            return;
          }
        }
        
        const AMap = await this.initAMap();
        
        if (this.mapInstance) {
          this.mapInstance.destroy();
          this.mapInstance = null;
        }
        
        this.polylineInstance = null;
        this.startMarker = null;
        this.endMarker = null;
        this.foodMarkers = [];
        
        this.mapInstance = new AMap.Map(container, {
          zoom: 12,
          center: [106.7134, 26.5783],
          viewMode: '2D',
          resizeEnable: true
        });
        
        const fromAttraction = this.detail;
        const toAttraction = this.nextAttractions.find(a => a.id === this.selectedAttractionId);
        
        if (!fromAttraction || !toAttraction || !fromAttraction.longitude || !toAttraction.longitude) {
          console.warn('景点坐标信息缺失');
          this.$message.warning('景点坐标信息缺失');
          this.mapLoading = false;
          return;
        }
        
        const startPoint = [fromAttraction.longitude, fromAttraction.latitude];
        const endPoint = [toAttraction.longitude, toAttraction.latitude];
        
        let path = [];
        if (polyline) {
          path = this.parsePolyline(polyline);
        }
        
        this.startMarker = new AMap.Marker({
          position: path.length > 0 ? path[0] : startPoint,
          content: `<div style="
            width: 26px;
            height: 26px;
            background: #3b82f6;
            border-radius: 50%;
            border: 2px solid #fff;
            box-shadow: 0 2px 6px rgba(0,0,0,0.3);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 12px;
            font-weight: bold;
            font-family: Arial, sans-serif;
          ">起</div>`,
          offset: new AMap.Pixel(-13, -13),
          title: '起点'
        });
        
        this.endMarker = new AMap.Marker({
          position: path.length > 0 ? path[path.length - 1] : endPoint,
          content: `<div style="
            width: 26px;
            height: 26px;
            background: #ef4444;
            border-radius: 50%;
            border: 2px solid #fff;
            box-shadow: 0 2px 6px rgba(0,0,0,0.3);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 12px;
            font-weight: bold;
            font-family: Arial, sans-serif;
          ">终</div>`,
          offset: new AMap.Pixel(-13, -13),
          title: '终点'
        });
        
        this.mapInstance.add([this.startMarker, this.endMarker]);
        
        // 清除旧路线
        if (this.polylineInstance) {
          this.mapInstance.remove(this.polylineInstance);
          this.polylineInstance = null;
        }
        if (this.polylineInstances && this.polylineInstances.length > 0) {
          this.polylineInstances.forEach(line => this.mapInstance.remove(line));
          this.polylineInstances = [];
        }
        
        let routes = [];
        
        // 如果是公交模式且有步骤数据，尝试分段绘制
        if (this.currentTrafficType === 'bus' && this.trafficInfo.bus && this.trafficInfo.bus.steps) {
          this.trafficInfo.bus.steps.forEach((step) => {
            if (step.polyline) {
              const points = this.parsePolyline(step.polyline);
              if (points.length > 1) {
                const isWalking = step.type === 'walking';
                const line = new AMap.Polyline({
                  path: points,
                  strokeColor: isWalking ? '#999999' : '#1890FF', // 步行灰，公交蓝
                  strokeWeight: isWalking ? 4 : 6,
                  strokeOpacity: 0.8,
                  strokeStyle: isWalking ? 'dashed' : 'solid',    // 步行虚线，公交实线
                  strokeDasharray: isWalking ? [10, 5] : null,
                  lineJoin: 'round',
                  lineCap: 'round'
                });
                routes.push(line);
              }
            }
          });
        }
        
        // 如果没有分段数据，或者不是公交模式，绘制单条路线
        if (routes.length === 0 && path.length > 1) {
          let routeColor = '#FF8A45';
          if (this.currentTrafficType === 'bus') {
            routeColor = '#1890FF';
          } else if (this.currentTrafficType === 'taxi') {
            routeColor = '#52C41A';
          }
          
          const line = new AMap.Polyline({
            path: path,
            strokeColor: routeColor,
            strokeWeight: 6,
            strokeOpacity: 0.8,
            strokeStyle: 'solid',
            lineJoin: 'round',
            lineCap: 'round'
          });
          routes.push(line);
        }
        
        // 添加到地图
        if (routes.length > 0) {
          this.mapInstance.add(routes);
          this.polylineInstances = routes;
          
          const fitViewElements = [this.startMarker, this.endMarker, ...routes];
          
          if (this.foodShops && this.foodShops.length > 0) {
            this.drawFoodMarkers(this.foodShops);
            fitViewElements.push(...this.foodMarkers);
          }
          
          this.mapInstance.setFitView(fitViewElements);
        } else {
          this.mapInstance.setFitView([this.startMarker, this.endMarker]);
        }
        
        setTimeout(() => {
          if (this.mapInstance) {
            this.mapInstance.resize();
            // 再次调整视野确保地图完全显示
            const fitViewElements = [this.startMarker, this.endMarker];
            if (this.polylineInstance) {
              fitViewElements.push(this.polylineInstance);
            }
            if (this.foodMarkers && this.foodMarkers.length > 0) {
              fitViewElements.push(...this.foodMarkers);
            }
            this.mapInstance.setFitView(fitViewElements);
          }
          this.mapLoading = false;
        }, 300);
      } catch (error) {
        console.error('地图渲染失败:', error);
        this.$message.error('地图加载失败：' + error.message);
        this.mapLoading = false;
      }
    },
    // 解析 polyline 字符串为坐标数组
    parsePolyline(polylineStr) {
      if (!polylineStr) return [];
      
      try {
        // polyline 可能是 JSON 字符串或分号分隔的字符串
        let points = [];
        if (typeof polylineStr === 'string') {
          // 尝试解析 JSON
          if (polylineStr.startsWith('[')) {
            points = JSON.parse(polylineStr);
          } else {
            // 分号分隔格式: "lng,lat;lng,lat"
            points = polylineStr.split(';').map(point => {
              const [lng, lat] = point.split(',').map(Number);
              return [lng, lat];
            });
          }
        }
        
        const validPoints = points.filter(p => !isNaN(p[0]) && !isNaN(p[1]));
        
        return validPoints;
      } catch (error) {
        console.error('解析 polyline 失败:', error);
        return [];
      }
    },
    // 绘制小吃店标记
    drawFoodMarkers(foodShops) {
      if (!this.mapInstance) return;
      
      // 清除旧的小吃店标记
      this.foodMarkers.forEach(marker => {
        this.mapInstance.remove(marker);
      });
      this.foodMarkers = [];
      
      // 为每个小吃店创建标记
      foodShops.forEach(shop => {
        // 尝试不同的字段名（兼容多种情况）
        const lng = shop.longitude || shop.lng || shop.shopLongitude || shop.shopLng;
        const lat = shop.latitude || shop.lat || shop.shopLatitude || shop.shopLat;
        
        // 检查经纬度是否有效（排除 null, undefined, 0, 空字符串）
        if (!lng || !lat) return;
        
        // 获取小吃图片 URL
        let foodImageUrl = '';
        if (shop.images) {
          try {
            const images = typeof shop.images === 'string' ? JSON.parse(shop.images) : shop.images;
            if (images && images.length > 0) {
              const imageId = images[0].id || images[0];
              foodImageUrl = config.backHost + '/file/download/' + imageId;
            }
          } catch (e) {
            console.error('解析小吃图片失败:', e);
          }
        }
        
        // 创建标记 - 使用小吃图片作为图标
        const marker = new AMap.Marker({
          position: [lng, lat],
          icon: new AMap.Icon({
            size: new AMap.Size(48, 48), // 图标尺寸
            image: foodImageUrl || 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png', // 使用小吃图片，如果没有则使用默认标记
            imageSize: new AMap.Size(48, 48)
          }),
          offset: new AMap.Pixel(-24, -48), // 偏移量，使图片底部中心对准坐标点
          title: shop.name
        });
        
        // 设置信息窗体内容
        const infoContent = `
          <div style="padding: 0; min-width: 180px; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.15);">
            ${foodImageUrl ? `
            <div style="width: 100%; height: 100px; overflow: hidden;">
              <img src="${foodImageUrl}" style="width: 100%; height: 100%; object-fit: cover;" />
            </div>` : ''}
            <div style="padding: 10px; background: #fff;">
              <div style="font-size: 14px; font-weight: bold; color: #333; margin-bottom: 8px;">${shop.name}</div>
              <div style="font-size: 12px; color: #666; margin-bottom: 4px;">
                <i class="el-icon-location-outline" style="color: #FF8A45; margin-right: 4px;"></i>
                ${shop.address || '地址未知'}
              </div>
              <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 8px;">
                <div style="font-size: 12px; color: #FF8A45; font-weight: bold;">
                  人均：￥${shop.avgPrice}
                </div>
                <div style="font-size: 11px; color: #999;">
                  距路线：${shop.distance}km
                </div>
              </div>
            </div>
          </div>
        `;
        
        // 创建信息窗体
        const infoWindow = new AMap.InfoWindow({
          content: infoContent,
          offset: new AMap.Pixel(0, -35)
        });
        
        // 点击标记显示信息窗体
        marker.on('click', () => {
          infoWindow.open(this.mapInstance, marker.getPosition());
        });
        
        // 添加到地图
        this.mapInstance.add(marker);
        this.foodMarkers.push(marker);
      });
    }
  }
};
</script>

<style scoped lang="scss">
$primary-blue: $theme-color;
$bg-color: #f5f7fa;
$dark-text: #1a2b49;

.attraction-detail { background: $bg-color; min-height: 100%; }
.container { max-width: 1220px; margin: 0 auto; padding: 20px;position: relative;overflow-y: auto }
.custom-header { margin-bottom: 20px; }
.shadow-card { background: #fff; border-radius: 12px; box-shadow: 0 2px 15px rgba(0,0,0,0.06); margin-bottom: 25px; overflow: hidden; }

.gallery-wrapper { .gallery-img { width: 100%; height: 100%; } }

.article-body {
  padding: 0px 35px 30px;
  .attr-title { font-size: 28px; font-weight: bold; margin-bottom: 15px; color: $dark-text; }
  .meta-info { color: #555; font-size: 14px; margin-bottom: 30px; span { margin-right: 20px; i { color: $primary-blue; margin-right: 5px; } } }
}

.section-label { font-size: 20px; font-weight: bold; margin: 0px 0 20px; display: flex; align-items: center;
  &::before { content: ""; width: 4px; height: 18px; background: $primary-blue; margin-right: 10px; border-radius: 2px; }
}

.ticket-card {
  cursor: pointer;
  position: relative; border: 1px solid #e0e0e0; border-radius: 8px; padding: 20px 0px 0; margin-bottom: 15px;
  overflow: hidden;
  transition: 0.3s;
  &:hover, &.active { border-color: $primary-blue; box-shadow: 0 4px 12px rgba(0,108,228,0.1); }
  .ticket-badge { position: absolute; top: -10px; left: 15px; background: #d4111e; color: #fff; font-size: 12px; padding: 2px 8px; border-radius: 4px; }
  .ticket-info { flex: 1;padding: 0 20px; .t-name { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 8px; } .t-desc { font-size: 14px; color: #666; margin-bottom: 12px; } .t-policy { font-size: 12px; color: #008009; span { margin-right: 15px; i { margin-right: 4px; } } } }
  .ticket-price-action { background: #EBEEF1;padding: 20px 20px 20px;display: flex;justify-content: space-between;align-items: center;.price-val { font-size: 24px; font-weight: bold; color: $primary-blue; .unit { font-size: 14px; } small { font-size: 12px; color: #999; } } .btns { display: flex; gap: 10px; } }
}

.booking-sidebar {
  padding: 24px;
  position: fixed;
  top:134px;
  right: 0;
  z-index: 11;
  width: calc(1220px * (7/24) - 20px - 8.5px);
  left: calc(1220px * (17/24) + ((100vw - 1220px)/2) + 12.5px);
  box-sizing: border-box;
  .top-action-bar {
    display: flex; justify-content: flex-end; gap: 15px; margin-bottom: 20px;
    font-size: 13px; font-weight: 500; color: $dark-text;
    span { cursor: pointer; i { font-size: 16px; margin-right: 4px; vertical-align: middle; } }
  }

  .price-header-v2 {
    margin-bottom: 25px;
    .sold-out-tag { background: #d4111e; color: #fff; display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 11px; margin-bottom: 8px; }
    .price-label { color: #666; font-size: 14px; display: block; }
    .price-amount { margin-top: 4px; .val { font-size: 26px; font-weight: 800; color: $dark-text; } .unit { font-size: 14px; color: #333; margin-left: 5px; } }
  }

  .custom-select-box {
    background: #edf2f7; border-radius: 12px; height: 48px;
    display: flex; align-items: center; padding: 0 15px; cursor: pointer;
    transition: background 0.2s;
    &:hover { background: #e2e8f0; }

    i { color: #2d3748; font-size: 18px; }
    .display-text { flex: 1; margin: 0 12px; font-weight: 600; color: $dark-text; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

    ::v-deep .el-input__inner { background: transparent !important; border: none !important; height: 48px; line-height: 48px; padding: 0; font-weight: 600; color: $dark-text; cursor: pointer; }
    ::v-deep .el-input__prefix { display: none; }
    ::v-deep .el-select { width: 100%; }
  }

  .mt-12 { margin-top: 12px; }

  .order-submit-btn { width: 100%; border-radius: 24px; height: 48px; font-size: 16px; font-weight: bold; margin-top: 20px; background: $primary-blue; border: none; &:hover { background: #0056b3; } }
}

.visitor-picker-panel {
  padding: 0px 20px;
  .visitor-row {
    display: flex; justify-content: space-between; align-items: center; padding: 15px 0; border-bottom: 1px solid #f0f2f5;
    &:last-child { border-bottom: none; }
    .v-name { font-weight: bold; color: $dark-text; font-size: 15px; }
    .v-age { font-size: 12px; color: #718096; margin-top: 2px; }
    .v-control { display: flex; align-items: center; gap: 12px; .v-num { min-width: 20px; text-align: center; font-weight: bold; } }
  }
  .v-notice { color: #a0aec0; font-size: 12px; margin-top: 15px; }
}
.visitor-picker-popover { border-radius: 12px !important; box-shadow: 0 10px 25px rgba(0,0,0,0.1) !important; padding: 20px !important; }

.detail-html-content { margin-top: 40px; line-height: 1.8; color: #444; }
.card-image {
  position: relative;
  max-height: 400px;
  overflow: hidden;
  display: flex;
  gap: 4px;
  .el-image{
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform .5s ease;
    max-height: 400px;
  }
  &.pic-count-2 {
    img {
      width: 100%;
    }
  }
  &.pic-count-3 {
    .pic-main {
      width: 100%;
    }
    .side-pics {
      width: 34%;
      display: flex;
      flex-direction: column;
      gap: 4px;
      img{
        width: 100%;
      }
    }
  }
}
@media (max-width: 1466px) {
  .booking-sidebar{
    top: 210px;
  }
}
@media (max-width: 1260px) {
  .booking-sidebar{
    width: calc(100vw * (7/24) - 20px - 22.5px);
    left: calc(100vw * (17/24));
  }
}
.comment-section {
  background: #fff;
  border-radius: 12px;
  padding: 0px 35px 30px;
  width: 100%;
  box-sizing: border-box;
  .comment-header {
    margin-bottom: 30px;line-height: 20px;
    .title { font-size: 24px; font-weight: bold; color: #1a1a1a; white-space: nowrap; }
    .line { height: 1px; background: #eee; width: 100%; }
  }
}

.input-card {
  background: #fff; padding: 20px; border-radius: 12px; border: 1px solid #eee;
  ::v-deep .el-textarea__inner { border: none; padding: 0; font-size: 15px; &:focus { outline: none; } }
  .input-footer { display: flex; justify-content: flex-end; align-items: center; gap: 15px; margin-top: 15px; }
  .submit-btn { background: $theme-color; color: #fff; border: none; padding: 10px 25px; border-radius: 20px; font-weight: bold; &:hover { opacity: 0.9; } }
}

.comment-item {
  padding: 25px 0; border-bottom: 1px solid #f7f7f7;
  &:last-child { border-bottom: none; }
  .c-main {
    flex: 1;
    .c-user-info { display: flex;align-items:center;justify-content: space-between;margin-bottom: 8px; .user-name { font-weight: bold; color: #333; margin-right: 12px; } .user-time { color: #ccc; font-size: 12px; } }
    .c-content { padding: 8px 0 16px;font-size: 15px; color: #444; line-height: 1.6; .at-tag { color: $theme-color; font-weight: bold; margin-right: 6px; } }
    .c-actions {
      margin-top: 12px; display: flex; gap: 20px;justify-content: right;
      .act-btn {
        font-size: 13px; color: #999; cursor: pointer; transition: 0.2s;
        i { margin-right: 4px; } &:hover { color:$theme-color;}
        &.del:hover { color: #f56c6c; }
      }
    }
  }
}
.children-list {
  margin-top: 15px; padding:0 15px;border-radius: 8px;
  width: calc(100% - 65px);
  margin-left: 65px;
  box-sizing: border-box;
  .child-item {
    display: flex; gap: 12px; padding: 25px 0; border-top: 1px solid #eee;
    &:last-child { border-bottom: none; padding-bottom: 0}
    .at-tag { color: $theme-color; font-weight: bold; margin-right: 5px; }
  }
}
::v-deep{
  .el-button--primary.is-plain{
    color: $theme-color;
    border-color: $theme-color;
    background: transparent;
  }
  .el-button--primary.is-plain:hover{
    background: $side-bar-container-hoverBackColor;
  }
}
::v-deep .el-tabs {
  .el-tabs__header {
    margin: 0;
    background: #fff;
    padding: 0 20px;
  }

  /* 激活状态的文字颜色 */
  .el-tabs__item {
    font-size: 16px;
    font-weight: 500;
    height: 55px;
    line-height: 55px;
    color: #666;
    transition: all 0.3s;

    &.is-active {
      color: #FF8A45 !important;
      font-weight: bold;
    }
    &:hover {
      color: #FF8A45;
    }
  }

  .el-tabs__active-bar {
    background-color: #FF8A45 !important;
    height: 3px;
    border-radius: 2px;
  }

  .el-tabs__nav-wrap::after {
    height: 1px;
    background-color: #f0f0f0;
  }
}

.comment-input-area {
  background: #fff;
  border: 1px solid #FDE6D8;
  padding: 20px;
  border-radius: 16px;
  margin: 20px 0 30px;
  box-shadow: 0 4px 12px rgba(253, 230, 216, 0.3);
  ::v-deep .el-textarea__inner {
    border: 1px solid #f0f0f0;
    border-radius: 10px;
    padding: 12px;
    font-size: 14px;
    background-color: #fafafa;
    transition: all 0.3s;

    &:focus {
      border-color: #FF8A45;
      background-color: #fff;
    }
  }

  .comment-action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 15px;
    .el-button--primary {
      background-color: #FF8A45 !important;
      border-color: #FF8A45 !important;
      padding: 10px 25px;
      font-weight: bold;
      box-shadow: 0 4px 10px rgba(255, 138, 69, 0.2);

      &:hover {
        background-color: #ff9d66 !important;
        transform: translateY(-1px);
      }
      &:active {
        transform: translateY(0);
      }
    }
  }

  ::v-deep .el-upload--picture-card {
    background-color: #FDE6D8;
    border: 1px dashed #FF8A45;
    color: #FF8A45;
    width: 60px;
    height: 60px;
    line-height: 70px;
    display: flex;
    align-items: center;
    justify-content: center;
    i {
      font-size: 20px;
    }
  }
}

.comment-item {
  .at-tag {
    color: #FF8A45 !important;
    background: #FDE6D8;
    padding: 2px 6px;
    border-radius: 4px;
  }
  .act-btn.del:hover {
    color: #FF8A45 !important;
  }
}

// 路线推荐模块样式
.route-recommend-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #f0f0f0;

  .loading-container {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px 0;
    color: #999;
    i {
      font-size: 24px;
      margin-right: 10px;
    }
  }

  // 推荐景点卡片
  .recommend-attractions {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 30px;

    .attraction-card {
      background: #fff;
      border: 2px solid #e8e8e8;
      border-radius: 12px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: $theme-color;
        box-shadow: 0 4px 12px rgba(255, 138, 69, 0.15);
        transform: translateY(-2px);
      }

      &.active {
        border-color: $theme-color;
        box-shadow: 0 4px 16px rgba(255, 138, 69, 0.25);
      }

      .card-image {
        width: 100%;
        height: 160px;
        overflow: hidden;
        background: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;

        .image-placeholder {
          text-align: center;
          color: #ccc;
          i {
            font-size: 40px;
            display: block;
            margin-bottom: 8px;
          }
          span {
            font-size: 12px;
          }
        }
      }

      .card-info {
        padding: 15px;

        .attraction-name {
          font-size: 16px;
          font-weight: bold;
          color: #333;
          margin-bottom: 8px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .attraction-location {
          font-size: 13px;
          color: #666;
          margin-bottom: 10px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          i {
            margin-right: 4px;
            color: $theme-color;
          }
        }

        .attraction-distance,
        .attraction-duration,
        .attraction-playtime {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 6px 0;
          font-size: 13px;
          border-top: 1px dashed #f0f0f0;

          .distance-label,
          .duration-label,
          .playtime-label {
            color: #999;
          }

          .distance-value,
          .duration-value,
          .playtime-value {
            color: $theme-color;
            font-weight: bold;
          }
        }
      }
    }
  }

  // 交通信息区域
  .traffic-section {
    margin-top: 30px;
    padding: 20px;
    background: #f9fafb;
    border-radius: 12px;

    // 横向对比卡片
    .traffic-compare-cards {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;
      margin-bottom: 24px;

      .traffic-card {
        background: #fff;
        border: 2px solid #e8e8e8;
        border-radius: 12px;
        padding: 16px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 16px rgba(255, 138, 69, 0.15);
          border-color: lighten($theme-color, 20%);
        }

        &.active {
          border-color: $theme-color;
          background: linear-gradient(135deg, #fff 0%, lighten($theme-color, 45%) 100%);
          box-shadow: 0 8px 20px rgba(255, 138, 69, 0.25);

          .card-header {
            .type-name {
              color: $theme-color;
            }
          }
        }

        .card-header {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;
          margin-bottom: 12px;
          padding-bottom: 12px;
          border-bottom: 1px solid #f0f0f0;

          i {
            font-size: 20px;
            color: $theme-color;
          }

          .type-name {
            font-size: 16px;
            font-weight: bold;
            color: #333;
            transition: color 0.3s;
          }
        }

        .card-body {
          .info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            font-size: 13px;

            .info-label {
              color: #999;
            }

            .info-value {
              color: #333;
              font-weight: 600;

              &.price {
                color: $theme-color;
                font-size: 15px;
              }
            }
          }
        }
      }
    }

    // 详细信息区域
    .traffic-detail-info {
      background: #fff;
      border-radius: 8px;
      padding: 20px;
      margin-top: 16px;

      .detail-row {
        display: flex;
        padding: 12px 0;
        border-bottom: 1px dashed #e8e8e8;
        font-size: 14px;
        align-items: flex-start;

        &:last-child {
          border-bottom: none;
        }

        .label {
          color: #666;
          min-width: 110px;
          flex-shrink: 0;
          display: flex;
          align-items: center;
          gap: 6px;

          i {
            color: $theme-color;
            font-size: 16px;
          }
        }

        .value {
          color: #333;
          flex: 1;
          font-weight: 500;
        }
      }

      // 导航按钮组
      .navigation-actions {
        display: flex;
        gap: 12px;
        margin-top: 20px;
        padding-top: 16px;
        border-top: 1px solid #f0f0f0;

        .nav-btn {
          flex: 1;
          height: 44px;
          font-size: 15px;
          font-weight: 500;
          border-radius: 22px;
          background: linear-gradient(135deg, $theme-color 0%, darken($theme-color, 10%) 100%);
          border: none;
          box-shadow: 0 4px 12px rgba(255, 138, 69, 0.3);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

          i {
            font-size: 18px;
            transition: transform 0.3s;
          }

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(255, 138, 69, 0.4);
            background: linear-gradient(135deg, darken($theme-color, 5%) 0%, darken($theme-color, 15%) 100%);

            i {
              transform: scale(1.2);
            }
          }

          &:active {
            transform: translateY(0);
          }
        }

        .copy-btn {
          flex: 0 0 auto;
          height: 44px;
          padding: 0 20px;
          border-radius: 22px;
          border: 1px solid #dcdfe6;
          background: #fff;
          color: #606266;
          transition: all 0.3s;

          i {
            font-size: 16px;
            margin-right: 6px;
          }

          &:hover {
            border-color: $theme-color;
            color: $theme-color;
            background: lighten($theme-color, 45%);
          }

          &:active {
            transform: scale(0.98);
          }
        }
      }

      // 公交路线详情
      .route-description {
        margin-top: 16px;
        padding: 16px;
        background: linear-gradient(135deg, #f0f7ff 0%, #e6f0ff 100%);
        border-radius: 8px;
        border-left: 4px solid #409EFF;
        overflow: visible; // 确保展开时不被父容器裁剪
        min-height: 80px; // 最小高度，防止内容切换时跳动

        // 没有找到线路提示
        .no-route-message {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 20px 0;
          text-align: center;

          i {
            font-size: 48px;
            color: #E6A23C;
            margin-bottom: 12px;
          }

          .message-text {
            font-size: 15px;
            color: #909399;
            font-weight: 500;
          }
        }

        .route-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 15px;
          font-weight: 600;
          color: #409EFF;
          margin-bottom: 10px;

          i {
            font-size: 18px;
          }
        }

        .route-content {
          font-size: 14px;
          line-height: 1.8;
          color: #333;
          padding-left: 26px;
          word-break: break-word; // 允许单词内换行
          overflow-wrap: break-word; // 长单词换行
          
          // 折叠状态
          &.collapsed {
            max-height: 60px; // 约3行高度
            overflow: hidden;
            position: relative;
            
            // 添加渐变遮罩
            &::after {
              content: '';
              position: absolute;
              bottom: 0;
              left: 0;
              right: 0;
              height: 30px;
              background: linear-gradient(to bottom, transparent, #e6f0ff);
            }
          }
          
          // 展开状态 - 移除高度限制
          &:not(.collapsed) {
            max-height: none !important;
            overflow: visible !important;
            height: auto !important;
          }
          
          // 公交路线高亮显示
          strong {
            color: #409EFF;
            font-weight: 600;
          }
        }
        
        // 切换按钮
        .route-toggle {
          text-align: right;
          margin-top: 8px;
          padding-left: 26px;
          
          .toggle-btn {
            color: #409EFF;
            font-size: 13px;
            padding: 0;
            
            &:hover {
              color: darken(#409EFF, 10%);
            }
            
            i {
              margin-left: 4px;
              transition: transform 0.3s;
            }
          }
        }
      }
    }
  }

  // 小吃推荐区域
  .food-section {
    margin-top: 30px;

    .section-subtitle {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin-bottom: 20px;
      padding-left: 14px;
      position: relative;

      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 18px;
        background: $theme-color;
        border-radius: 2px;
      }
    }

    .food-list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;

      .food-card {
        background: #fff;
        border: 1px solid #e8e8e8;
        border-radius: 12px;
        overflow: hidden;
        cursor: pointer;
        transition: all 0.3s;
        display: flex;

        &:hover {
          border-color: $theme-color;
          box-shadow: 0 4px 12px rgba(255, 138, 69, 0.15);
          transform: translateY(-2px);
        }

        .food-image {
          width: 120px;
          height: 120px;
          flex-shrink: 0;
          background: #f5f7fa;
          display: flex;
          align-items: center;
          justify-content: center;

          .image-placeholder-small {
            color: #ccc;
            i {
              font-size: 32px;
            }
          }
        }

        .food-info {
          flex: 1;
          padding: 12px;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .shop-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 6px;

            .shop-name {
              font-size: 15px;
              font-weight: bold;
              color: #333;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              flex: 1;
            }

            .business-status {
              flex-shrink: 0;
              font-size: 11px;
              padding: 2px 8px;
              border-radius: 10px;
              margin-left: 8px;

              &.open {
                background: #f0f9ff;
                color: #52c41a;
                border: 1px solid #b7eb8f;
              }

              &.closed {
                background: #fff1f0;
                color: #ff4d4f;
                border: 1px solid #ffa39e;
              }
            }
          }

          .shop-address {
            font-size: 12px;
            color: #999;
            margin-bottom: 6px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            i {
              margin-right: 4px;
              color: $theme-color;
            }
          }

          .recommend-reason {
            font-size: 12px;
            color: #666;
            margin-bottom: 8px;
            padding: 4px 8px;
            background: lighten($theme-color, 45%);
            border-radius: 4px;
            line-height: 1.4;

            i {
              color: $theme-color;
              margin-right: 4px;
              font-size: 12px;
            }
          }

          .shop-meta {
            display: flex;
            gap: 15px;
            font-size: 12px;
            margin-bottom: 6px;

            .meta-item {
              .meta-label {
                color: #999;
              }

              .meta-value {
                color: $theme-color;
                font-weight: bold;
              }
            }
          }

          .convenience-index {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 12px;
            margin-bottom: 4px;

            .index-label {
              color: #999;
            }

            .stars {
              display: flex;
              gap: 2px;

              i {
                font-size: 12px;
                color: #d9d9d9;

                &.active {
                  color: #faad14;
                }
              }
            }
          }

          .business-hours {
            font-size: 11px;
            color: #999;
            margin-top: 4px;

            i {
              margin-right: 4px;
              color: $theme-color;
            }
          }
        }
      }
    }

    // 高德地图容器样式
    .amap-wrapper {
      position: relative;
      width: 100%;
      margin-top: 20px;
      
      .map-loading {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 400px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        border-radius: 12px;
        z-index: 10;
        
        i {
          font-size: 32px;
          color: $theme-color;
          margin-bottom: 10px;
        }
        
        span {
          font-size: 14px;
          color: #666;
        }
      }
      
      .amap-container {
        width: 100%;
        height: 400px;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        border: 1px solid #e8e8e8;
        position: relative;
        z-index: 1;
      }
    }
    
    // 导航按钮行
    &.navigation-row {
      justify-content: center;
      padding: 20px 0 10px;
      border-bottom: none;
      
      .nav-btn {
        width: 100%;
        max-width: 300px;
        height: 44px;
        font-size: 15px;
        font-weight: 500;
        border-radius: 22px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
        }
        
        &:active {
          transform: translateY(0);
        }
      }
    }
  }
}

// 过渡动画
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.fade-in-enter-active, .fade-in-leave-active {
  transition: opacity 0.3s ease;
}

.fade-in-enter, .fade-in-leave-to {
  opacity: 0;
}

// 门票区域免费提示样式
.free-ticket-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
  color: #52c41a;
  
  i {
    font-size: 32px;
    margin-bottom: 8px;
  }
  
  span {
    font-size: 15px;
    font-weight: 500;
  }
}
</style>
