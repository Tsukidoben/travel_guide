<template>
  <div class="strategy-detail-page">
    <div class="container">
      <div class="action-bar">
        <el-button icon="el-icon-back" @click="$router.go(-1)" type="text">返回列表</el-button>
        <div class="status-indicator">
          <el-tag :type="getStatusClass(detail.status)" effect="dark">
            {{ getStatusText(detail.status) }}
          </el-tag>
        </div>
      </div>

      <div v-if="detail.status == '-2'" class="review-alert">
        <div class="alert-icon"><i class="el-icon-warning"></i></div>
        <div class="alert-content">
          <h4>审核未通过</h4>
          <p>驳回原因：{{ detail.reviewReason }}</p>
          <span class="review-time">审核时间：{{ detail.reviewTime }}</span>
        </div>
        <el-button type="primary" size="small" @click="goEdit">去修改</el-button>
      </div>

      <div class="main-content">
        <div class="meta-info">
          <div class="author">

          </div>
          <div class="time-view">
            <span><i class="el-icon-time"></i> {{ detail.createTime }}</span>
          </div>
        </div>

        <el-divider></el-divider>

        <div class="image-gallery" v-if="detail.strategyPic">
          <el-image
              :src="getPicUrlByJson(detail.strategyPic,0)"
              :preview-src-list="[getPicUrlByJson(detail.strategyPic,0)]"
              class="content-img">
          </el-image>
        </div>

        <div class="content-text">
          {{ detail.strategyContent }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";

export default {
  mixins: [common],
  data() {
    return {
      detail: {
        strategyTitle: '',
        strategyContent: '',
        strategyPic: '',
        status: '',
        reviewReason: '',
        createName: '',
        createTime: '',
        viewNum: 0
      }
    };
  },
  mounted() {
    this.getDetail();
  },
  methods: {
    getDetail() {
      const id = this.$route.query.id;
      request({
        url: config.backHost + `/tripStrategy/getById/${id}`,
      }).then(res => {
        if (res.code === 200) {
          this.detail = res.data;
        }
      });
    },
    getStatusText(status) {
      const map = { "10": "审核中", "80": "发布成功", "-2": "审核未通过" };
      return map[status] || "未知状态";
    },
    getStatusClass(status) {
      const map = { "10": "warning", "80": "success", "-2": "danger" };
      return map[status] || "info";
    },
    goEdit() {
      this.$router.push({ name: 'postReview', query: { id: this.detail.id } });
    }
  }
};
</script>

<style scoped lang="scss">
.strategy-detail-page {
  padding: 30px 20px;
  background: $pageBack;
  min-height:100%;
  box-sizing: border-box;

  .container {
    max-width: 850px;
    margin: 0 auto;
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .el-button--text {
    color: #666;
    &:hover { color: $theme-color; }
  }
}

.review-alert {
  background: #fff5f2;
  border: 1px solid $theme-color;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(255, 138, 69, 0.1);

  .alert-icon {
    font-size: 32px;
    color: $theme-color;
    margin-right: 15px;
  }

  .alert-content {
    flex: 1;
    h4 { margin: 0 0 5px 0; color: #333; }
    p { margin: 0; color: #666; font-size: 14px; }
    .review-time { font-size: 12px; color: #999; margin-top: 5px; display: block; }
  }
}

.main-content {
  background: #fff;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);

  .strategy-title {
    font-size: 30px;
    color: #2c3e50;
    margin-bottom: 20px;
    line-height: 1.4;
  }

  .meta-info {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .author {
      display: flex;
      align-items: center;
      gap: 10px;
      .name { font-weight: 600; color: #444; }
    }

    .time-view {
      color: #999;
      font-size: 14px;
      span { margin-left: 15px; }
      i { color: $theme-color; margin-right: 4px; }
    }
  }
}

.image-gallery {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;

  .content-img {
    width: 100%;
    border-radius: 12px;
    cursor: zoom-in;
    transition: opacity 0.3s;
    &:hover { opacity: 0.9; }
  }
}

.content-text {
  font-size: 17px;
  line-height: 2;
  color: #444;
  white-space: pre-wrap;
  letter-spacing: 0.5px;
}

@media (max-width: 768px) {
  .main-content { padding: 20px; }
  .strategy-title { font-size: 24px; }
}
</style>
