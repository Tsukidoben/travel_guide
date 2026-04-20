<template>
  <div class="attraction-detail">
    <div class="container">
      <el-page-header style="padding: 0 12.5px" @back="$router.go(-1)" content="景点详情" class="custom-header"></el-page-header>
      <div class="booking-sidebar shadow-card">
        <div class="sticky-box">
          <div class="top-action-bar">
                <span @click="handleCollect">
                  <span v-if="detail.isCollect == 1" style="margin-right: 4px;color: red">❤</span>
                  <span v-if="detail.isCollect != 1" style="margin-right: 4px">♡</span>
                  <span v-if="detail.isCollect == 1">已收藏</span>
                  <span v-if="detail.isCollect != 1">收藏</span>
                </span>
          </div>

          <div class="price-header-v2">
            <span class="price-label">单价</span>
            <div class="price-amount">
              <span class="val">￥{{ currentPrice }}</span>
              <span class="unit">每人</span>
            </div>
          </div>

          <div class="booking-form-v2">
            <el-popover
                placement="bottom"
                width="320"
                trigger="click"
                popper-class="visitor-picker-popover"
            >
              <div class="visitor-picker-panel">
                <div class="visitor-row" v-for="(type, index) in visitorTypes" :key="index">
                  <div class="v-info">
                    <div class="v-name">{{ type.label }}</div>
                  </div>
                  <div class="v-control">
                    <el-button icon="el-icon-minus" circle size="mini" :disabled="type.count <= type.min" @click="type.count--"></el-button>
                    <span class="v-num">{{ type.count }}</span>
                    <el-button icon="el-icon-plus" circle size="mini" @click="type.count++"></el-button>
                  </div>
                </div>
              </div>

              <div slot="reference" class="custom-select-box">
                <i class="el-icon-user"></i>
                <span class="display-text">{{ visitorSummary }}</span>
                <i class="el-icon-caret-bottom"></i>
              </div>
            </el-popover>
          </div>
        </div>
      </div>
      <el-row :gutter="25" style="margin: 0">
        <el-col :span="17">
          <div class="main-content shadow-card">
            <div v-if="detail.attractionPic" class="card-image" :class="'pic-count-' + JSON.parse(detail.attractionPic).length">
              <el-image v-if="detail.attractionPic && JSON.parse(detail.attractionPic).length == 1" :src="getPicUrlByJson(detail.attractionPic,0)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,0)]"></el-image>
              <template v-else-if="detail.attractionPic && JSON.parse(detail.attractionPic).length == 2">
                <div style="width: 50%;overflow: hidden">
                  <el-image :src="getPicUrlByJson(detail.attractionPic,0)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,0)]"></el-image>
                </div>
                <div style="width: 50%;overflow: hidden">
                  <el-image :src="getPicUrlByJson(detail.attractionPic,1)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,1)]"></el-image>
                </div>
              </template>

              <template v-else-if="detail.attractionPic && JSON.parse(detail.attractionPic).length >= 3">
                <div style="width: 66%;overflow: hidden">
                  <el-image class="pic-main" :src="getPicUrlByJson(detail.attractionPic,0)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,0)]"></el-image>
                </div>
                <div class="side-pics">
                  <div style="width:100%;height: 50%;overflow: hidden">
                    <el-image :src="getPicUrlByJson(detail.attractionPic,1)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,1)]"></el-image>
                  </div>
                  <div style="width: 100%;height: 50%;overflow: hidden">
                    <el-image :src="getPicUrlByJson(detail.attractionPic,2)" :preview-src-list="[getPicUrlByJson(detail.attractionPic,2)]"></el-image>
                  </div>
                </div>
              </template>
            </div>
            <div>
              <el-tabs v-model="activeName" @tab-click="selTab">
                <el-tab-pane label="景点信息" name="first">
                  <div class="article-body">
                    <h1 class="attr-title">{{ detail.attractionName }}</h1>
                    <div class="meta-info">
                      <span><i class="el-icon-location-outline"></i> {{ detail.attractionPlace }}</span>
                      <span><i class="el-icon-menu"></i>{{ classify.find(item=>item.id == detail.typeId)?.typeName }}</span>
                    </div>

                    <div class="ticket-section">
                      <div class="section-label">门票选项</div>
                      <div v-if="ticketList.length > 0">
                        <div v-for="ticket in ticketList" :key="ticket.id" class="ticket-card" @click="selTicket(ticket)" :class="{ 'active': selectedTicketId === ticket.id }">
                          <div class="ticket-info">
                            <div class="t-name">{{ ticket.ticketName }}</div>
                            <div class="t-desc">{{ ticket.useScope }}</div>
                          </div>
                          <div class="ticket-price-action">
                            <div class="price-val">
                              <div style="font-size: 24px;font-weight: bold">
                                <span class="unit">￥</span>{{ ticket.ticketPrice * visitorTypes[0].count }}
                              </div>
                              <div style="font-size: 14px;color: #63687a">{{visitorTypes[0].count}}人 x ￥{{ticket.ticketPrice}}</div>
                            </div>
                            <div class="btns">
                              <el-button type="primary" plain style="border-radius: 20px" size="medium" @click="handleOrder(ticket)">立即预订</el-button>
                            </div>
                          </div>
                        </div>
                      </div>
                      <el-empty v-else description="暂无可预订门票" :image-size="60"></el-empty>
                    </div>

                    <div class="detail-html-content">
                      <div class="section-label">详细介绍</div>
                      <div v-html="detail.attractionDetail"></div>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="景点评价" name="second">
                  <el-row :gutter="25" style="margin: 0">
                    <el-col :span="24">
                      <div class="comment-input-area">
                        <el-input type="textarea" :rows="3" placeholder="觉得景点怎么样？快来评价吧..." v-model="commentForm.commentDetail"></el-input>
                        <div class="comment-action-bar">
                          <upload-image-more v-model="commentForm.pictureUrl" :max="3"/>
                          <el-button type="primary" round @click="submitComment" :loading="submitting">发布评价</el-button>
                        </div>
                      </div>
                    </el-col>
                    <el-col :span="24">
                      <div class="comment-section">
                        <div class="comment-header">
                          <span  class="section-label">景点评论</span>
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
                                <div style="display: flex;gap: 12px" v-if="item.pictureUrl">
                                  <el-image v-for="(items,indexs) in JSON.parse(item.pictureUrl)" :key="indexs" style="width: 100px;height: 100px" :src="getPicUrlByJson(item.pictureUrl,indexs)" :preview-src-list="[getPicUrlByJson(item.pictureUrl,indexs)]"></el-image>
                                </div>
                                <div class="c-actions">
                                  <span v-if="item.creator === currentUserId" class="act-btn del" @click="handleDelete(item.id)">
                                    <i class="el-icon-delete"></i> 删除
                                  </span>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <el-empty v-else description="暂无评论，快来抢沙发" :image-size="80"></el-empty>
                      </div>
                    </el-col>
                  </el-row>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </el-col>

        <el-col :span="7">

        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";
import uploadImageMore from "@/components/UploadImageMore.vue";

export default {
  components: {uploadImageMore},
  mixins: [common],
  data() {
    return {
      detail: {},
      picList: [],
      ticketList: [],
      selectedTicketId: null,
      currentPrice: 0,
      visitorTypes: [
        { label: '人数', count: 1, min: 1 },
      ],
      activeName:'first',
      classify:[],
      comments:[],
      currentUserId: this.$store.getters.getUser.id,
      commentForm: {
        attractionId: this.$route.query.id,
        commentDetail: "",
        pictureUrl: "[]"
      },
      submitting: false,
    };
  },
  computed: {
    visitorSummary() {
      const active = this.visitorTypes.filter(v => v.count > 0);
      return active.map(v => `${v.label} x ${v.count}`).join(', ');
    }
  },
  mounted() {
    this.getDetail();
    this.getComment()
    this.$nextTick(() => {
      const container = document.querySelector('.content');
      if (container) {
        setTimeout(()=>{
          container.scrollTop = 0;
        },10)
      }
    });
  },
  methods: {
    selTab(val){
      if(val.index == 1){
        this.getComment()
      }
    },
    getComment(){
      request({
        url: config.backHost + "/attractionComment/getByAttractionId/"+this.$route.query.id,
      }).then(res => {
        if (res.code === 200) {
          this.comments = res.data
        }
      })
    },
    submitComment() {
      if (!this.commentForm.commentDetail) return this.$message.warning("请输入评价内容");
      this.submitting = true;
      request({
        url: config.backHost + "/attractionComment/addComment",
        data: this.commentForm
      }).then(res => {
        if (res.code === 200) {
          this.$message.success("发布成功");
          this.commentForm.commentDetail = "";
          this.commentForm.pictureUrl = "[]";
          location.reload();
        }
      }).finally(() => this.submitting = false);
    },
    handleDelete(id) {
      this.$confirm('确认删除？删除后不可回退', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({ url: config.backHost + `/attractionComment/delById/${id}` }).then(res => {
          if (res.code === 200) this.getComment();
        });
      });
    },
    selTicket(ticket){
      this.selectedTicketId = ticket.id
      this.currentPrice = ticket.ticketPrice;
    },
    async getDetail() {
      const id = this.$route.query.id
      const res = await request({ url: config.backHost + `/attractionInfo/getById/${id}` });
      if (res.code === 200) {
        this.detail = res.data;
        this.parsePics();
        this.getClassify()
        this.getTickets(id);
      }
    },
    getClassify(){
      request({
        url: config.backHost + "/attractionType/list",
      }).then(res => {
        if (res.code === 200) {
          this.classify = res.data
        }
      });
    },
    async getTickets(id) {
      const res = await request({
        url: config.backHost + "/ticketInfo/listPage",
        method: "POST",
        data: {
          params: { attractionId: id },
          pageBean: { page: 1, pageSize: -1 }
        }
      });
      if (res.code === 200) {
        this.ticketList = res.data;
        if (this.ticketList.length > 0) {
          this.selTicket(this.ticketList[0])
        }
      }
    },
    parsePics() {
      if (this.detail.attractionPic) {
        try {
          const list = JSON.parse(this.detail.attractionPic);
          this.picList = list.map(item => config.backHost + '/file/download/' + item.id);
        } catch (e) {
          this.picList = [this.getPicUrlByJson(this.detail.attractionPic, 0)];
        }
      }
    },
    handleCollect() {
      this.setCollect()
    },
    setCollect(){
      let url = ''
      if(this.detail.isCollect == 1){
        url = '/attractionCollection/noCollect/'+this.detail.id
      } else {
        url = '/attractionCollection/collect/'+this.detail.id
      }
      request({
        url: config.backHost + url,
      }).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg)
          this.getDetail();
        }
      });
    },
    handleOrder() {
      this.$confirm('是否确认预订?', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: config.backHost + '/attractionOrder/submit',
          data:{
            "attractionId":this.$route.query.id,
            "ticketId": this.selectedTicketId,
            "buyCount": this.visitorTypes[0].count,
            "totalPrice": this.currentPrice * this.visitorTypes[0].count
          }
        }).then(res => {
          document.querySelector("body").innerHTML = res.data;
          document.forms[0].submit();
        });
      })
    }
  }
};
</script>

<style scoped lang="scss">
$primary-blue: $theme-color;
$bg-color: #f5f7fa;
$dark-text: #1a2b49;

.attraction-detail { background: $bg-color; min-height: 100%; }
.container { max-width: 1220px; margin: 0 auto; padding: 20px;position: relative;overflow-y: auto }
.custom-header { margin-bottom: 20px; }
.shadow-card { background: #fff; border-radius: 12px; box-shadow: 0 2px 15px rgba(0,0,0,0.06); margin-bottom: 25px; overflow: hidden; }

.gallery-wrapper { .gallery-img { width: 100%; height: 100%; } }

.article-body {
  padding: 0px 35px 30px;
  .attr-title { font-size: 28px; font-weight: bold; margin-bottom: 15px; color: $dark-text; }
  .meta-info { color: #555; font-size: 14px; margin-bottom: 30px; span { margin-right: 20px; i { color: $primary-blue; margin-right: 5px; } } }
}

.section-label { font-size: 20px; font-weight: bold; margin: 0px 0 20px; display: flex; align-items: center;
  &::before { content: ""; width: 4px; height: 18px; background: $primary-blue; margin-right: 10px; border-radius: 2px; }
}

.ticket-card {
  cursor: pointer;
  position: relative; border: 1px solid #e0e0e0; border-radius: 8px; padding: 20px 0px 0; margin-bottom: 15px;
  overflow: hidden;
  transition: 0.3s;
  &:hover, &.active { border-color: $primary-blue; box-shadow: 0 4px 12px rgba(0,108,228,0.1); }
  .ticket-badge { position: absolute; top: -10px; left: 15px; background: #d4111e; color: #fff; font-size: 12px; padding: 2px 8px; border-radius: 4px; }
  .ticket-info { flex: 1;padding: 0 20px; .t-name { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 8px; } .t-desc { font-size: 14px; color: #666; margin-bottom: 12px; } .t-policy { font-size: 12px; color: #008009; span { margin-right: 15px; i { margin-right: 4px; } } } }
  .ticket-price-action { background: #EBEEF1;padding: 20px 20px 20px;display: flex;justify-content: space-between;align-items: center;.price-val { font-size: 24px; font-weight: bold; color: $primary-blue; .unit { font-size: 14px; } small { font-size: 12px; color: #999; } } .btns { display: flex; gap: 10px; } }
}

.booking-sidebar {
  padding: 24px;
  position: fixed;
  top:134px;
  right: 0;
  z-index: 11;
  width: calc(1220px * (7/24) - 20px - 8.5px);
  left: calc(1220px * (17/24) + ((100vw - 1220px)/2) + 12.5px);
  box-sizing: border-box;
  .top-action-bar {
    display: flex; justify-content: flex-end; gap: 15px; margin-bottom: 20px;
    font-size: 13px; font-weight: 500; color: $dark-text;
    span { cursor: pointer; i { font-size: 16px; margin-right: 4px; vertical-align: middle; } }
  }

  .price-header-v2 {
    margin-bottom: 25px;
    .sold-out-tag { background: #d4111e; color: #fff; display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 11px; margin-bottom: 8px; }
    .price-label { color: #666; font-size: 14px; display: block; }
    .price-amount { margin-top: 4px; .val { font-size: 26px; font-weight: 800; color: $dark-text; } .unit { font-size: 14px; color: #333; margin-left: 5px; } }
  }

  .custom-select-box {
    background: #edf2f7; border-radius: 12px; height: 48px;
    display: flex; align-items: center; padding: 0 15px; cursor: pointer;
    transition: background 0.2s;
    &:hover { background: #e2e8f0; }

    i { color: #2d3748; font-size: 18px; }
    .display-text { flex: 1; margin: 0 12px; font-weight: 600; color: $dark-text; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

    ::v-deep .el-input__inner { background: transparent !important; border: none !important; height: 48px; line-height: 48px; padding: 0; font-weight: 600; color: $dark-text; cursor: pointer; }
    ::v-deep .el-input__prefix { display: none; }
    ::v-deep .el-select { width: 100%; }
  }

  .mt-12 { margin-top: 12px; }

  .order-submit-btn { width: 100%; border-radius: 24px; height: 48px; font-size: 16px; font-weight: bold; margin-top: 20px; background: $primary-blue; border: none; &:hover { background: #0056b3; } }
}

.visitor-picker-panel {
  padding: 0px 20px;
  .visitor-row {
    display: flex; justify-content: space-between; align-items: center; padding: 15px 0; border-bottom: 1px solid #f0f2f5;
    &:last-child { border-bottom: none; }
    .v-name { font-weight: bold; color: $dark-text; font-size: 15px; }
    .v-age { font-size: 12px; color: #718096; margin-top: 2px; }
    .v-control { display: flex; align-items: center; gap: 12px; .v-num { min-width: 20px; text-align: center; font-weight: bold; } }
  }
  .v-notice { color: #a0aec0; font-size: 12px; margin-top: 15px; }
}
.visitor-picker-popover { border-radius: 12px !important; box-shadow: 0 10px 25px rgba(0,0,0,0.1) !important; padding: 20px !important; }

.detail-html-content { margin-top: 40px; line-height: 1.8; color: #444; }
.card-image {
  position: relative;
  max-height: 400px;
  overflow: hidden;
  display: flex;
  gap: 4px;
  .el-image{
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform .5s ease;
    max-height: 400px;
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
}
@media (max-width: 1466px) {
  .booking-sidebar{
    top: 210px;
  }
}
@media (max-width: 1260px) {
  .booking-sidebar{
    width: calc(100vw * (7/24) - 20px - 22.5px);
    left: calc(100vw * (17/24));
  }
}
.comment-section {
  background: #fff;
  border-radius: 12px;
  padding: 0px 35px 30px;
  width: 100%;
  box-sizing: border-box;
  .comment-header {
    margin-bottom: 30px;line-height: 20px;
    .title { font-size: 24px; font-weight: bold; color: #1a1a1a; white-space: nowrap; }
    .line { height: 1px; background: #eee; width: 100%; }
  }
}

.input-card {
  background: #fff; padding: 20px; border-radius: 12px; border: 1px solid #eee;
  ::v-deep .el-textarea__inner { border: none; padding: 0; font-size: 15px; &:focus { outline: none; } }
  .input-footer { display: flex; justify-content: flex-end; align-items: center; gap: 15px; margin-top: 15px; }
  .submit-btn { background: $theme-color; color: #fff; border: none; padding: 10px 25px; border-radius: 20px; font-weight: bold; &:hover { opacity: 0.9; } }
}

.comment-item {
  padding: 25px 0; border-bottom: 1px solid #f7f7f7;
  &:last-child { border-bottom: none; }
  .c-main {
    flex: 1;
    .c-user-info { display: flex;align-items:center;justify-content: space-between;margin-bottom: 8px; .user-name { font-weight: bold; color: #333; margin-right: 12px; } .user-time { color: #ccc; font-size: 12px; } }
    .c-content { padding: 8px 0 16px;font-size: 15px; color: #444; line-height: 1.6; .at-tag { color: $theme-color; font-weight: bold; margin-right: 6px; } }
    .c-actions {
      margin-top: 12px; display: flex; gap: 20px;justify-content: right;
      .act-btn {
        font-size: 13px; color: #999; cursor: pointer; transition: 0.2s;
        i { margin-right: 4px; } &:hover { color:$theme-color;}
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
    .at-tag { color: $theme-color; font-weight: bold; margin-right: 5px; }
  }
}
::v-deep{
  .el-button--primary.is-plain{
    color: $theme-color;
    border-color: $theme-color;
    background: transparent;
  }
  .el-button--primary.is-plain:hover{
    background: $side-bar-container-hoverBackColor;
  }
}
::v-deep .el-tabs {
  .el-tabs__header {
    margin: 0;
    background: #fff;
    padding: 0 20px;
  }

  /* 激活状态的文字颜色 */
  .el-tabs__item {
    font-size: 16px;
    font-weight: 500;
    height: 55px;
    line-height: 55px;
    color: #666;
    transition: all 0.3s;

    &.is-active {
      color: #FF8A45 !important;
      font-weight: bold;
    }
    &:hover {
      color: #FF8A45;
    }
  }

  .el-tabs__active-bar {
    background-color: #FF8A45 !important;
    height: 3px;
    border-radius: 2px;
  }

  .el-tabs__nav-wrap::after {
    height: 1px;
    background-color: #f0f0f0;
  }
}

.comment-input-area {
  background: #fff;
  border: 1px solid #FDE6D8;
  padding: 20px;
  border-radius: 16px;
  margin: 20px 0 30px;
  box-shadow: 0 4px 12px rgba(253, 230, 216, 0.3);
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

.comment-item {
  .at-tag {
    color: #FF8A45 !important;
    background: #FDE6D8;
    padding: 2px 6px;
    border-radius: 4px;
  }
  .act-btn.del:hover {
    color: #FF8A45 !important;
  }
}
</style>
