<template>
  <div class="my-favorites-container">
    <div class="favorite-header">
      <h1 class="title">我的收藏</h1>
      <p class="subtitle">管理您心仪的景点、小吃和目的地</p>
    </div>

    <!-- 标签切换 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="collect-tabs">
      <el-tab-pane label="景点收藏" name="attraction">
        <div class="activity-grid" v-if="attractionList.length>0">
          <div v-for="item in attractionList" :key="item.id" @click="goAttractionDetail(item.attractionId)" class="activity-card">
            <div class="wishlist-heart" @click.stop="handleCancelAttractionCollect(item.id)">
              <i class="el-icon-delete"></i>
            </div>
            <div class="card-image" :class="'pic-count-' + JSON.parse(item.attractionPic).length">
              <img
                  :src="getPicUrlByJson(item.attractionPic,0)" alt="activity">
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.attractionName }}</h3>
              <div class="card-info two-line">{{ item.attractionDesc }}</div>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <div class="empty-icon">📂</div>
          <h3>您的景点收藏夹空空如也</h3>
          <p>去发现一些有趣的景点并收藏它们吧！</p>
          <el-button type="primary" round @click="$router.push('/front/attractionList')">
            浏览景点
          </el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane label="小吃收藏" name="food">
        <div class="activity-grid" v-if="foodList.length>0">
          <div v-for="item in foodList" :key="item.id" @click="goFoodDetail(item.foodId)" class="activity-card">
            <div class="wishlist-heart" @click.stop="handleCancelFoodCollect(item.id)">
              <i class="el-icon-delete"></i>
            </div>
            <div class="card-image">
              <img :src="getPicUrlByJson(item.foodPic,0)" alt="food">
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.foodName }}</h3>
              <div class="card-info two-line">{{ item.shopName }}</div>
              <div class="card-price">
                <span class="price-label">人均</span>
                <span class="new-price">¥{{ item.avgPrice }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <div class="empty-icon">🍜</div>
          <h3>您的小吃收藏夹空空如也</h3>
          <p>去发现一些美味的小吃并收藏它们吧！</p>
          <el-button type="primary" round @click="$router.push('/front/foodList')">
            浏览小吃
          </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";

export default {
  mixins: [common],
  computed: {
    userInfo() {
      return this.$store.getters.getUser || {};
    }
  },
  data() {
    return {
      activeTab: 'attraction',
      attractionList: [],
      foodList: []
    };
  },
  mounted() {
    this.getAttractionCollect();
    this.getFoodCollect();
  },
  methods: {
    handleTabClick(tab) {
      // 切换标签时的逻辑
    },
    
    getAttractionCollect() {
      request({
        url: config.backHost + "/attractionCollection/myCollect",
      }).then(res => {
        if (res.code === 200) {
          this.attractionList = res.data;
        }
      });
    },
    
    getFoodCollect() {
      if (!this.userInfo || !this.userInfo.id) {
        console.log('用户未登录，无法获取收藏列表');
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
          
          // 兼容两种返回格式：
          // 1. 分页格式：{ records: [...], total: X }
          // 2. 直接数组格式：[{...}, {...}]
          let records = [];
          if (Array.isArray(data)) {
            // 如果直接是数组
            records = data;
          } else if (data && data.records) {
            // 如果是分页对象
            records = data.records;
          }
          
          // 先构建基础列表
          this.foodList = records.map(item => ({
            id: item.id,
            foodId: item.foodId,
            foodName: item.foodName,
            foodPic: item.foodImages || item.foodPic,
            shopName: item.shopName,
            avgPrice: item.avgPrice || item.price || 0
          }));
          
          // 为每个小吃获取详情以补充价格信息
          this.fetchFoodDetails();
        }
      }).catch(err => {
        this.$message.error('获取小吃收藏失败');
      });
    },
    
    // 获取小吃详情以补充价格信息
    fetchFoodDetails() {
      if (this.foodList.length === 0) return;
      
      // 并发请求所有小吃的详情
      const promises = this.foodList.map(item => {
        return request({
          url: config.backHost + `/api/food/info/detail/${item.foodId}`,
          method: 'POST'
        }).then(res => {
          if (res.code === 200 && res.data) {
            return {
              foodId: item.foodId,
              avgPrice: res.data.avgPrice || 0
            };
          }
          return { foodId: item.foodId, avgPrice: 0 };
        }).catch(() => {
          return { foodId: item.foodId, avgPrice: 0 };
        });
      });
      
      Promise.all(promises).then(results => {
        // 更新价格信息
        results.forEach(result => {
          const food = this.foodList.find(f => f.foodId === result.foodId);
          if (food) {
            food.avgPrice = result.avgPrice;
          }
        });
      });
    },
    
    handleCancelAttractionCollect(attractionId) {
      this.$confirm('确定要将此景点从收藏中移除吗？', '取消收藏', {
        confirmButtonText: '确定移除',
        cancelButtonText: '再想想',
        type: 'warning',
        roundButton: true
      }).then(() => {
        request({
          url: config.backHost + `/attractionCollection/delById/${attractionId}`,
        }).then(res => {
          if (res.code === 200) {
            this.$message.success("已移除收藏");
            this.getAttractionCollect();
          }
        });
      }).catch(() => {});
    },
    
    handleCancelFoodCollect(favoriteId) {
      this.$confirm('确定要将此小吃从收藏中移除吗？', '取消收藏', {
        confirmButtonText: '确定移除',
        cancelButtonText: '再想想',
        type: 'warning',
        roundButton: true
      }).then(() => {
        // 先获取该收藏对应的 foodId
        const favorite = this.foodList.find(item => item.id === favoriteId);
        if (!favorite) return;
        
        request({
          url: config.backHost + "/api/food/favorite",
          method: 'POST',
          data: {
            foodId: favorite.foodId
          }
        }).then(res => {
          if (res.code === 200) {
            this.$message.success("已移除收藏");
            this.getFoodCollect();
          }
        });
      }).catch(() => {});
    },
    
    goAttractionDetail(id) {
      this.$router.push({name:'attractionDetail',query:{id:id}});
    },
    
    goFoodDetail(foodId) {
      this.$router.push({name:'foodDetail', query:{id:foodId}});
    }
  }
};
</script>

<style scoped lang="scss">
.my-favorites-container {
  padding: 20px 40px;
  max-width: 1300px;
  min-height: 100%;
  box-sizing: border-box;
  margin: 0 auto;
  .favorite-header {
    margin-bottom: 30px;
    .title {
      font-size: 28px;
      color: #333;
      font-weight: 600;
      position: relative;
      padding-left: 15px;
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: $theme-color;
        border-radius: 2px;
      }
    }
    .subtitle { color: #636872; margin-top: 8px; }
  }
  
  ::v-deep .collect-tabs {
    .el-tabs__header {
      margin-bottom: 30px;
    }
    .el-tabs__item {
      font-size: 16px;
      font-weight: 500;
    }
  }
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.activity-card {
  border: 1px solid #efefef;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: 0.3s;
  position: relative;
}
.activity-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  .card-image img {
    transform: scale(1.1);
  }
  .wishlist-heart{
    display: flex;
  }
}
.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  display: flex;
  gap: 4px;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform .5s ease;
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
  &:hover {
    img {
      transform: scale(1.1);
    }
  }
}
.tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
  font-weight: bold;
}
.tag.dark { background: #1a2b49; }

.wishlist-heart {
  position: absolute;
  top: 10px;
  right: 10px;
  background: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  font-size: 22px;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 10;
  color: red;
  display: none;
  span {
    transition: all 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    color: #1a2b49;

    &.active {
      color: #d92128;
      font-size: 20px;
    }
  }

  &:hover {
    background: #f5f5f5;
    transform: scale(1.05);
  }

  &:active {
    transform: scale(0.9);
  }
}
.card-body {
  padding: 12px;
}
.card-title {
  font-size: 18px;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 20px;
  line-height: 20px;
  color: #1a2b49;
}
.card-info {
  font-size: 14px;
  color: #1a2b49;
  margin-bottom: 8px;
  line-height: 16px;
  height: 32px;
}

.card-price {
  text-align: right;
}

.price-label {
  font-size: 11px;
  color: #636872;
}

.new-price {
  color: #d92128;
  font-size: 18px;
  font-weight: bold;
}

@media (max-width: 1100px) { .activity-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 800px) { .activity-grid { grid-template-columns: repeat(2, 1fr); } }

.empty-container {
  text-align: center; padding: 100px 0;
  .empty-icon { font-size: 64px; margin-bottom: 20px; }
  h3 { font-size: 22px; color: #303133; margin-bottom: 10px; }
  p { color: #909399; margin-bottom: 25px; }
}
</style>
