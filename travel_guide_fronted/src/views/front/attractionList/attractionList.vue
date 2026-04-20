<template>
  <div class="gyg-container">
    <div style="width: 100%;background:#fcf9f7;  position: sticky;top: 0;border-bottom: 1px solid #eee;z-index: 999 ">
      <div class="filter-bar">
        <div class="filters-outer" ref="outerContainer">
          <div class="filters" ref="filterScroll" @scroll="handleScroll">
            <div v-for="(item,index) in classify" :key="index" class="filter-pill" :class="searchParams.typeId == item.id?'active':''" @click="setActive(item)">{{item.typeName}}</div>
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
              <input type="text" v-model="searchParams.keyword" placeholder="关键字搜索">
              <button class="search-btn" @click="search">搜索</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <main class="main-content">
      <div class="results-header">
        <span class="count">{{activities.length>100?activities.length+"+":activities.length}}个结果</span>
      </div>

      <div class="activity-grid" v-if="activities.length>0">
        <div v-for="item in activities" :key="item.id" @click="goDetail(item)" class="activity-card">
          <div class="card-image" :class="'pic-count-' + JSON.parse(item.attractionPic).length">
            <img
                 :src="getPicUrlByJson(item.attractionPic,0)" alt="activity">

<!--            <template v-else-if="JSON.parse(item.attractionPic).length == 2">-->
<!--              <div style="width: 50%;overflow: hidden">-->
<!--                <img :src="getPicUrlByJson(item.attractionPic,0)" class="pic-main">-->
<!--              </div>-->
<!--              <div style="width: 50%;overflow: hidden">-->
<!--                <img :src="getPicUrlByJson(item.attractionPic,1)" class="pic-sub">-->
<!--              </div>-->
<!--            </template>-->

<!--            <template v-else-if="JSON.parse(item.attractionPic).length >= 3">-->
<!--              <div style="width: 66%;overflow: hidden">-->
<!--                <img :src="getPicUrlByJson(item.attractionPic,0)" class="pic-main">-->
<!--              </div>-->
<!--              <div class="side-pics">-->
<!--                <div style="width:100%;height: 50%;overflow: hidden">-->
<!--                  <img :src="getPicUrlByJson(item.attractionPic,1)">-->
<!--                </div>-->
<!--                <div style="width: 100%;height: 50%;overflow: hidden">-->
<!--                  <img :src="getPicUrlByJson(item.attractionPic,2)">-->
<!--                </div>-->
<!--              </div>-->
<!--            </template>-->

            <span class="tag dark">{{ classify.find(items=>items.id == item.typeId)?.typeName }}</span>
            <div class="wishlist-heart" @click.stop="setCollect(item)">
              <span :class="{ 'active': collectList.find(items=>items.attractionId == item.id) }">
                {{ collectList.find(items=>items.attractionId == item.id) ? '❤' : '♡' }}
              </span>
            </div>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ item.attractionName }}</h3>
            <div class="card-info two-line">{{ item.attractionDesc }}</div>
<!--            <div class="card-price">-->
<!--              <div class="price-value">-->
<!--                <span class="new-price">{{ item.attractionPlace }}CNY</span>-->
<!--              </div>-->
<!--            </div>-->
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
  mixins:[common],
  data() {
    return {
      searchParams:{
        "typeId": "",
        "keyword": ""
      },
      showLeftArrow: false,
      showRightArrow: false,
      classify:[{typeName:'全部',id:''}],
      activities: [],
      collectList:[],
    }
  },
  mounted() {
    this.checkOverflow();
    this.getClassify()
    window.addEventListener('resize', this.checkOverflow);
    this.getActivities()
    this.getCollect()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkOverflow);
  },
  methods: {
    goDetail(item){
      this.$router.push({name:'attractionDetail',query:{id:item.id}})
    },
    getCollect(){
      request({
        url: config.backHost + "/attractionCollection/myCollect",
      }).then(res => {
        if (res.code === 200) {
          this.collectList = res.data
        }
      });
    },
    search(){
      this.getActivities()
    },
    setActive(item){
      this.searchParams.typeId = item.id
      this.getActivities()
    },
    setCollect(item){
      let url = ''
      if(this.collectList.find(items=>items.attractionId == item.id)){
        url = '/attractionCollection/noCollect/'+item.id
      } else {
        url = '/attractionCollection/collect/'+item.id
      }
      request({
        url: config.backHost + url,
      }).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg)
          this.getCollect()
        }
      });
    },
    getActivities(){
      request({
        url: config.backHost + "/attractionInfo/listPage",
        data:{
          "pageBean": {
            "page": 1,
            "pageSize": -1,
            "total": 1
          },
          "params": this.searchParams
        }
      }).then(res => {
        if (res.code === 200) {
          this.activities = res.data
        }
      });
    },
    getClassify(){
      request({
        url: config.backHost + "/attractionType/list",
      }).then(res => {
        if (res.code === 200) {
          this.classify = [...this.classify,...res.data]
        }
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
.gyg-container {
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
.search-container{
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
.nav-menu a {
  text-decoration: none;
  color: #2d333f;
  margin-right: 15px;
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
  //border: 1px solid #dcdfe6;
  border-radius: 20px;
  background: white;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  background: rgb(235, 238, 241);
  border-color: rgb(235, 238, 241);
  color: #000;
}
.filter-pill.active {
  background: #1A2B49;
  border-color: #1A2B49;
  color: #fff;
}
.filter-pill:hover{
  border-color: #DCDFE4 !important;
  background: #DCDFE4;
}
.filter-pill.active:hover{
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
}
.activity-card:hover {
  background: #E0F0FF;
  //box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  .card-image img {
    transform: scale(1.1);
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
.tag.dark { background: #1a2b49;line-height: 16px }

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
</style>
