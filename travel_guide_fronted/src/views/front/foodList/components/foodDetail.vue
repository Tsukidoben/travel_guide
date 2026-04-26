<template>
  <div class="food-detail-container">
    <div class="content-wrapper">
      <el-page-header style="padding: 0 12.5px" @back="$router.go(-1)" content="小吃详情" class="custom-header"></el-page-header>
      <!-- 图片展示区 - 轮播图 -->
      <div class="section-card" v-if="foodDetail.foodPic">
        <div class="image-carousel">
          <el-carousel 
            :height="getPicArray(foodDetail.foodPic).length > 1 ? '450px' : 'auto'"
            :autoplay="getPicArray(foodDetail.foodPic).length > 1"
            :indicator-position="getPicArray(foodDetail.foodPic).length > 1 ? 'outside' : 'none'"
            arrow="always"
            trigger="click"
            v-if="getPicArray(foodDetail.foodPic).length > 1">
            <el-carousel-item v-for="(pic, index) in getPicArray(foodDetail.foodPic)" :key="index">
              <el-image 
                :src="getPicUrl(pic)" 
                :preview-src-list="getPicArray(foodDetail.foodPic).map(p => getPicUrl(p))"
                fit="cover"
                class="carousel-image">
              </el-image>
            </el-carousel-item>
          </el-carousel>
          <div v-else class="single-image">
            <el-image 
              :src="getPicUrl(getPicArray(foodDetail.foodPic)[0])" 
              :preview-src-list="[getPicUrl(getPicArray(foodDetail.foodPic)[0])]"
              fit="cover"
              class="single-image-inner">
            </el-image>
          </div>
        </div>
      </div>

      <!-- 基本信息头部 -->
      <div class="info-header">
        <div class="header-left">
          <div class="food-name-row">
            <h1 class="food-name">{{ foodDetail.foodName }}</h1>
            <el-tag v-if="foodDetail.categoryName" type="warning" size="small" effect="plain">
              <i class="el-icon-menu"></i> {{ foodDetail.categoryName }}
            </el-tag>
          </div>
          
          <!-- 店铺名和地点信息 -->
          <div class="shop-location-row" v-if="shopInfo.shopName || shopInfo.address">
            <span class="shop-name-inline" v-if="shopInfo.shopName">
              <i class="el-icon-office-building"></i>
              {{ shopInfo.shopName }}
            </span>
            <span class="location-inline" v-if="shopInfo.address">
              <i class="el-icon-location-outline"></i>
              {{ shopInfo.address }}
            </span>
          </div>
          
          <div class="rating-box">
            <el-rate v-model="foodDetail.score" disabled show-score text-color="#ff9900"></el-rate>
            <span class="score-text">{{ foodDetail.score }}分</span>
            <span class="review-count">{{ commentTotal }}条评价</span>
          </div>
          <div class="meta-info">
            <span class="price-tag">
              <i class="el-icon-coin"></i>
              ¥{{ foodDetail.avgPrice }}<small>/人</small>
            </span>
            <el-tag v-if="foodDetail.isRecommend" type="danger" size="medium" effect="dark">
              <i class="el-icon-star-on"></i> 必吃推荐
            </el-tag>
            <template v-if="foodDetail.tags">
              <el-tag v-for="(tag, idx) in foodDetail.tags.split(',').filter(t => t.trim())" :key="idx" size="small" type="info" effect="plain">
                {{ tag.trim() }}
              </el-tag>
            </template>
          </div>
        </div>
        <div class="header-right">
          <div class="collect-btn" @click="toggleFavorite">
            <span v-if="isFavorited" class="heart-icon active">❤</span>
            <span v-else class="heart-icon">♡</span>
            <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
          </div>
        </div>
      </div>

      <!-- 基础信息区 -->
      <div class="section-card intro-section">
        <h2 class="section-title">
          <i class="el-icon-info"></i>
          小吃简介
        </h2>
        <div class="intro-content">
          <div class="content-text">
            <p>{{ foodDetail.introduction }}</p>
          </div>
        </div>
        <div class="taste-feature" v-if="foodDetail.tasteFeature">
          <h3 class="sub-title">
            <i class="el-icon-food"></i>
            口味特色
          </h3>
          <div class="feature-content">
            <p>{{ foodDetail.tasteFeature }}</p>
          </div>
        </div>
        <div class="recommend-reason" v-if="foodDetail.recommendReason">
          <h3 class="sub-title recommend">
            <i class="el-icon-star-on"></i>
            推荐理由
          </h3>
          <div class="reason-content">
            <p>{{ foodDetail.recommendReason }}</p>
          </div>
        </div>
      </div>

      <!-- 店铺信息区 -->
      <div class="section-card shop-section" v-if="shopInfo.id">
        <h2 class="section-title">
          <i class="el-icon-shop"></i>
          店铺信息
        </h2>
        <div class="shop-info-box">
          <div class="shop-image-wrapper">
            <div class="shop-image" v-if="shopInfo.shopPic">
              <img :src="getPicUrlByJson(shopInfo.shopPic, 0)" alt="shop">
            </div>
            <div class="shop-image-placeholder" v-else>
              <i class="el-icon-picture-outline"></i>
              <span>暂无图片</span>
            </div>
          </div>
          <div class="shop-details">
            <h3 class="shop-name">
              <i class="el-icon-office-building"></i>
              {{ shopInfo.shopName }}
            </h3>
            <div class="shop-item">
              <div class="item-icon">
                <i class="el-icon-location-outline"></i>
              </div>
              <div class="item-content">
                <span class="item-label">地址：</span>
                <span class="item-value">{{ shopInfo.address }}</span>
              </div>
            </div>
            <div class="shop-item">
              <div class="item-icon">
                <i class="el-icon-time"></i>
              </div>
              <div class="item-content">
                <span class="item-label">营业时间：</span>
                <span class="item-value highlight">{{ shopInfo.businessHours }}</span>
              </div>
            </div>
            <div class="shop-item">
              <div class="item-icon">
                <i class="el-icon-phone"></i>
              </div>
              <div class="item-content">
                <span class="item-label">联系电话：</span>
                <a :href="'tel:' + shopInfo.phone" class="phone-link">
                  <span class="item-value">{{ shopInfo.phone }}</span>
                </a>
              </div>
            </div>
            <div class="shop-item">
              <div class="item-icon">
                <i class="el-icon-wallet"></i>
              </div>
              <div class="item-content">
                <span class="item-label">人均消费：</span>
                <span class="item-value price">¥{{ shopInfo.avgPrice }}/人</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户评价区 -->
      <div class="section-card comment-section">
        <div class="section-title-row">
          <h2 class="section-title">
            <i class="el-icon-chat-dot-round"></i>
            用户评价
            <span class="comment-badge" v-if="commentTotal > 0">{{ commentTotal }}</span>
          </h2>
        </div>

        <!-- 评论输入框 -->
        <div class="comment-input-area" v-if="userInfo && userInfo.id">
          <div class="score-input">
            <span class="label">评分：</span>
            <el-rate v-model="commentForm.score" :max="5" show-text></el-rate>
          </div>
          <el-input 
            type="textarea" 
            :rows="3" 
            placeholder="觉得小吃怎么样？快来评价吧..." 
            v-model="commentForm.content">
          </el-input>
          <div class="comment-action-bar">
            <upload-image-more v-model="commentForm.images" :max="3"/>
            <el-button type="primary" round @click="submitComment" :loading="submitLoading">发布评价</el-button>
          </div>
        </div>
        <div v-else class="login-tip">
          <el-button type="primary" round @click="$router.push({name: 'login'})">登录后发表评价</el-button>
        </div>

        <div class="comment-list" v-if="commentList.length > 0">
          <div v-for="comment in commentList" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <el-avatar :size="40" :src="getPicUrlByJson(comment.userHeadPic, 0)"></el-avatar>
              <div class="comment-user-info">
                <div class="user-name">{{ comment.userName }}</div>
                <el-rate v-model="comment.score" disabled text-color="#ff9900" :max="5"></el-rate>
              </div>
              <div class="comment-time">{{ comment.createTime }}</div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-images" v-if="comment.images">
              <el-image
                v-for="(img, idx) in getPicArray(comment.images)"
                :key="idx"
                :src="getPicUrl(img)"
                :preview-src-list="getPicArray(comment.images).map(i => getPicUrl(i))"
                fit="cover"
                class="comment-img">
              </el-image>
            </div>
          </div>
        </div>

        <div v-else class="empty-comment">
          <div class="empty-wrapper">
            <i class="el-icon-chat-line-square empty-icon"></i>
            <p class="empty-text">暂无评价，快来抢沙发吧~</p>
          </div>
        </div>

        <!-- 评论分页 -->
        <div class="pagination-container" v-if="commentList.length > 0">
          <el-pagination
            background
            @current-change="handleCommentPageChange"
            :current-page="commentPageNum"
            :page-size="commentPageSize"
            layout="total, prev, pager, next"
            :total="commentTotal">
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";
import UploadImageMore from "@/components/UploadImageMore.vue";

export default {
  name: "FoodDetail",
  mixins: [common],
  components: {
    UploadImageMore
  },
  computed: {
    userInfo() {
      return this.$store.getters.getUser || {};
    }
  },
  data() {
    return {
      foodId: null,
      foodDetail: {},
      shopInfo: {},
      commentList: [],
      commentPageNum: 1,
      commentPageSize: 10,
      commentTotal: 0,
      isFavorited: false,
      showCommentDialog: false,
      submitLoading: false,
      commentForm: {
        score: 5,
        content: '',
        images: ''
      },
      commentRules: {
        score: [{ required: true, message: '请选择评分', trigger: 'change' }],
        content: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.foodId = this.$route.query.id;
    if (this.foodId) {
      // 先获取小吃详情，然后在回调中获取店铺信息
      this.getFoodDetail().then(() => {
        this.getShopInfo();
      });
      this.getComments();
      // 确保 foodId 已经赋值后再检查收藏状态
      this.$nextTick(() => {
        this.checkFavorite();
      });
    }
  },
  methods: {
    // 添加 getPicUrl 方法
    getPicUrl(pic) {
      if (!pic) return '';
      // 如果 pic 是对象(已解析的 JSON),直接取 id 拼接下载 URL
      if (typeof pic === 'object' && pic.id) {
        return config.downloadUrl + pic.id;
      }
      // 如果 pic 是字符串,使用 getPicUrlByJson 处理
      return this.getPicUrlByJson(typeof pic === 'string' ? pic : JSON.stringify(pic), 0);
    },
    // 将图片 JSON 字符串转为数组
    getPicArray(picJson) {
      if (!picJson) return [];
      // 如果已经是数组,直接返回
      if (Array.isArray(picJson)) return picJson;
      // 如果是对象(不是字符串),包装成数组
      if (typeof picJson === 'object') return [picJson];
      // 如果是字符串,尝试解析
      try {
        const trimmed = picJson.trim();
        if (trimmed.startsWith('[') && trimmed.endsWith(']')) {
          return JSON.parse(trimmed);
        }
        // 单个 URL 字符串
        return [picJson];
      } catch (e) {
        return [picJson];
      }
    },
    getFoodDetail() {
      return request({
        url: config.backHost + `/api/food/info/detail/${this.foodId}`,
        method: 'POST'
      }).then(res => {
        if (res.code === 200) {
          const data = res.data;
          // 映射后端字段
          this.foodDetail = {
            id: data.id,
            foodName: data.name,
            foodPic: data.images,
            categoryId: data.categoryId,
            shopId: data.shopId,
            avgPrice: data.avgPrice,
            score: data.score,
            tags: data.tags,
            status: data.status,
            isRecommend: data.isRecommend,
            introduction: data.description,
            tasteFeature: data.tasteFeature,
            recommendReason: data.recommendReason,
            categoryName: data.categoryName,
            shopName: data.shopName
          };
        }
      });
    },
    
    getShopInfo() {
      if (!this.foodDetail.shopId) return;
      request({
        url: config.backHost + `/api/food/shop/detail/${this.foodDetail.shopId}`,
        method: 'POST'
      }).then(res => {
        if (res.code === 200) {
          const data = res.data;
          // 如果小吃详情中已经包含 foodShop，直接使用
          if (this.foodDetail.foodShop) {
            this.shopInfo = {
              id: this.foodDetail.foodShop.id,
              shopName: this.foodDetail.foodShop.name,
              shopPic: this.foodDetail.foodShop.images || '',
              address: this.foodDetail.foodShop.address,
              phone: this.foodDetail.foodShop.phone,
              avgPrice: this.foodDetail.foodShop.avgPrice,
              businessHours: this.foodDetail.foodShop.businessHours
            };
          } else {
            // 否则使用单独查询的结果
            this.shopInfo = {
              id: data.id,
              shopName: data.name,
              shopPic: data.images || '',
              address: data.address,
              phone: data.phone,
              avgPrice: data.avgPrice,
              businessHours: data.businessHours
            };
          }
        }
      });
    },
    
    getComments() {
      request({
        url: config.backHost + "/api/food/comment/listPage",
        method: 'POST',
        data: {
          params: {
            foodId: this.foodId
          },
          pageBean: {
            page: this.commentPageNum,
            pageSize: this.commentPageSize
          }
        }
      }).then(res => {
        if (res.code === 200) {
          const data = res.data;
          
          // 兼容不同的返回格式
          let records = [];
          if (Array.isArray(data)) {
            records = data;
          } else if (data && data.records) {
            records = data.records;
          } else if (data && data.list) {
            records = data.list;
          }
          
          this.commentList = records.map(item => ({
            id: item.id,
            foodId: item.foodId,
            userId: item.userId,
            userName: item.userName,
            userHeadPic: item.userHeadPicUrl || item.userHeadPic,
            score: item.score,
            content: item.content,
            images: item.images,
            createTime: item.createTime
          }));
          
          this.commentTotal = data.total || records.length;
        } else {
          this.$message.error(res.msg || '获取评论失败');
        }
      }).catch(err => {
        this.$message.error('请求评论列表出错');
      });
    },
    
    checkFavorite() {
      if (!this.userInfo || !this.userInfo.id) {
        return;
      }
      
      request({
        url: config.backHost + "/api/food/favorite/listPage",
        method: 'POST',
        data: {
          pageBean: {
            page: 1,
            pageSize: 100
          }
        }
      }).then(res => {
        if (res.code === 200) {
          const data = res.data;
          
          // 兼容两种返回格式
          let records = [];
          if (Array.isArray(data)) {
            records = data;
          } else if (data && data.records) {
            records = data.records;
          }
          
          // 检查当前小吃是否在收藏列表中
          const favorites = records.map(item => {
            return item.foodId || item.foodInfoId;
          });
          
          this.isFavorited = favorites.includes(this.foodId);
        }
      }).catch(err => {
        // 静默失败
      });
    },
    
    toggleFavorite() {
      if (!this.userInfo || !this.userInfo.id) {
        this.$message.warning('请先登录');
        this.$router.push({name: 'login'});
        return;
      }
      
      // 判断当前是否已收藏
      const wasFavorited = this.isFavorited;
      
      request({
        url: config.backHost + "/api/food/favorite",
        method: 'POST',
        data: {
          foodId: this.foodId
        }
      }).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg || (wasFavorited ? '取消收藏成功' : '收藏成功'));
          // 切换收藏状态
          this.isFavorited = !wasFavorited;
        } else {
          this.$message.error(res.msg || '操作失败');
        }
      });
    },
    
    submitComment() {
      if (!this.commentForm.content.trim()) {
        this.$message.warning('请输入评价内容');
        return;
      }
      
      this.submitLoading = true;
      
      request({
        url: config.backHost + "/api/food/comment/add",
        method: 'POST',
        data: {
          foodId: this.foodId,
          score: this.commentForm.score,
          content: this.commentForm.content,
          images: this.commentForm.images
        }
      }).then(res => {
        if (res.code === 200) {
          this.$message.success('评价成功');
          // 清空表单
          this.commentForm = {
            score: 5,
            content: '',
            images: ''
          };
          
          // 延迟 500ms 后再加载评论，确保数据已经写入
          setTimeout(() => {
            this.commentPageNum = 1;
            this.getComments();
            // 重新加载小吃详情以更新评分
            this.getFoodDetail();
          }, 500);
        } else {
          this.$message.error(res.msg || '评价失败');
        }
      }).catch(err => {
        this.$message.error('提交评论失败，请稍后重试');
      }).finally(() => {
        this.submitLoading = false;
      });
    },
    
    resetCommentForm() {
      this.commentForm = {
        score: 5,
        content: '',
        images: ''
      };
    },
    
    handleCommentPageChange(page) {
      this.commentPageNum = page;
      this.getComments();
    }
  }
}
</script>

<style scoped lang="scss">
.food-detail-container {
  min-height: 100%;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  padding-bottom: 60px;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

.custom-header {
  margin-bottom: 24px;
  background: transparent;
  padding: 0 !important;
}

// 图片轮播样式
.image-carousel {
  border-radius: 16px;
  overflow: hidden;
  
  .carousel-image,
  .single-image-inner {
    width: 100%;
    height: 100%;
    cursor: pointer;
  }
  
  .single-image {
    width: 100%;
    height: 450px;
    
    .single-image-inner {
      border-radius: 16px;
    }
  }
  
  ::v-deep .el-carousel {
    .el-carousel__container {
      border-radius: 16px;
      overflow: hidden;
    }
    
    .el-carousel__item {
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f5f5;
    }
    
    .el-carousel__arrow {
      background-color: rgba(0, 0, 0, 0.5);
      width: 50px;
      height: 50px;
      font-size: 24px;
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.7);
      }
      
      i {
        font-size: 24px;
      }
    }
    
    .el-carousel__indicators--outside {
      bottom: -30px;
      
      .el-carousel__button {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        background-color: #dcdfe6;
        opacity: 1;
      }
      
      .is-active .el-carousel__button {
        background-color: #ff6b35;
      }
    }
  }
}

// 基本信息头部 - 重新设计
.info-header {
  background: linear-gradient(135deg, #ffffff 0%, #fafbfc 100%);
  padding: 40px;
  border-radius: 16px;
  margin-bottom: 28px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  border: 1px solid rgba(0,0,0,0.04);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 8px 32px rgba(0,0,0,0.12);
    transform: translateY(-2px);
  }
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 5px;
    background: linear-gradient(90deg, #ff6b35 0%, #ffa726 50%, #ffca28 100%);
    border-radius: 16px 16px 0 0;
  }
  
  .header-left {
    flex: 1;
    
    .food-name-row {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
      
      .food-name {
        font-size: 34px;
        font-weight: 700;
        color: #1a2b49;
        margin: 0;
        line-height: 1.2;
        letter-spacing: 0.5px;
      }
      
      ::v-deep .el-tag {
        flex-shrink: 0;
        padding: 6px 14px;
        font-size: 13px;
        font-weight: 600;
        
        i {
          margin-right: 4px;
        }
      }
    }
    
    // 店铺名和地点信息行
    .shop-location-row {
      display: flex;
      align-items: center;
      gap: 24px;
      margin-bottom: 18px;
      padding: 12px 16px;
      background: rgba(255, 107, 53, 0.04);
      border-radius: 10px;
      border-left: 4px solid #ff6b35;
      
      .shop-name-inline,
      .location-inline {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 15px;
        color: #555;
        font-weight: 500;
        
        i {
          font-size: 18px;
          color: #ff6b35;
        }
      }
      
      .shop-name-inline {
        color: #1a2b49;
        font-weight: 600;
        
        i {
          color: #ff8a45;
        }
      }
    }
    
    .rating-box {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      padding: 12px 16px;
      background: rgba(255, 152, 0, 0.05);
      border-radius: 12px;
      border: 1px solid rgba(255, 152, 0, 0.1);
      
      ::v-deep .el-rate {
        font-size: 20px;
      }
      
      .score-text {
        margin-left: 14px;
        font-size: 22px;
        color: #ff6b35;
        font-weight: 700;
      }
      
      .review-count {
        margin-left: 18px;
        font-size: 14px;
        color: #666;
        padding: 4px 12px;
        background: #fff;
        border-radius: 12px;
        border: 1px solid #e8e8e8;
      }
    }
    
    .meta-info {
      display: flex;
      align-items: center;
      gap: 14px;
      flex-wrap: wrap;
      
      .price-tag {
        font-size: 28px;
        color: #e53935;
        font-weight: 700;
        display: flex;
        align-items: baseline;
        gap: 4px;
        
        i {
          font-size: 24px;
        }
        
        small {
          font-size: 15px;
          font-weight: 500;
          color: #999;
        }
      }
      
      ::v-deep .el-tag {
        font-weight: 500;
        padding: 6px 12px;
        
        i {
          margin-right: 4px;
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    gap: 14px;
    margin-left: 32px;
    flex-shrink: 0;
    align-items: center;
    
    .collect-btn {
      display: flex;
      align-items: center;
      gap: 6px;
      cursor: pointer;
      font-size: 15px;
      font-weight: 600;
      color: #1a2b49;
      padding: 10px 20px;
      border-radius: 24px;
      transition: all 0.3s ease;
      user-select: none;
      
      &:hover {
        background: rgba(0, 0, 0, 0.04);
        transform: translateY(-1px);
      }
      
      .heart-icon {
        font-size: 20px;
        transition: all 0.3s ease;
        
        &.active {
          color: #ff4757;
        }
      }
      
      span:last-child {
        white-space: nowrap;
      }
    }
    
    ::v-deep .el-button {
      padding: 12px 28px;
      font-size: 15px;
      font-weight: 600;
      border-radius: 24px;
      transition: all 0.3s ease;
      
      i {
        margin-right: 6px;
        font-size: 16px;
      }
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      }
    }
  }
}

.section-card {
  background: #fff;
  padding: 36px;
  border-radius: 16px;
  margin-bottom: 28px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
  border: 1px solid rgba(0,0,0,0.04);
  
  &:hover {
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    transform: translateY(-2px);
  }
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a2b49;
  margin: 0 0 28px 0;
  padding-bottom: 16px;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, #ff6b35 0%, #ffa726 100%);
    border-radius: 2px;
  }
  
  i {
    margin-right: 12px;
    color: #ff6b35;
    font-size: 26px;
  }
}

.comment-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding-bottom: 18px;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, #ff6b35 0%, #ffa726 100%);
    border-radius: 2px;
  }
  
  .section-title {
    margin: 0;
    padding: 0;
    border: none;
    background: none;
    display: flex;
    align-items: center;
    gap: 10px;
    
    &::after {
      display: none;
    }
    
    .comment-badge {
      display: inline-block;
      min-width: 26px;
      height: 26px;
      line-height: 26px;
      text-align: center;
      background: linear-gradient(135deg, #ff6b35 0%, #ffa726 100%);
      color: #fff;
      font-size: 13px;
      font-weight: 700;
      border-radius: 13px;
      padding: 0 10px;
      box-shadow: 0 3px 8px rgba(255, 107, 53, 0.35);
    }
  }
  
  .write-comment-btn {
    padding: 10px 24px;
    font-size: 14px;
    font-weight: 600;
    border-radius: 20px;
    transition: all 0.3s ease;
    
    i {
      margin-right: 6px;
    }
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(255, 107, 53, 0.35);
    }
  }
}

.intro-section {
  background: linear-gradient(135deg, #ffffff 0%, #fafbfc 100%);
}

.intro-content {
  .content-text {
    padding: 24px;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    border-radius: 12px;
    border-left: 5px solid #ff6b35;
    
    p {
      font-size: 16px;
      line-height: 2;
      color: #333;
      margin: 0;
      text-align: justify;
    }
  }
}

.taste-feature {
  margin-top: 32px;
  
  .sub-title {
    font-size: 18px;
    font-weight: 700;
    color: #1a2b49;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 10px;
    
    i {
      color: #ff6b35;
      font-size: 22px;
    }
  }
  
  .feature-content {
    padding: 20px;
    background: linear-gradient(135deg, #fff8e1 0%, #ffffff 100%);
    border-radius: 12px;
    border-left: 5px solid #ffa726;
    
    p {
      font-size: 15px;
      line-height: 1.9;
      color: #555;
      margin: 0;
      text-align: justify;
    }
  }
}

.recommend-reason {
  margin-top: 32px;
  
  .sub-title.recommend {
    font-size: 18px;
    font-weight: 700;
    color: #1a2b49;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 10px;
    
    i {
      color: #e53935;
      font-size: 22px;
    }
  }
  
  .reason-content {
    padding: 20px;
    background: linear-gradient(135deg, #ffebee 0%, #ffffff 100%);
    border-radius: 12px;
    border-left: 5px solid #ef5350;
    
    p {
      font-size: 15px;
      line-height: 1.9;
      color: #555;
      margin: 0;
      text-align: justify;
    }
  }
}

.shop-section {
  background: linear-gradient(135deg, #fff9f0 0%, #ffffff 100%);
}

.shop-info-box {
  display: flex;
  gap: 30px;
  
  .shop-image-wrapper {
    width: 260px;
    flex-shrink: 0;
    
    .shop-image {
      width: 100%;
      height: 200px;
      border-radius: 14px;
      overflow: hidden;
      box-shadow: 0 6px 20px rgba(0,0,0,0.12);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-6px);
        box-shadow: 0 12px 28px rgba(0,0,0,0.18);
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .shop-image-placeholder {
      width: 100%;
      height: 200px;
      border-radius: 14px;
      background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #999;
      border: 2px dashed #dcdfe6;
      
      i {
        font-size: 52px;
        margin-bottom: 10px;
      }
      
      span {
        font-size: 14px;
      }
    }
  }
  
  .shop-details {
    flex: 1;
    
    .shop-name {
      font-size: 24px;
      font-weight: 700;
      color: #1a2b49;
      margin: 0 0 24px 0;
      padding-bottom: 14px;
      border-bottom: 2px solid #ffe4c4;
      display: flex;
      align-items: center;
      gap: 12px;
      
      i {
        color: #ff6b35;
        font-size: 26px;
      }
    }
    
    .shop-item {
      display: flex;
      align-items: flex-start;
      margin-bottom: 18px;
      padding: 14px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 10px;
      transition: all 0.3s ease;
      border: 1px solid rgba(0,0,0,0.03);
      
      &:hover {
        background: rgba(255, 255, 255, 1);
        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        transform: translateX(6px);
        border-color: rgba(255, 107, 53, 0.1);
      }
      
      .item-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: linear-gradient(135deg, #ff6b35 0%, #ffa726 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 14px;
        flex-shrink: 0;
        box-shadow: 0 3px 10px rgba(255, 107, 53, 0.25);
        
        i {
          font-size: 20px;
          color: #fff;
        }
      }
      
      .item-content {
        flex: 1;
        
        .item-label {
          font-size: 14px;
          color: #888;
          margin-right: 10px;
        }
        
        .item-value {
          font-size: 16px;
          color: #333;
          font-weight: 600;
          
          &.highlight {
            color: #ff6b35;
            font-weight: 700;
          }
          
          &.price {
            font-size: 20px;
            color: #e53935;
            font-weight: 700;
          }
        }
        
        .phone-link {
          text-decoration: none;
          color: inherit;
          
          &:hover .item-value {
            color: #ff6b35;
          }
        }
      }
    }
  }
}

.comment-list {
  .comment-item {
    padding: 26px;
    margin-bottom: 18px;
    border-radius: 14px;
    background: linear-gradient(135deg, #fafbfc 0%, #ffffff 100%);
    border: 1px solid rgba(0,0,0,0.04);
    transition: all 0.3s ease;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    &:hover {
      box-shadow: 0 6px 24px rgba(0,0,0,0.1);
      transform: translateY(-3px);
      border-color: rgba(255, 107, 53, 0.15);
    }
    
    .comment-header {
      display: flex;
      align-items: center;
      margin-bottom: 18px;
      padding-bottom: 14px;
      border-bottom: 1px dashed #e8e8e8;
      
      ::v-deep .el-avatar {
        border: 2px solid #fff;
        box-shadow: 0 3px 10px rgba(0,0,0,0.12);
      }
      
      .comment-user-info {
        margin-left: 16px;
        flex: 1;
        
        .user-name {
          font-size: 16px;
          font-weight: 600;
          color: #1a2b49;
          margin-bottom: 8px;
        }
        
        ::v-deep .el-rate {
          display: inline-block;
        }
      }
      
      .comment-time {
        font-size: 13px;
        color: #999;
        white-space: nowrap;
        margin-left: 14px;
      }
    }
    
    .comment-content {
      font-size: 15px;
      line-height: 1.9;
      color: #333;
      margin-bottom: 18px;
      word-break: break-word;
    }
    
    .comment-images {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      
      .comment-img {
        width: 100px;
        height: 100px;
        border-radius: 10px;
        cursor: pointer;
        border: 2px solid #fff;
        box-shadow: 0 3px 10px rgba(0,0,0,0.12);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.08);
          box-shadow: 0 6px 16px rgba(0,0,0,0.18);
        }
      }
    }
  }
}

.empty-comment {
  padding: 70px 0;
  
  .empty-wrapper {
    text-align: center;
    
    .empty-icon {
      font-size: 88px;
      color: #e0e0e0;
      margin-bottom: 18px;
      display: block;
    }
    
    .empty-text {
      font-size: 16px;
      color: #999;
      margin-bottom: 20px;
    }
  }
}

// 评论输入区样式
.comment-input-area {
  background: #fff;
  border: 1px solid #FDE6D8;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(253, 230, 216, 0.3);
  
  .score-input {
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    gap: 10px;
    
    .label {
      font-size: 14px;
      color: #666;
      font-weight: 600;
    }
  }
  
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

.login-tip {
  text-align: center;
  padding: 30px 0;
  
  .el-button {
    font-size: 16px;
    padding: 12px 40px;
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 24px 16px;
  }
  
  .info-header {
    flex-direction: column;
    padding: 28px;
    
    .header-left {
      .food-name-row {
        .food-name {
          font-size: 28px;
        }
      }
    }
    
    .header-right {
      margin-left: 0;
      margin-top: 24px;
      width: 100%;
      
      ::v-deep .el-button {
        flex: 1;
      }
    }
  }
  
  .section-card {
    padding: 24px;
  }
  
  .shop-info-box {
    flex-direction: column;
    
    .shop-image-wrapper {
      width: 100%;
      
      .shop-image {
        height: 220px;
      }
    }
  }
}
</style>
