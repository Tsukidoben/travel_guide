<template>
  <div class="hotel-page">
    <div class="hero-section">
      <div class="search-container">
        <h1 class="hero-title">发现下一站的理想住所</h1>
        <div class="search-box">
          <el-input
              v-model="searchForm.keyword"
              placeholder="输入酒店名称、酒店介绍..."
              class="hotel-search-input"
              clearable
              @keyup.enter.native="handleSearch"
          >
            <el-button slot="append" @click="handleSearch">搜索酒店</el-button>
          </el-input>
        </div>
      </div>
    </div>
    <div class="filterAll">
      <div class="filter-bar">
        <div class="stat-info">
          <i class="el-icon-office-building"></i>
          <span>共 <b>{{ hotelList.length }}</b> 家酒店</span>
        </div>
      </div>
    </div>
    <div class="container">
      <el-row :gutter="25" style="margin-top: 12px">
        <el-col :span="6" v-for="hotel in hotelList" :key="hotel.id" class="hotel-col">
          <div class="hotel-card" @click="goDetail(hotel.id)">
            <div class="hotel-image">
              <el-image :src="getPicUrlByJson(hotel.htoelPic, 0)" fit="cover" lazy>
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>

            <div class="hotel-info">
              <h3 class="hotel-name one-line">{{ hotel.htoelName }}</h3>
              <div class="hotel-tags" v-if="hotel.hotelService">
                <el-tag
                    v-for="(tag, index) in hotel.hotelService.split(',').slice(0, 2)"
                    :key="index"
                    size="mini"
                    effect="plain"
                    class="list-service-tag"
                >
                  {{ tag.trim() }}
                </el-tag>
              </div>
              <div class="hotel-location two-line">
                {{hotel.hotelDesc}}
              </div>
              <div class="hotel-footer">
                <div class="view-btn">
                  查看详情 <i class="el-icon-arrow-right"></i>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-if="hotelList.length === 0" description="暂无匹配酒店"></el-empty>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";

export default {
  name: "HotelList",
  mixins: [common],
  data() {
    return {
      searchForm: {
        keyword: ""
      },
      hotelList: []
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    async getData() {
      try {
        const res = await request({
          url: config.backHost + "/hotelInfo/listPage",
          data: {
            pageBean: { page: 1, pageSize: -1 },
            params: { keyword: this.searchForm.keyword }
          }
        });
        if (res.code === 200) {
          this.hotelList = res.data;
        }
      } finally {
      }
    },
    handleSearch() {
      this.getData();
    },
    goDetail(id) {
      this.$router.push({ name: 'hotelInfoDetail', query: { id } });
    }
  }
};
</script>

<style scoped lang="scss">
$primary-color: $theme-color;
$light-bg: $side-bar-container-hoverBackColor;
$dark-text: #2c3e50;
.filterAll{
  padding: 20px;position: sticky;top: 180px;z-index: 11;width: 1220px;margin: 0 auto;background: $pageBack;
}
.hotel-page { min-height: 100%; }

.hero-section {
  height: 180px;
  background: url('@/assets/imgs/heroSectionBack.avif');
  background-size: cover;
  background-position: center;
  display: flex; align-items: center; justify-content: center;
  position: sticky;
  top: 0;
  z-index: 11;
  ::v-deep{
    .el-input__suffix{
      line-height: 50px;
    }
  }
  .search-container {
    width: 100%; max-width: 600px; padding: 0 20px;
    .hero-title { color: #fff; font-size: 28px; margin-bottom: 25px; text-align: center; letter-spacing: 1px;margin-top: 0 }

    ::v-deep .el-input__inner {
      height: 50px; border: none;
      box-shadow: 0 4px 20px rgba(0,0,0,0.1);
      border-radius: 12px 0 0 12px;
    }
    ::v-deep .el-input-group__append {
      background: $primary-color; color: #fff; border: none;
      border-radius: 0 12px 12px 0; padding: 0 25px; font-weight: bold;
      cursor: pointer; transition: 0.3s;
      &:hover { background: $side-bar-container-hoverBackColor; }
    }
  }
}

.container { max-width: 1220px; margin: 0px auto 0; padding:0 20px 0px; position: relative; }

.filter-bar {
  background: #fff; padding: 15px 25px; border-radius: 16px;;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  border-bottom: 3px solid $light-bg;
  width: 100%;
  box-sizing: border-box;
  top: 192px;
  z-index: 11;
  .stat-info {
    color: $dark-text; font-size: 15px;
    i { color: $primary-color; margin-right: 8px; font-size: 18px; }
    b { color: $primary-color; font-size: 20px; }
  }

  .sort-tags {
    display: flex; gap: 20px; font-size: 14px; color: #666;
    span { cursor: pointer; padding: 4px 12px; border-radius: 20px; transition: 0.3s; }
    .active-tag { background: $light-bg; color: $primary-color; font-weight: bold; }
    span:hover:not(.active-tag) { color: $primary-color; }
  }
}

.hotel-col { margin-bottom: 30px; }

.hotel-card {
  background: #fff; border-radius: 20px; overflow: hidden; transition: 0.4s cubic-bezier(0.165, 0.84, 0.44, 1); cursor: pointer;
  box-shadow: 0 4px 12px rgba(0,0,0,0.04);
  border: 1px solid #f0f0f0;

  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 20px 40px $side-bar-container-hoverBackColor;
    .view-btn { color: $primary-color !important; }
  }
  .hotel-image {
    height: 190px; position: relative; overflow: hidden;
    .el-image { width: 100%; height: 100%; transition: 0.6s; }

    .hotel-price-tag {
      position: absolute; bottom: 0; left: 0;
      background: rgba($primary-color, 0.95); color: #fff;
      padding: 6px 15px; border-radius: 0 15px 0 0;
      .unit { font-size: 12px; }
      .num { font-size: 22px; font-weight: bold; }
      .label { font-size: 12px; margin-left: 2px; }
    }
  }

  .hotel-info {
    padding: 20px;
    .hotel-name { font-size: 18px; font-weight: bold; color: $dark-text; margin:0 0 10px;text-align: left }
    .hotel-location { color: #7f8c8d; font-size: 13px; margin-bottom: 20px; line-height: 18px;height: 36px;
      i { color: $primary-color; }
    }
    .hotel-footer {
      display: flex; justify-content: right; align-items: center;
      padding-top: 15px; border-top: 1px dashed $light-bg;

      .view-btn {
        font-size: 13px; font-weight: bold; color: #999; transition: 0.3s;
        i { font-weight: bold; }
      }
    }
  }
}
.hotel-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
  overflow: hidden;

  .list-service-tag {
    border-radius: 4px;
    background-color: rgba($primary-color, 0.05);
    border-color: rgba($primary-color, 0.2);
    color: $primary-color;
    font-size: 11px;
    padding: 0 6px;
    height: 22px;
    line-height: 20px;
  }
}
@media (max-width: 1268px) {
  .filterAll{
    width: calc(100% - 40px);
  }
}
@media (max-width: 1132px) {
  .hotel-col{
    width: 33.3%;
  }
}
@media (max-width: 912px) {
  .hotel-col{
    width: 50%;
  }
}
::v-deep .el-rate__icon { margin-right: 2px; font-size: 14px; }
</style>
