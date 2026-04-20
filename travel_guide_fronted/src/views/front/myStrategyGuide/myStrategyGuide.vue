<template>
  <div class="my-strategy-page">
    <div class="container">
      <div class="page-header">
        <div class="header-left">
          <h2 class="title">我的旅游攻略</h2>
          <p class="desc">在这里管理你分享的所有旅行足迹</p>
        </div>
        <el-button class="btn-primary-theme" icon="el-icon-edit-outline" @click="$router.push({name:'postReview'})" round>
          发布新攻略
        </el-button>
      </div>

      <div class="filter-section">
        <el-tabs v-model="activeStatus" @tab-click="handleFilter">
          <el-tab-pane label="全部" name=""></el-tab-pane>
          <el-tab-pane label="审核通过" name="80"></el-tab-pane>
          <el-tab-pane label="待审核" name="10"></el-tab-pane>
          <el-tab-pane label="被驳回" name="-2"></el-tab-pane>
        </el-tabs>
      </div>

      <div v-if="strategyList.length > 0" class="strategy-list">
        <div v-for="item in strategyList" :key="item.id" class="strategy-card">
          <div class="card-cover" @click="goDetail(item.id)">
            <img :src="getPicUrlByJson(item.strategyPic,0)" alt="cover">
            <span :class="['status-badge', getStatusClass(item.status)]">
              {{ getStatusText(item.status) }}
            </span>
          </div>

          <div class="card-content">
            <div class="text-area" @click="goDetail(item.id)">
<!--              <h3 class="strategy-title">{{ item.strategyTitle || '未命名攻略' }}</h3>-->
              <p class="strategy-intro">{{ item.strategyContent || '这篇攻略还没有写摘要介绍呢...' }}</p>
            </div>

            <div class="card-footer">
              <div class="info">
                <span><i class="el-icon-date"></i> {{ formatDate(item.createTime) }}</span>
                <span class="view-count" v-if="item.status === '80'">
                  <i class="el-icon-view"></i> {{ item.viewNum || 0 }}
                </span>
              </div>
              <div class="btns">
                <el-button type="text" class="edit-text-btn" icon="el-icon-edit" @click="handleEdit(item.id)">编辑</el-button>
                <el-button type="text" icon="el-icon-delete" class="del-btn" @click="handleDelete(item.id)">删除</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-holder">
        <el-empty description="暂无相关攻略，快去开启你的第一场旅行吧！">
          <el-button class="btn-primary-theme" plain @click="$router.push({name:'postReview'})">去写攻略</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";
export default {
  name: "MyStrategy",
  mixins:[common],
  data() {
    return {
      activeStatus: "",
      strategyList: [],
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      request({
        url: config.backHost + "/tripStrategy/myTripStrategies",
        data: {
          pageBean: { page: 1, pageSize: -1 },
          params: {
            strategyContent: "",
            status: this.activeStatus == 0?'':this.activeStatus
          }
        }
      }).then(res => {
        if (res.code === 200) {
          this.strategyList = res.data;
        }
      });
    },

    handleFilter() {
      this.loadData();
    },

    handleEdit(id) {
      this.$router.push({ name: 'postReview', query: { id } });
    },

    handleDelete(id) {
      this.$confirm('确定要永久删除这篇攻略吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        roundButton: true
      }).then(() => {
        request({
          url: config.backHost + `/tripStrategy/delById/${id}`,
        }).then(res => {
          if (res.code === 200) {
            this.$message.success("删除成功");
            this.loadData();
          }
        });
      }).catch(() => {});
    },

    goDetail(id) {
      this.$router.push({ name: 'myStrategyDetail', query: { id } });
    },
    getStatusText(status) {
      const statusMap = { "10": "待审核", "80": "已发布", "-2": "未通过" };
      return statusMap[status] || "未知";
    },
    getStatusClass(status) {
      const classMap = { "10": "warning", "80": "success", "-2": "danger" };
      return classMap[status] || "";
    },
    formatDate(date) {
      return date.split(' ')[0];
    }
  }
};
</script>

<style scoped lang="scss">
.my-strategy-page {
  padding: 30px 40px;
  background: $pageBack;
  min-height: 100%;
  box-sizing: border-box;
}

.container {
  max-width: 1220px;
  margin: 0 auto;
}

.btn-primary-theme {
  background-color: $theme-color !important;
  border-color: $theme-color !important;
  color: #fff !important;
  &:hover {
    background-color: #ff9d66 !important;
    opacity: 0.9;
  }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  .desc { color: #999; margin-top: 8px; font-size: 14px; }
}

.filter-section {
  background: #fff;
  padding: 5px 25px;
  border-radius: 16px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(255, 138, 69, 0.05);

  ::v-deep .el-tabs__nav-wrap::after { display: none; }
  ::v-deep .el-tabs__active-bar {
    background-color: $theme-color;
    height: 3px;
    border-radius: 3px;
  }
  ::v-deep .el-tabs__item {
    font-size: 16px;
    height: 54px;
    line-height: 54px;
    &:hover { color: $theme-color; }
    &.is-active { color: $theme-color; font-weight: bold; }
  }
}

.strategy-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.strategy-card {
  display: flex;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  border: 1px solid rgba(253, 230, 216, 0.5);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 24px rgba(255, 138, 69, 0.12);
    border-color: #FDE6D8;
  }

  .card-cover {
    width: 300px;
    height: 200px;
    position: relative;
    cursor: pointer;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s ease;
    }

    &:hover img { transform: scale(1.1); }

    .status-badge {
      position: absolute;
      top: 15px;
      left: 15px;
      padding: 5px 12px;
      border-radius: 8px;
      font-size: 12px;
      font-weight: 600;
      color: #fff;
      z-index: 2;
      line-height: 16px;
      backdrop-filter: blur(4px);

      &.success { background: rgba(103, 194, 58, 0.9); }
      &.warning { background: rgba(255, 138, 69, 0.9); }
      &.danger { background: rgba(245, 108, 108, 0.9); }
    }
  }

  .card-content {
    width: calc(100% - 300px);
    padding: 25px 25px 0 25px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    box-sizing: border-box;
    .text-area {
      cursor: pointer;
      .strategy-title {
        font-size: 22px;
        color: #2c3e50;
        margin: 0 0 12px 0;
        transition: color 0.3s;
        &:hover { color: $theme-color; }
      }
      .strategy-intro {
        color: #606266;
        font-size: 15px;
        line-height: 1.8;
        display: -webkit-box;
        -webkit-line-clamp: 4;
        -webkit-box-orient: vertical;
        word-break: break-all;
        white-space: normal;
        overflow: hidden;
        width: 100%;
      }
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-top: 1px solid #fdf4ee;

      .info {
        font-size: 14px;
        color: #909399;
        i { color: $theme-color; margin-right: 4px; }
        span { margin-right: 20px; }
      }

      .btns {
        .edit-text-btn {
          color: $theme-color;
          font-weight: 500;
          &:hover { color: #ffab7a; }
        }
        .del-btn {
          color: #f56c6c;
          margin-left: 15px;
          &:hover { color: #f78989; }
        }
      }
    }
  }
}

.empty-holder {
  background: #fff;
  padding: 80px 0;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.02);
  ::v-deep .el-button--primary.is-plain {
    color: $theme-color;
    background: #FDE6D8;
    border-color: #FDE6D8;
    &:hover {
      background: $theme-color;
      color: #fff;
    }
  }
}

@media (max-width: 992px) {
  .strategy-card {
    .card-cover { width: 220px; }
  }
}

@media (max-width: 768px) {
  .my-strategy-page { padding: 15px; }
  .strategy-card {
    flex-direction: column;
    .card-cover { width: 100%; height: 200px; }
  }
}
</style>
