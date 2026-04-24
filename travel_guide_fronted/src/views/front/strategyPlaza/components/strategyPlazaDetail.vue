<template>
  <div class="detail-page">
    <div class="container">
      <div class="nav-bar">
        <el-page-header @back="$router.go(-1)" content="攻略详情"></el-page-header>
      </div>

      <div class="main-content">
        <div class="article-box">
          <div class="author-bar">
            <div class="left">
              <el-avatar :src="getPicUrlByJson(detail.createHead, 0)" icon="el-icon-user-solid" :size="50" class="avatar-shadow"></el-avatar>
              <div class="info">
                <div class="name">{{ detail.createName }}</div>
                <div class="time">{{ detail.createTime }}</div>
              </div>
            </div>
          </div>
          <div class="attraction-link">
            <i class="el-icon-location"></i>
            {{ detail.attractionName }}
          </div>
          <div class="article-content">{{ detail.strategyContent }}</div>

<!--          <div class="article-imgs" v-if="detail.strategyPic">-->
<!--            <el-image-->
<!--                :src="getPicUrlByJson(detail.strategyPic, 0)"-->
<!--                :preview-src-list="[getPicUrlByJson(detail.strategyPic, 0)]"-->
<!--                class="content-img">-->
<!--            </el-image>-->
<!--          </div>-->
          <div v-if="detail.strategyPic" class="post-images">
            <el-image
                v-for="(img, idx) in JSON.parse(detail.strategyPic).slice(0, 3)"
                :key="idx"
                :src="getPicUrlByJson(detail.strategyPic, idx)"
                :preview-src-list="[getPicUrlByJson(detail.strategyPic, idx)]"
                fit="cover"
                class="preview-img">
            </el-image>
          </div>
          <div class="interaction-bar">
            <div class="stat-item"><i class="el-icon-chat-line-round"></i> {{ comments.length }} 评论</div>
          </div>
        </div>

        <div class="comment-section">
          <div class="comment-header">
            <span class="title">互动交流</span>
            <div class="line"></div>
          </div>

          <div class="input-card">
            <el-input
                type="textarea" :rows="3"
                :placeholder="replyId ? `回复 @${replyName}` : '既然来了，就留下你的足迹吧...'"
                v-model="commentText"
                resize="none">
            </el-input>
            <div class="input-footer">
              <el-button v-if="replyId" type="text" size="small" @click="cancelReply">取消回复</el-button>
              <el-button class="submit-btn" @click="submitComment">发表评论</el-button>
            </div>
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
                  <div class="c-actions">
                    <span class="act-btn" @click="prepareReply(item)"><i class="el-icon-chat-dot-square"></i> 回复</span>
                    <span v-if="item.creator === currentUserId" class="act-btn del" @click="handleDelete(item.id)">
                    <i class="el-icon-delete"></i> 删除
                  </span>
                  </div>
                </div>
              </div>
              <div class="children-list" v-if="item.children && item.children.length > 0">
                <div v-for="child in item.children" :key="child.id" class="child-item">
                  <el-avatar :src="getPicUrlByJson(child.creatorHead, 0)" :size="30"></el-avatar>
                  <div class="c-main">
                    <div class="c-user-info">
                      <span class="user-name">{{ child.createName }}</span>
                      <span class="user-time">{{ child.createTime }}</span>
                    </div>
                    <div class="c-content">
                      <span class="at-tag">@{{ child.replyName }}</span>
                      {{ child.commentDetail }}
                    </div>
                    <div class="c-actions">
                      <span class="act-btn" @click="prepareReply(child)"><i class="el-icon-chat-dot-square"></i> 回复</span>
                      <span v-if="child.creator === currentUserId" class="act-btn del" @click="handleDelete(child.id)">
                          <i class="el-icon-delete"></i> 删除
                        </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无评论，快来抢沙发" :image-size="80"></el-empty>
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
  mixins:[common],
  data() {
    return {
      detail: {},
      comments: [],
      commentText: '',
      replyId: null,
      replyName: '',
    };
  },
  computed: {
    currentUserId() {
      const user = this.$store.getters.getUser;
      return user ? user.id : null;
    },
  },
  mounted() { this.getDetail(); },
  methods: {
    async getDetail() {
      const id = this.$route.query.id;
      const res = await request({ url: config.backHost + `/tripStrategy/getById/${id}` });
      if(res.code === 200) { this.detail = res.data; this.loadComments(); }
    },
    loadComments() {
      request({ url: config.backHost + `/tripStrategyComment/getByTripStrategyId/${this.detail.id}` }).then(res => { this.comments = res.data; });
    },
    prepareReply(item) { this.replyId = item.id; this.replyName = item.createName; },
    cancelReply() { this.replyId = null; this.replyName = ''; this.commentText = ''; },
    submitComment() {
      if (!this.commentText) return this.$message.warning("写点什么吧");
      let params = { tripStrategyId: this.detail.id, commentDetail: this.commentText };
      if(this.replyId) params.replayCommentId = this.replyId;
      request({ url: config.backHost + "/tripStrategyComment/addComment", method:'post', data: params }).then(res => {
        if (res.code === 200) { this.commentText = ''; this.cancelReply(); this.loadComments(); }
      });
    },
    handleDelete(id) {
      this.$confirm('确认删除？删除后不可回退', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({ url: config.backHost + `/tripStrategyComment/delById/${id}` }).then(res => {
          if (res.code === 200) this.loadComments();
        });
      });
    }
  }
};
</script>

<style scoped lang="scss">
$primary: $theme-color;
$bg-soft: $side-bar-container-hoverBackColor;
.attraction-link {
  font-size: 12px;
  color: #FF8A45;
  background: rgba(255, 138, 69, 0.1);
  padding: 4px 12px;
  border-radius: 4px;
  margin-bottom: 15px;
  width: fit-content;
}
.post-images {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  .preview-img {
    width: 33%;
    max-width: 200px;
    max-height: 200px;
    border-radius: 4px;
    border: 1px solid #f0f0f0;
  }
}
.detail-page { background: #f9fafb; min-height: 100%; padding: 20px 0 30px; }
.container { max-width: 1220px; margin: 0 auto; padding: 0 20px; }

.nav-bar { margin-bottom: 25px; }

.main-content { background: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); overflow: hidden; }
.interaction-bar { display: flex; gap: 30px; padding-top: 20px; .stat-item { color: #999; font-size: 14px; i { margin-right: 5px; color: $primary; font-size: 16px; } } }
.article-box {
  padding: 20px 40px; border-bottom: 1px solid #f0f0f0;
  .title { font-size: 32px; color: #1a1a1a; margin-bottom: 25px; line-height: 1.4; }
  .author-bar {
    display: flex; justify-content: space-between; align-items: center;
    background: #fcfcfc; padding: 15px 0px; border-radius: 10px;
    .left {
      display: flex; align-items: center; gap: 15px;
      .name { font-weight: bold; font-size: 16px; color: #333; }
      .time { color: #999; font-size: 13px; margin-top: 4px; }
    }
    .right-stats { color: #bbb; font-size: 14px; span { margin-left: 20px; i { margin-right: 4px; } } }
    .avatar-shadow { border: 2px solid #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
  }
  .article-content { font-size: 17px; line-height: 1.8; color: #333; margin-bottom: 15px; white-space: pre-wrap; }
  .content-img { width: 100%; border-radius: 10px; margin-top: 10px; }
}

.comment-section {
  padding: 40px;
  .comment-header {
    display: flex; align-items: center; gap: 20px; margin-bottom: 30px;
    .title { font-size: 24px; font-weight: bold; color: #1a1a1a; white-space: nowrap; }
    .line { height: 1px; background: #eee; width: 100%; }
  }
}

.input-card {
  background: #fff; padding: 20px; border-radius: 12px; border: 1px solid #eee;
  ::v-deep .el-textarea__inner { border: none; padding: 0; font-size: 15px; &:focus { outline: none; } }
  .input-footer { display: flex; justify-content: flex-end; align-items: center; gap: 15px; margin-top: 15px; }
  .submit-btn { background: $primary; color: #fff; border: none; padding: 10px 25px; border-radius: 20px; font-weight: bold; &:hover { opacity: 0.9; } }
}

.comment-item {
 padding: 25px 0; border-bottom: 1px solid #f7f7f7;
  &:last-child { border-bottom: none; }
  .c-main {
    flex: 1;
    .c-user-info { display: flex;align-items:center;justify-content: space-between;margin-bottom: 8px; .user-name { font-weight: bold; color: #333; margin-right: 12px; } .user-time { color: #ccc; font-size: 12px; } }
    .c-content { font-size: 15px; color: #444; line-height: 1.6; .at-tag { color: $primary; font-weight: bold; margin-right: 6px; } }
    .c-actions {
      margin-top: 12px; display: flex; gap: 20px;
      .act-btn {
        font-size: 13px; color: #999; cursor: pointer; transition: 0.2s;
        i { margin-right: 4px; } &:hover { color: $primary; }
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
    .at-tag { color: $primary; font-weight: bold; margin-right: 5px; }
  }
}
</style>
