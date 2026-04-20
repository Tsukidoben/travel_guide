<template>
  <div class="strategy-plaza">
    <div class="banner">
      <div class="banner-content">
        <h1 class="bounce-in">探索旅行灵感</h1>
        <div class="search-wrapper">
          <el-input
              placeholder="搜索攻略内容"
              v-model="queryParams.params.strategyContent"
              @keyup.enter.native="handleSearch"
              clearable>
            <el-button slot="append" @click="handleSearch"> 搜索</el-button>
          </el-input>
        </div>
      </div>
    </div>

    <div class="container">
      <div v-if="list.length > 0" class="forum-list">
        <div v-for="item in list" :key="item.id" class="forum-item" @click="goDetail(item.id)">
          <div class="post-container">
            <el-tag type="success" style="position: absolute;right: 0">
              <div class="attraction-link">
                <i class="el-icon-location"></i>
                {{ item.attractionName }}
              </div>
            </el-tag>
            <div class="post-header">
              <div class="user-avatar">
                <el-avatar :src="getPicUrlByJson(item.createHead, 0)" icon="el-icon-user-solid" :size="48" class="avatar-shadow"></el-avatar>
              </div>
              <span class="username">{{ item.createName }}</span>
            </div>

            <div class="post-excerpt">
              {{ item.strategyContent }}
            </div>

            <div v-if="item.strategyPic" class="post-images">
              <el-image
                  v-for="(img, idx) in JSON.parse(item.strategyPic).slice(0, 3)"
                  :key="idx"
                  :src="getPicUrlByJson(item.strategyPic, idx)"
                  fit="cover"
                  class="preview-img">
              </el-image>
            </div>

            <div class="post-footer">
              <div class="actions">
              </div>
              <div class="post-time">
                {{ item.createTime }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="还没有相关攻略哦" :image-size="200"></el-empty>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";

export default {
  name: "StrategyPlaza",
  mixins: [common],
  data() {
    return {
      list: [],
      queryParams: {
        pageBean: {
          page: 1,
          pageSize: -1,
          total: 0
        },
        params: {
          strategyContent: "",
        }
      }
    };
  },
  mounted() {
    this.loadList();
  },
  methods: {
    loadList() {
      request({
        url: config.backHost + "/tripStrategy/list",
        data: this.queryParams
      }).then(res => {
        if (res.code === 200) {
          this.list = res.data;
        }
      });
    },
    handleSearch() {
      this.loadList();
    },
    goDetail(id) {
      this.$router.push({ name: 'strategyPlazaDetail', query: { id } });
    },
  }
};
</script>

<style scoped lang="scss">
.forum-list{
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}
.strategy-plaza {
  min-height: 100%;
}

.banner {
  height: 180px;
  background: linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)),
  url('@/assets/imgs/strategyPlazaBack.jfif');
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #fff;
  position: sticky;
  top: 0;
  z-index: 11;
  .banner-content {
    p { font-size: 18px; margin-bottom: 18px; opacity: 0.8; }
  }
}
.bounce-in { color: #fff; font-size: 28px; margin-bottom: 25px; text-align: center; letter-spacing: 1px;margin-top: 0 }
.search-wrapper {
  width: 550px;
  margin: 0 auto;
  ::v-deep .el-input__inner {
    border-radius: 12px 0 0 12px;
    height: 50px;
    font-size: 16px;
    border: none;
    padding-left: 25px;
  }
  ::v-deep{
    .el-input__suffix{
      line-height: 50px;
    }
  }
  ::v-deep .el-input-group__append {
    background: #FF8A45;
    border: none;
    border-radius: 0 12px 12px 0;
    color: #fff;
    width: 80px;
    .el-button { font-size: 16px; font-weight: bold; }
  }
}

.container {
  max-width: 1220px;
  margin: 0 auto;
  padding: 40px 20px;
}

.strategy-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
}

.strategy-item {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  transition: 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  cursor: pointer;
  border: 1px solid #f1ece8;

  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 15px 30px rgba(255, 138, 69, 0.15);
    border-color: #FDE6D8;
  }

  .item-cover {
    height: 190px;
    position: relative;
    overflow: hidden;
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s;
    }
    .view-badge {
      position: absolute;
      top: 12px;
      right: 12px;
      background: rgba(0,0,0,0.5);
      color: #fff;
      padding: 4px 10px;
      border-radius: 20px;
      font-size: 12px;
      backdrop-filter: blur(4px);
    }
  }

  &:hover .item-cover img {
    transform: scale(1.1);
  }

  .item-body {
    padding: 18px;
    .item-title {
      font-size: 18px;
      color: #333;
      margin: 0 0 10px 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      &:hover { color: #FF8A45; }
    }
    .item-desc {
      font-size: 14px;
      color: #777;
      line-height: 1.6;
      height: 44px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      margin-bottom: 15px;
      margin-top: 0;
    }
    .item-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 12px;
      color: #bbb;
      .user {
        color: #FF8A45;
        font-weight: 500;
        i { margin-right: 4px; }
      }
    }
  }
}

.forum-item {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  display: flex;
  padding: 25px;
  border-bottom: 1px solid #f2f2f2;
  transition: background 0.3s;
  cursor: pointer;
  width: calc(50% - 10px);
  flex-shrink: 0;
  box-sizing: border-box;
  &:last-child { border-bottom: none; }
  &:hover { background: #fafafa; }

  .user-avatar {
    flex-shrink: 0;
  }

  .post-container {
    flex: 1;
    overflow: hidden;
    position: relative;
    .post-header {
      margin-bottom: 8px;
      display: flex;
      align-items: center;
      gap: 8px;
      .username {
        font-size: 16px;
        color: #333;
        font-weight: 500;
      }
    }

    .post-title-row {
      display: flex;
      align-items: center;
      margin-bottom: 12px;

      .recommend-tag {
        background-color: #f0f9eb;
        border: 1px solid #e1f3d8;
        color: #67c23a;
        font-size: 12px;
        padding: 2px 6px;
        border-radius: 4px;
        margin-right: 10px;
      }

      .post-title {
        margin: 0;
        font-size: 18px;
        color: #1a1a1a;
        font-weight: bold;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .post-excerpt {
      font-size: 15px;
      color: #4a4a4a;
      line-height: 1.6;
      margin-bottom: 15px;
      display: -webkit-box;
      -webkit-line-clamp: 2; /* 最多显示2行 */
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .post-images {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
      .preview-img {
        width: 80px;
        height: 80px;
        border-radius: 4px;
        border: 1px solid #f0f0f0;
      }
    }

    .post-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: #999;
      font-size: 14px;

      .actions {
        display: flex;
        gap: 20px;
        span {
          display: flex;
          align-items: center;
          gap: 5px;
          &:hover { color: #FF8A45; }
          i { font-size: 16px; }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .forum-item { padding: 15px; }
  .user-avatar { margin-right: 10px; }
  .post-images .preview-img { width: 80px; height: 80px; }
}
@media (max-width: 1200px) {
  .strategy-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .strategy-grid { grid-template-columns: repeat(1, 1fr); }
  .search-wrapper { width: 90%; }
}
</style>
