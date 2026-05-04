<template>
  <div class="home-page">
    <div class="banner-wrapper">
      <el-carousel height="calc(70vh)" :interval="5000" arrow="always">
        <el-carousel-item v-for="(item, index) in bannerList" :key="index">
          <div class="banner-box">
            <img :src="getPicUrlByJson(item.imgUrl,0)" class="banner-img" alt="轮播图" />
            <div class="banner-content">
              <h2 class="animate__animated animate__fadeInUp one-line" style="text-align: left">{{ item.imgTitle}} </h2>
              <p class="animate__animated animate__fadeInUp">每一次旅行，都是一次心灵的洗礼</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="main-container">
      <el-row :gutter="30">
        <el-col :span="18">
          <div class="card-panel section-panel">
            <div class="panel-header">
              <div class="header-left">
                <i class="el-icon-location-information"></i>
                <span class="title-text">热门景点推荐</span>
              </div>
              <el-button type="text" class="more-btn" @click="$router.push({name:'attractionList'})">更多景点 <i class="el-icon-arrow-right"></i></el-button>
            </div>
            <el-row :gutter="20">
              <el-col :span="4.8" v-for="(spot, index) in spotList" :key="index"  class="custom-col-5">
                <el-card class="travel-card" :body-style="{ padding: '0px' }" shadow="hover" @click.native="$router.push({name:'attractionDetail',query:{id:spot.id}})"  >
                  <div class="image-wrapper">
                    <img :src="getPicUrlByJson(spot.attractionPic,0)" class="card-image">
<!--                    <div class="price-tag">￥{{ spot.price }}起</div>-->
                  </div>
                  <div class="content">
                    <h3 class="name one-line">{{ spot.attractionName }}</h3>
                    <p class="desc two-line">{{ spot.attractionDesc }}</p>
<!--                    <div class="footer">-->
<!--                      <el-button type="primary"  size="mini" plain>查看详情</el-button>-->
<!--                    </div>-->
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <!-- 热门小吃推荐模块 -->
          <div class="card-panel section-panel">
            <div class="panel-header">
              <div class="header-left">
                <i class="el-icon-food"></i>
                <span class="title-text">热门小吃推荐</span>
              </div>
              <el-button type="text" class="more-btn" @click="$router.push({name:'foodList'})">更多小吃 <i class="el-icon-arrow-right"></i></el-button>
            </div>
            <el-row :gutter="20">
              <el-col :span="4.8" v-for="(food, index) in foodList" :key="index" class="custom-col-5">
                <el-card class="travel-card food-card" :body-style="{ padding: '0px' }" shadow="hover" @click.native="$router.push({name:'foodDetail',query:{id:food.id}})">
                  <div class="image-wrapper">
                    <img :src="getPicUrlByJson(food.foodPic,0)" class="card-image">
                    <div class="recommend-badge" v-if="food.tags">{{ getFirstTag(food.tags) }}</div>
                  </div>
                  <div class="content">
                    <h3 class="name one-line">{{ food.foodName }}</h3>
                    <p class="desc two-line">{{ food.shopName || '特色小吃' }}</p>
                    <div class="footer">
                      <span class="price-text">¥{{ food.avgPrice }}</span>
                      <span class="score"><i class="el-icon-star-on"></i> {{ food.score }}</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <div class="card-panel section-panel">
            <div class="panel-header">
              <div class="header-left">
                <i class="el-icon-office-building"></i>
                <span class="title-text">精选品质酒店</span>
              </div>
              <el-button type="text" class="more-btn" @click="$router.push({name:'hotelInfoFront'})">更多酒店 <i class="el-icon-arrow-right"></i></el-button>
            </div>
            <el-row :gutter="20">
              <el-col :span="4.8" v-for="(hotel, index) in hotelList" :key="index" class="custom-col-5">
                <el-card class="travel-card hotel-card" :body-style="{ padding: '0px' }" shadow="hover" @click.native="$router.push({name:'hotelInfoDetail',query:{id:hotel.id}})">
                  <div class="image-wrapper">
                    <img :src="getPicUrlByJson(hotel.htoelPic,0)" class="card-image">
                  </div>
                  <div class="content">
                    <h3 class="name one-line">{{ hotel.htoelName }}</h3>
                    <p class="desc two-line">{{ hotel.hotelDesc }}</p>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-col>

        <el-col :span="6">
          <div class="card-panel guide-panel">
            <div class="panel-header">
              <div class="header-left">
                <i class="el-icon-notebook-2"></i>
                <span class="title-text">热门旅游攻略</span>
              </div>
            </div>
            <div class="guide-body">
              <div v-for="(guide, index) in guideList" :key="index" class="guide-item" @click="$router.push({name:'strategyPlazaDetail',query:{id:guide.id}})">
                <div class="guide-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
                <div class="guide-info">
                  <div class="guide-title one-line">{{ guide.strategyContent }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import common from "@/utils/common";
import request from "@/utils/request";
import config from "@/config/config";
export default {
  name: "FrontIndex",
  mixins:[common],
  data() {
    return {
      bannerList: [],
      guideList: [],
      spotList: [],
      hotelList: [],
      foodList: []
    };
  },
  mounted() {
    this.getImgUrl()
    this.getTripStrategy()
    this.getAttraction()
    this.geThotelInfo()
    this.getRecommendFood()
  },
  methods:{
    geThotelInfo(){
      request({
        url: config.backHost + "/hotelInfo/recommend"
      }).then(res => {
        if (res.code == 200) {
          this.hotelList = res.data
        }
      })
    },
    getRecommendFood(){
      request({
        url: config.backHost + "/api/food/info/listPage",
        method: 'POST',
        data: {
          params: {
            isRecommend: 1,
            status: 1
          },
          pageBean: {
            page: 1,
            pageSize: 6
          }
        }
      }).then(res => {
        if (res.code == 200) {
          // 处理不同的返回数据格式
          let dataList = [];
          if (Array.isArray(res.data)) {
            dataList = res.data;
          } else if (res.data && res.data.records) {
            dataList = res.data.records;
          } else if (res.data && res.data.list) {
            dataList = res.data.list;
          }
          
          // 取前6个推荐小吃
          this.foodList = dataList.slice(0, 6).map(item => ({
            id: item.id,
            foodName: item.name,
            foodPic: item.images,
            shopName: item.shopName,
            avgPrice: item.avgPrice,
            score: item.score,
            tags: item.tags
          }));
        }
      })
    },
    getFirstTag(tags) {
      if (!tags) return '';
      const tagArray = tags.split(',').filter(t => t.trim());
      return tagArray.length > 0 ? tagArray[0].trim() : '';
    },
    getAttraction(){
      request({
        url: config.backHost + "/attractionInfo/recommend",
        method: 'POST',
        data: {
          params: {
            sortBy: 'viewCount',  // 按浏览量排序
            sortOrder: 'desc'     // 降序排列
          },
          pageBean: {
            page: 1,
            pageSize: 10  // 固定数量
          }
        }
      }).then(res => {
        if (res.code == 200) {
          this.spotList = res.data
        }
      })
    },
    getTripStrategy() {
      request({
        url: config.backHost + "/tripStrategy/recommend"
      }).then(res => {
        if (res.code == 200) {
          this.guideList = res.data
        }
      })
    },
    getImgUrl() {
      request({
        url: config.backHost + "/carouselImage/list"
      }).then(res => {
        if (res.code == 200) {
          if(res.data.length>0){
            res.data.forEach(item=>{
              this.bannerList.push(item)
            })
          }
        } else {
          this.$message.error('轮播图获取失败')
        }
      })
    },
  },
};
</script>

<style lang="scss" scoped>
$primary-color: $theme-color;
$bg-light: $side-bar-container-hoverBackColor;
$text-main: #333;
$text-muted: #999;
.home-page {
  background-color: #f9f9f9;
  min-height: 100vh;
  padding-bottom: 50px;
}

.banner-wrapper {
  .banner-box {
    position: relative;
    height: 100%;
    .banner-img { width: 100%; height: 100%; object-fit: cover; }
    &::after {
      content: '';
      position: absolute;
      top: 0; left: 0; width: 100%; height: 100%;
      background: linear-gradient(to bottom, transparent, rgba(0,0,0,0.5));
    }
    .banner-content {
      position: absolute;
      bottom: 20%; left: 10%; z-index: 10; color: white;
      width: 80vw;
      h2 { font-size: 48px; margin-bottom: 10px; }
      p { font-size: 20px; opacity: 0.9; }
    }
  }
}

.main-container {
  max-width: 1300px;
  margin: -90px auto 0;
  position: relative;
  z-index: 20;
  padding: 0 20px;
}

.card-panel {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 25px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    .header-left {
      display: flex;
      align-items: center;
      i { font-size: 24px; color: $primary-color; margin-right: 10px; }
      .title-text { font-size: 20px; font-weight: bold; color: $text-main; }
    }
    .more-btn { color: $primary-color; }
  }
}

.guide-body {
  .guide-item {
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px dashed #eee;
    cursor: pointer;
    width: 100%;
    &:hover .guide-title { color: $primary-color; }

    .guide-rank {
      width: 24px; height: 24px; line-height: 24px; text-align: center;
      background: #f0f0f0; border-radius: 4px; margin-right: 12px;
      font-weight: bold; font-size: 14px;
      flex-shrink: 0;
      &.rank-1 { background: $primary-color; color: white; }
      &.rank-2 { background: #FFA36C; color: white; }
      &.rank-3 { background: #FFC09F; color: white; }
    }
    .guide-info {
      flex: 1;
      width: calc(100% - 36px);
      .guide-title { font-size: 14px; color: $text-main; transition: 0.3s;text-align: left }
      .guide-meta {
        font-size: 12px; color: $text-muted;
        span { margin-right: 15px; i { margin-right: 3px; } }
      }
    }
  }
}

.travel-card {
  border: none;
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.3s;
  margin-bottom: 10px;
  &:hover { transform: translateY(-5px); }

  .image-wrapper {
    position: relative;
    height: 150px;
    .card-image { width: 100%; height: 100%; object-fit: cover; }
    .price-tag {
      position: absolute; bottom: 0; right: 0;
      background: rgba(0,0,0,0.6); color: white;
      padding: 4px 10px; font-size: 12px; border-top-left-radius: 8px;
    }
    .status-badge {
      position: absolute; top: 10px; left: 10px;
      background: $primary-color; color: white;
      padding: 2px 8px; border-radius: 4px; font-size: 11px;
      line-height: 16px;
    }
    .recommend-badge {
      position: absolute; top: 10px; right: 10px;
      background: linear-gradient(135deg, #ff6b35 0%, #ffa726 100%);
      color: white;
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 11px;
      font-weight: 600;
      line-height: 16px;
      box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
      white-space: nowrap;
    }
  }

  .content {
    padding: 15px;
    .name { font-size: 16px; margin:0 0 8px;text-align: left }
    .desc { font-size: 12px; color: $text-muted; height: 32px; line-height: 1.4; margin: 0px; margin-bottom: 8px}
    .tags { margin-bottom: 10px; .el-tag { margin-right: 5px; background: $bg-light; border-color: transparent; color: $primary-color; } }
    .footer {
      display: flex; justify-content: left; align-items: center;
      .score { color: #F7BA2A; font-weight: bold; }
      .price-text { color: $primary-color; font-size: 18px; font-weight: bold; }
      .buy-icon { font-size: 20px; color: $primary-color; cursor: pointer; }
    }
  }
}

.custom-col-5 {
  width: 20% !important;
  cursor: pointer;
}

::v-deep {
  .el-button--primary {
    background-color: $primary-color !important;
    border-color: $primary-color !important;
    &.is-plain {
      background-color: $bg-light !important;
      color: $primary-color !important;
    }
  }
  .el-carousel__indicator.is-active .el-carousel__button {
    background-color: $primary-color;
  }
}
</style>
