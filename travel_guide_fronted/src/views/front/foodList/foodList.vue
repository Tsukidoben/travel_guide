<template>
  <div class="food-container">
    <!-- 顶部筛选区 -->
    <div style="width: 100%;background:#fcf9f7; position: sticky;top: 0;border-bottom: 1px solid #eee;z-index: 999">
      <div class="filter-bar">
        <div class="filters-outer" ref="outerContainer">
          <div class="filters" ref="filterScroll" @scroll="handleScroll">
            <div v-for="(item,index) in categoryList" :key="index" 
                 class="filter-pill" 
                 :class="searchParams.categoryId == item.id?'active':''" 
                 @click="setCategory(item)">
              {{item.categoryName}}
            </div>
          </div>
          
          <div v-if="showRightArrow" class="scroll-indicator right">
            <div class="arrow-btn" @click="scrollRight">
              <i class="chevron-right"></i>
            </div>
          </div>
          
          <div v-if="showLeftArrow" class="scroll-indicator left">
            <div class="arrow-btn" @click="scrollLeft">
              <i class="chevron-left"></i>
            </div>
          </div>
        </div>
        
        <div class="nav-top">
          <div class="search-container">
            <div class="search-box">
              <i class="el-icon-search"></i>
              <input type="text" v-model="searchParams.name" placeholder="搜索小吃名/店铺名" @keyup.enter="search">
              <button class="search-btn" @click="search">搜索</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <main class="main-content">
      <div class="results-header">
        <span class="count">{{total}}个结果</span>
      </div>

      <div class="food-grid" v-if="foodList.length>0">
        <div v-for="item in foodList" :key="item.id" @click="goDetail(item)" class="food-card">
          <div class="card-image">
            <img :src="getPicUrlByJson(item.foodPic,0)" alt="food">
            <span class="tag dark">{{ getCategoryName(item.categoryId) }}</span>
            <div class="wishlist-heart" @click.stop="toggleFavorite(item)">
              <span :class="{ 'active': isFavorited(item.id) }">
                {{ isFavorited(item.id) ? '❤' : '♡' }}
              </span>
            </div>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ item.foodName }}</h3>
            <div class="card-info">
              <el-rate v-model="item.score" disabled show-score text-color="#ff9900" score-template="{value}"></el-rate>
            </div>
            <div class="card-price">
              <span class="price-label">人均</span>
              <span class="new-price">¥{{ item.avgPrice }}</span>
            </div>
            <div class="shop-name two-line">{{ item.shopName }}</div>
            <div class="food-tags" v-if="item.tags">
              <el-tag v-for="(tag, idx) in item.tags.split(',').slice(0, 3)" :key="idx" size="mini" effect="plain">
                {{ tag.trim() }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else>
        <el-empty description="暂无数据"></el-empty>
      </div>


    </main>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";

export default {
  name: "FoodList",
  mixins: [common],
  computed: {
    userInfo() {
      return this.$store.getters.getUser || {};
    }
  },
  data() {
    return {
      searchParams: {
        name: "",
        categoryId: ""
      },
      categoryList: [{categoryName:'全部', id:''}],
      foodList: [],
      favoriteList: [],
      pageNum: 1,
      pageSize: 100,  // 增大pageSize以显示更多数据
      total: 0,
      showLeftArrow: false,
      showRightArrow: false
    }
  },
  mounted() {
    this.checkOverflow();
    this.getCategories();
    this.getFoodList();
    this.getFavorites();
    window.addEventListener('resize', this.checkOverflow);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkOverflow);
  },
  methods: {
    goDetail(item) {
      this.$router.push({name:'foodDetail', query:{id:item.id}});
    },
    
    getCategories() {
      request({
        url: config.backHost + "/api/food/category/list",
        method: 'POST'
      }).then(res => {
        if (res.code === 200) {
          // 后端返回的字段是 name，前端显示用 categoryName
          this.categoryList = [...this.categoryList, ...(res.data || []).map(item => ({
            id: item.id,
            categoryName: item.name,
            sort: item.sort
          }))];
        }
      });
    },
    
    getFoodList() {
      request({
        url: config.backHost + "/api/food/info/listPage",
        method: 'POST',
        data: {
          params: {
            keyword: this.searchParams.name,
            categoryId: this.searchParams.categoryId,
            status: 1  // 只查询上架的小吃
          },
          pageBean: {
            page: this.pageNum,
            pageSize: this.pageSize
          }
        }
      }).then(res => {
        if (res.code === 200) {
          // 处理不同的返回数据格式
          let dataList = [];
          if (Array.isArray(res.data)) {
            dataList = res.data;
          } else if (res.data && res.data.records) {
            dataList = res.data.records;
          } else if (res.data && res.data.list) {
            dataList = res.data.list;
          }
          
          this.foodList = dataList.map(item => ({
            id: item.id,
            foodName: item.name,
            foodPic: item.images,
            categoryId: item.categoryId,
            categoryName: item.categoryName,
            shopId: item.shopId,
            shopName: item.shopName,
            avgPrice: item.avgPrice,
            score: item.score,
            tags: item.tags,
            status: item.status,
            isRecommend: item.isRecommend
          }));
          
          // 尝试多种方式获取总数
          if (res.pageBean && res.pageBean.total !== undefined && res.pageBean.total > 0) {
            this.total = res.pageBean.total;
          } else if (res.total !== undefined && res.total > 0) {
            this.total = res.total;
          } else if (res.data && res.data.total !== undefined && res.data.total > 0) {
            this.total = res.data.total;
          } else if (dataList.length > 0) {
            // 如果 pageBean.total 为 0，使用 data 数组的长度
            this.total = dataList.length;
          } else {
            // 如果都没有，使用当前列表的长度
            this.total = this.foodList.length;
          }
        }
      }).catch(err => {
        this.$message.error('获取小吃列表失败，请稍后重试');
      });
    },
    
    getFavorites() {
      if (!this.userInfo || !this.userInfo.id) return;
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
          // res.data 直接是收藏记录数组,不是包含 records 的对象
          this.favoriteList = (data || []).map(item => ({
            id: item.id,
            foodId: item.foodId
          }));
        }
      });
    },
    
    search() {
      this.pageNum = 1;
      this.getFoodList();
    },
    
    setCategory(item) {
      this.searchParams.categoryId = item.id;
      this.pageNum = 1;
      this.getFoodList();
    },
    
    toggleFavorite(item) {
      if (!this.userInfo || !this.userInfo.id) {
        this.$message.warning('请先登录');
        this.$router.push({name: 'login'});
        return;
      }
      
      // 判断当前是否已收藏
      const isFav = this.isFavorited(item.id);
      
      request({
        url: config.backHost + "/api/food/favorite",
        method: 'POST',
        data: {
          foodId: item.id
        }
      }).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg || (isFav ? '取消收藏成功' : '收藏成功'));
          this.getFavorites();
        } else {
          this.$message.error(res.msg || '操作失败');
        }
      });
    },
    
    isFavorited(foodId) {
      return this.favoriteList.some(item => item.foodId === foodId);
    },
    
    getCategoryName(categoryId) {
      const category = this.categoryList.find(c => c.id === categoryId);
      return category ? category.categoryName : '';
    },
    
    handlePageChange(page) {
      this.pageNum = page;
      this.getFoodList();
      this.$nextTick(() => {
        document.querySelector('.main-content').scrollTop = 0;
      });
    },
    
    checkOverflow() {
      const el = this.$refs.filterScroll;
      if (el) {
        this.showRightArrow = el.scrollWidth > el.clientWidth &&
            (el.scrollLeft + el.clientWidth < el.scrollWidth - 5);
        this.showLeftArrow = el.scrollLeft > 5;
      }
    },
    
    handleScroll() {
      this.checkOverflow();
    },
    
    scrollRight() {
      this.$refs.filterScroll.scrollBy({ left: 200, behavior: 'smooth' });
    },
    
    scrollLeft() {
      this.$refs.filterScroll.scrollBy({ left: -200, behavior: 'smooth' });
    }
  }
}
</script>

<style scoped lang="scss">
.food-container {
  font-family: "Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  color: #2d333f;
  min-height: 100%;
  box-sizing: border-box;
}

.nav-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 400px;
  margin: 0 0 0 40px;
  flex-shrink: 0;
}

.search-container {
  width: 100%;
}

.search-box {
  display: flex;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 24px;
  padding: 4px 4px 4px 15px;
}

.search-box input {
  border: none;
  outline: none;
  flex: 1;
  padding: 8px;
  font-size: 16px;
}

.search-btn {
  background: $theme-color;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 24px;
  cursor: pointer;
  font-weight: bold;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 40px;
  max-width: 1380px;
  box-sizing: border-box;
  margin: 0 auto;
}

.filters-outer {
  position: relative;
  display: flex;
  align-items: center;
  width: calc(100% - 440px);
}

.filters {
  width: 100%;
  display: flex;
  gap: 8px;
  overflow-x: auto;
  scroll-behavior: smooth;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding-right: 20px;
}

.filters::-webkit-scrollbar {
  display: none;
}

.scroll-indicator {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 60px;
  display: flex;
  align-items: center;
  pointer-events: none;
  z-index: 2;
}

.scroll-indicator.right {
  right: 0;
  background: linear-gradient(to right, rgba(255,255,255,0), rgba(255,255,255,1) 70%);
  justify-content: flex-end;
}

.scroll-indicator.left {
  left: 0;
  background: linear-gradient(to left, rgba(255,255,255,0), rgba(255,255,255,1) 70%);
  justify-content: flex-start;
}

.arrow-btn {
  width: 32px;
  height: 32px;
  background: white;
  border: 1px solid #eee;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: auto;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.chevron-right, .chevron-left {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-top: 2px solid #1a2b49;
  border-right: 2px solid #1a2b49;
}

.chevron-right { transform: rotate(45deg); margin-right: 2px; }
.chevron-left { transform: rotate(-135deg); margin-left: 2px; }

.filter-pill {
  padding: 6px 16px;
  border-radius: 20px;
  background: rgb(235, 238, 241);
  border-color: rgb(235, 238, 241);
  color: #000;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}

.filter-pill.active {
  background: #1A2B49;
  border-color: #1A2B49;
  color: #fff;
}

.filter-pill:hover {
  border-color: #DCDFE4 !important;
  background: #DCDFE4;
}

.filter-pill.active:hover {
  background: #304C84 !important;
  color: #fff;
}

.main-content {
  padding: 20px 40px;
  max-width: 1300px;
  margin: 0 auto;
}

.results-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 14px;
}

.food-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.food-card {
  border: 1px solid #efefef;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: 0.3s;
  background: #fff;
}

.food-card:hover {
  background: #E0F0FF;
  .card-image img {
    transform: scale(1.1);
  }
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform .5s ease;
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

.tag.dark { 
  background: #1a2b49;
  line-height: 16px;
}

.wishlist-heart {
  position: absolute;
  top: 10px;
  right: 10px;
  background: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  font-size: 26px;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 10;

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
}

.card-price {
  text-align: right;
  margin-bottom: 8px;
}

.price-label {
  font-size: 11px;
  color: #636872;
  margin-right: 4px;
}

.new-price {
  color: #d92128;
  font-size: 18px;
  font-weight: bold;
}

.shop-name {
  font-size: 13px;
  color: #636872;
  margin-bottom: 8px;
  line-height: 18px;
  height: 36px;
}

.food-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  
  ::v-deep .el-tag {
    background-color: rgba($theme-color, 0.05);
    border-color: rgba($theme-color, 0.2);
    color: $theme-color;
    font-size: 11px;
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
}

@media (max-width: 1100px) { 
  .food-grid { grid-template-columns: repeat(3, 1fr); } 
}

@media (max-width: 800px) { 
  .food-grid { grid-template-columns: repeat(2, 1fr); } 
}
</style>
