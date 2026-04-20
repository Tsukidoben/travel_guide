<template>
  <div class="chat-container">
    <div class="session-list" :style="isFront?'width:360px':''">
      <div class="chat-header">
        <span class="title">会话列表</span>
      </div>
      <div class="session-scroll" v-if="roomDetil.length > 0 || sessionList.length > 0">
        <div
            class="session-item"
            v-for="item in roomDetil"
            :key="item.id"
            :class="{active: item.id === activeRoomId}"
            @click="changeSession(item)"
            v-show="roomNewFlag"
        >
          <div style="display: flex;align-items: center;width: calc(100% - 100px);flex-shrink: 0;overflow-x: hidden">
            <img class="avatar" :src="getPicUrlByJson(item.avatar)" />
            <div class="info">
              <div class="name one-line" :title="item.name">{{ item.name }}</div>
              <div class="msg one-line" v-if="item.recentMsgType == 1">{{ item.lastMsg }}</div>
              <div class="msg one-line" v-if="item.recentMsgType == 2">【图片】</div>
              <div class="msg one-line" v-if="item.recentMsgType == 3">【视频】</div>
              <div class="msg one-line" v-if="item.recentMsgType == 4">【商品信息】</div>
              <div class="msg one-line" v-if="item.recentMsgType == 5">【订单信息】</div>
              <div class="msg one-line" v-if="!item.recentMsgType"></div>
            </div>
          </div>
          <div class="time">
            <div style="height: 20px;line-height: 20px">{{ item.time?formatTime(new Date(item.time).getTime()):'' }}</div>
          </div>
          <span class="badge" v-if="item.unread > 0" :style="item.unread<99?'left:36px':'left:28px'">
            {{ item.unread > 99 ? '99+' : item.unread }}
          </span>
        </div>
        <div
            class="session-item"
            v-for="item in sessionList"
            :key="item.id"
            :class="{active: item.id === activeRoomId}"
            @click="changeSession(item)"
        >
          <div style="display: flex;align-items: center;width: calc(100% - 100px);flex-shrink: 0;overflow-x: hidden">
            <img class="avatar" :src="getPicUrlByJson(item.avatar)" />
            <div class="info">
              <div class="name one-line" :title="item.name">{{ item.name }}</div>
              <div class="msg one-line" v-if="item.recentMsgType == 1">{{ item.lastMsg }}</div>
              <div class="msg one-line" v-if="item.recentMsgType == 2">【图片】</div>
              <div class="msg one-line" v-if="item.recentMsgType == 3">【视频】</div>
              <div class="msg one-line" v-if="!item.recentMsgType"></div>
            </div>
          </div>
          <div class="time">
            <div style="height: 20px;line-height: 20px">{{ item.time?formatTime(new Date(item.time).getTime()):'' }}</div>
          </div>
          <span class="badge" v-if="item.unread > 0" :style="item.unread<99?'left:36px':'left:28px'">
            {{ item.unread > 99 ? '99+' : item.unread }}
          </span>
        </div>
      </div>
      <el-empty class="session-scroll" v-else description="暂无会话"></el-empty>
    </div>
    <div class="chat-window" v-if="activeRoomId">
      <div class="chat-header">
        <span class="title one-line" v-if="this.isFront">{{ activeRoomName }}</span>
        <span
            class="title one-line title-flex"
            v-else-if="pathName.indexOf('frontChatRoom') != -1"
        >
          <img
              class="goods-pic"
              :src="getPicUrlByJson(manageSelObj.userHead, 0)"
          >
          <span class="title-text">{{ manageSelObj.userName }}</span>
        </span>
        <span
            class="title one-line title-flex"
            v-else-if="pathName.indexOf('manageChatRoom') != -1"
        >
          <img
              class="goods-pic"
              :src="getPicUrlByJson(manageSelObj.hotelPic, 0)"
          >
          <span class="title-text">{{ manageSelObj.hotelName }}</span>
        </span>
      </div>
      <div class="chat-content" ref="contentBox">
        <div v-for="msg in messageList" :key="msg.id">
          <div v-if="msg.messageType === 99" class="time-tag">
            {{ msg.time }}
          </div>
          <div
              v-else
              :class="['msg-item', msg.userId == userInfo.id ? 'self' : 'other']"
          >
            <img class="avatar" :src="getPicUrlByJson(msg.headPicUrl,0)">
            <div class="bubble" v-if="msg.messageType == 1">
              <div v-if="msg.messageType == 1">{{ msg.messageInfo }}</div>
            </div>
            <div class="bubbles" v-if="msg.messageType != 1">
              <el-image :preview-src-list="[getPicUrlByJson(msg.messageInfo)]" class="msg-img" :src="getPicUrlByJson(msg.messageInfo)" v-if="msg.messageType == 2">
                <div slot="placeholder" class="image-slot">
                  加载中<span class="dot">...</span>
                </div>
              </el-image>
              <!-- 商品 -->
              <div v-if="msg.messageType == 4" class="goods-card">
                <div class="order-header">
                  <div>
                    <i class="el-icon-present"></i>
                    <span class="order-title">商品信息</span>
                  </div>
                </div>
                <div class="goods-body">
                  <img
                      class="goods-pics"
                      :src="getPicUrlByJson(JSON.parse(msg.messageInfo).goodsPic,0)"
                  />
                  <div class="goods-info">
                    <div class="goods-name one-line">
                      {{ JSON.parse(msg.messageInfo).goodsName }}
                    </div>
                    <div class="goods-price">
                      ￥{{ JSON.parse(msg.messageInfo).goodsPrice }}
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="msg.messageType == 5" class="order-card">
                <div class="order-header">
                  <div>
                    <i class="el-icon-s-order"></i>
                    <span class="order-title">订单信息</span>
                  </div>
<!--                  <span-->
<!--                      class="order-status"-->
<!--                      :class="'status-' + JSON.parse(msg.messageInfo).orderState"-->
<!--                  >-->
<!--                    {{ getOrderStateText(JSON.parse(msg.messageInfo).orderState) }}-->
<!--                  </span>-->
                  <span class="order-status">{{ JSON.parse(msg.messageInfo).createTime }}</span>
                </div>

                <div class="order-body">
                  <div class="order-meta">
                    <span>订单号：</span>
                    <span>{{ JSON.parse(msg.messageInfo).orderCode }}</span>
                  </div>
                  <div class="order-goods one-line">
                    {{ JSON.parse(msg.messageInfo).goodsName }}
                  </div>
                  <div class="order-price">
                    ￥{{ JSON.parse(msg.messageInfo).orderPrice }}
                  </div>
                </div>

                <div class="order-footer">
                  <el-button
                      type="text"
                      size="mini"
                      @click="viewOrder(JSON.parse(msg.messageInfo).id)"
                  >
                    查看订单
                  </el-button>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
      <div class="chat-input-area">
        <div class="tool-bar">
          <el-upload
              :show-file-list="false"
              :http-request="uploadImage"
              accept="image/*"
          >
            <div style="display: flex;margin-right: 20px">
              <i style="font-size: 16px" class="el-icon-picture-outline tool-icon"></i>
              <div style="font-size: 12px;margin-left: 8px;line-height: 18px;color: #999">图片</div>
            </div>
          </el-upload>
        </div>
        <el-input
            type="textarea"
            v-model="msgInput"
            placeholder="在此输入内容..."
            resize="none"
        />
        <div style="padding: 0 12px">
          <el-button v-loading="loading" type="primary" size="small" class="send-btn" @click="sendText">
            发送
          </el-button>
        </div>
      </div>
    </div>
    <el-empty class="session-scroll" v-else description="暂无会话"></el-empty>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";
import tableUtils from "@/utils/tableUtils";
export default {
  mixins: [common,tableUtils],
  data() {
    return {
      keyword: "",
      msgInput: "",
      activeRoomId: "",
      activeRoomName: "",
      sessionList: [],     // 会话列表
      messageList: [],     // 当前聊天记录
      timer: null,
      loading:false,
      newCreateTime:'',
      isFront:null,
      manageSelObj:{},
      roomDetil:[],
      roomNewFlag:false,
      pathName:'',
    }
  },
  props: {
    roomId: {
      type: String,
      default: ""
    },
    orderId: {
      type: String,
      default: ""
    },
  },
  computed: {
    userInfo() {
      return this.$store.getters.getUser;
    },
  },
  mounted() {
    this.pathName = window.location.pathname
    this.$nextTick(()=>{
      const pathType = window.location.pathname.split('/')[2]
      if (pathType == 'front') {
        this.isFront = true
      } else if (pathType == 'manage') {
        this.isFront = false
      }
      this.activeRoomId = this.roomId
      if(this.activeRoomId){
        this.getRoomDetil()
        this.loadSessions();
        this.firstloadMsg()
        this.startPolling();

        this.$nextTick(()=>{
          if(this.orderId){
            // 查询订单详情
           this.sendOrder(this.orderId)
          }

        })
      } else {
        request({
          url: config.backHost + "/chatRoom/"+(this.isFront?'myRoom':'myRoomManage')
        }).then(res => {
          if (res.code === 200) {
            if (Array.isArray(res.data) && res.data.length > 0) {
              if (this.$route.query.roomId != res.data[0].id && window.location.pathname.indexOf('frontChatRoom')!=-1) {
                this.$router.replace({
                  name: this.isFront?'frontChatRoom':'manageChatRoom',
                  query: { roomId: res.data[0].id }
                });
              }
              this.activeRoomId =  res.data[0].id
              this.loadSessions();
              this.firstloadMsg()
              this.startPolling();
            }
          }
        });
      }
    })
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    getOrderStateText(state) {
      const map = {
        '-2': '已退款',
        '-1': '已取消',
        '1': '待付款',
        '2': '待发货',
        '3': '待收货',
        '4': '待评价',
        '5': '交易完成',
      }
      return map[state] || '未知状态'
    },
    viewOrder(orderId) {
      this.formDialog = true;
      this.$nextTick(()=>{
        this.$refs.form.getOrder(orderId);
      })
    },
    async sendOrder(orderId) {
      if(!orderId){
        return
      }
      request({
        url: config.backHost + "/mallOrder/getById/" + this.orderId
      }).then(res => {
        let orderInfo = res.data;
        let messageInfo = {
          id: orderInfo.id,
          orderCode: orderInfo.orderCode,
          goodsName: orderInfo.goodsName,
          orderPrice: orderInfo.orderPrice,
          orderState: orderInfo.orderState,
          createTime: this.timestampToYMDHMS(orderInfo.createTime),
        }
        if(this.messageList.length == 0){
          const date = new Date();
          date.setHours(date.getHours() - 1);
          this.newCreateTime = date.getTime()
        }
        this.loading = true
        request({
          url: config.backHost + "/chatRecord/chat",
          data:{
            "roomId": this.activeRoomId,
            "messageType": 5, // 消息类型 1 文字 2图片 3 视频 4 商品 5 订单
            "messageInfo": JSON.stringify(messageInfo)
          }
        }).then(res => {
          this.msgInput = "";
          this.loadMessages(true);
          this.$emit('update:orderId','')
          this.$router.replace({name:'manageChatRoom',query:{roomId: this.roomId}})
        }).finally(()=>{
          this.loading = false
        })
      })
    },
    getRoomDetil(){
      request({
        url: config.backHost + "/chatRoom/getById/" + this.activeRoomId,
      }).then(res => {
        const session = {
          name: this.isFront ? res.data.hotelName : res.data.userName,
          lastMsg: res.data.recentMsgInfo,
          time: res.data.recentTime,
          unread: res.data.msgCount,
          avatar: this.isFront ? res.data.hotelPic : res.data.userHead,
          id: this.activeRoomId,
          recentMsgType: 1,
          ...res.data
        };
        this.roomDetil = [session];
      });
    },
    formatChatTime(ts) {
      const date = new Date(ts);
      const now = new Date();
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, "0");
      const day = date.getDate().toString().padStart(2, "0");
      const hour = date.getHours();
      const minute = date.getMinutes().toString().padStart(2, "0");
      const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
      const yesterdayStart = todayStart - 24 * 60 * 60 * 1000;
      const timeStamp = date.getTime();
      let period = "";
      if (hour < 12) period = "上午";
      else if (hour === 12) period = "中午";
      else if (hour > 12 && hour < 18) period = "下午";
      else period = "晚上";
      if (timeStamp >= todayStart) {
        return `${period} ${hour}:${minute}`;
      }
      if (timeStamp >= yesterdayStart) {
        return `昨天 ${period} ${hour}:${minute}`;
      }
      if (year === now.getFullYear()) {
        return `${month}月${day}日 ${period} ${hour}:${minute}`;
      }
      return `${year}年${month}月${day}日 ${period} ${hour}:${minute}`;
    },
    insertTimeSplits(list) {
      if (!list.length) return [];
      const result = [];
      const msgs = list;
      for (let i = 0; i < msgs.length; i++) {
        const msg = msgs[i];
        const cur = new Date(msg.createTime).getTime();
        if (i === 0) {
          result.push({
            id: "time-" + cur,
            messageType: 99,
            time: this.formatChatTime(cur),
          });
        } else {
          const prev = new Date(msgs[i - 1].createTime).getTime();
          if (cur - prev > 3 * 60 * 1000) {
            result.push({
              id: "time-" + cur,
              messageType: 99,
              time: this.formatChatTime(cur),
            });
          }
        }
        result.push(msg);
      }
      return result;
    },
    firstloadMsg(){
      request({
        url: config.backHost + "/chatRecord/getMessageByRoomId/"+this.activeRoomId,
      }).then(res => {
        this.messageList = res.data;
        this.messageList = this.insertTimeSplits(this.messageList);
        this.$nextTick(() => {
          this.scrollBottom();
        });
      });
    },
    formatTime(timeStr) {
      const date = new Date(timeStr);
      const now = new Date();
      const y = date.getFullYear();
      const m = date.getMonth() + 1;
      const d = date.getDate();
      const hh = date.getHours();
      const mm = (date.getMinutes() < 10 ? "0" : "") + date.getMinutes();
      const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
      const timeStamp = date.getTime();
      const yesterdayStart = todayStart - 24 * 60 * 60 * 1000;
      const isThisYear = y === now.getFullYear();
      if (timeStamp >= yesterdayStart && timeStamp < todayStart && isThisYear) {
        return "昨天";
      }
      if (timeStamp >= todayStart && isThisYear) {
        if (hh < 12) return `上午 ${hh}:${mm}`;
        if (hh === 12) return `中午 ${hh}:${mm}`;
        if (hh > 12 && hh < 18) return `下午 ${hh}:${mm}`;
        return `晚上 ${hh}:${mm}`;
      }
      if (!isThisYear) {
        const mm2 = m < 10 ? "0" + m : m;
        const dd2 = d < 10 ? "0" + d : d;
        return `${y}年${mm2}月${dd2}日`;
      }
      const mm2 = m < 10 ? "0" + m : m;
      const dd2 = d < 10 ? "0" + d : d;
      return `${mm2}月${dd2}日`;
    },
    loadSessions(flag) {
      request({
        url: config.backHost + "/chatRoom/" + (this.isFront ? 'myRoom' : 'myRoomManage')
      }).then(res => {
        if (res.data.length == 0) {
          this.roomNewFlag = true
          return;
        }
        this.sessionList = res.data.map(item => ({
          name: this.isFront ? item.hotelName : item.userName,
          lastMsg: item.recentMsgInfo,
          time: item.recentTime,
          unread: item.msgCount,
          avatar: this.isFront ? item.hotelPic : item.userHead,
          id: item.id,
          recentMsgType: item.recentMsgType,
          ...item
        }));
        const activeSession = this.sessionList.find(s => s.id == this.activeRoomId);
        if (activeSession) {
          this.changeSession(activeSession, flag);
          this.roomNewFlag = false
        } else {
          this.roomNewFlag = true
        }
      });
    },
    changeSession(item,flag) {
      this.activeRoomId = item.id;
      this.activeRoomName = item.name;
      this.manageSelObj = item
      console.log(item)
      if (this.$route.query.roomId != item.id && window.location.pathname.indexOf('frontChatRoom')!=-1) {
        this.$router.replace({
          name: this.isFront ? 'frontChatRoom' : 'manageChatRoom',
          query: {roomId: item.id}
        });
      }
      this.sessionList.forEach(item=>{
        if(item.id == this.activeRoomId){
          item.unread = 0
        }
      })
      if(!flag){
        this.firstloadMsg()
      }
    },
    async loadMessages(flag,flags) {
      let createTime = "";
      if (this.messageList.length > 0) {
        for (let i = this.messageList.length - 1; i >= 0; i--) {
          if (this.messageList[i].messageType !== 99) {
            createTime = this.messageList[i].createTime;
            break;
          }
        }
      } else {
        if(this.newCreateTime){
          createTime = this.timestampToYMDHMS(this.newCreateTime)
        } else {
          const date = new Date();
          date.setHours(date.getHours() - 1);
          createTime = this.timestampToYMDHMS(date.getTime())
        }
      }
      request({
        url: config.backHost + "/chatRecord/getMessageByTime",
        data: {
          roomId: this.activeRoomId,
          createTime: createTime,
        }
      }).then(res => {
        this.loadSessions(flags);
        const old = this.messageList.filter(i => i.messageType !== 99);
        const newData = res.data.filter(n =>
            !old.some(o => o.id === n.id)
        );
        if (newData.length > 0) {
          const merged = [...old, ...newData];
          this.messageList = this.insertTimeSplits(merged);
          if (flag) {
            this.$nextTick(() => {
              this.scrollBottom();
            });
          }
        }
      });
    },
    startPolling() {
      this.timer = setInterval(() => {
        if (this.activeRoomId) this.loadMessages(false,true);
      }, 1000);
    },
    scrollBottom() {
      setTimeout(()=>{
        const box = this.$refs.contentBox;
        if(box){
          box.scrollTop = box?.scrollHeight + 100;
        }
      },50)
    },
    async sendText() {
      if (!this.msgInput.trim()) return;
      if(this.messageList.length == 0){
        const date = new Date();
        date.setHours(date.getHours() - 1);
        this.newCreateTime = date.getTime()
      }
      this.loading = true
      request({
        url: config.backHost + "/chatRecord/chat",
        data:{
          "roomId": this.activeRoomId,
          "messageType": 1, // 消息类型 1 文字 2图片 3 视频
          "messageInfo": this.msgInput
        }
      }).then(res => {
        this.msgInput = "";
        this.loadMessages(true);
      }).finally(()=>{
        this.loading = false
      })
    },
    uploadImage({ file }) {
      if(this.messageList.length == 0){
        const date = new Date();
        date.setHours(date.getHours() - 1);
        this.newCreateTime = date.getTime()
      }
      const form = new FormData();
      form.append("file", file);
      request({
        url: config.backHost + "/file/upload",
        data: form,
      }).then(res => {
        request({
          url: config.backHost + "/chatRecord/chat",
          data:{
            "roomId": this.activeRoomId,
            "messageType": 2,
            "messageInfo": JSON.stringify(res.data)
          }
        }).then(res => {
          this.loadMessages(true);
        })
      })
    },
    uploadVideo({ file }) {
      if(this.messageList.length == 0){
        const date = new Date();
        date.setHours(date.getHours() - 1);
        this.newCreateTime = date.getTime()
      }
      const form = new FormData();
      form.append("file", file);
      request({
        url: config.backHost + "/file/upload",
        data: form,
      }).then(res => {
        request({
          url: config.backHost + "/chatRecord/chat",
          data:{
            "roomId": this.activeRoomId,
            "messageType": 3,
            "messageInfo": JSON.stringify(res.data)
          }
        }).then(res => {
          this.loadMessages(true);
        })
      })
    },
  }
}
</script>

<style scoped lang="scss">
.chat-container {
  display: flex;
  background: #fff;
  overflow: hidden;
  height: 100%;
}

/* 左侧会话列表 */
.session-list {
  width: 280px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.search-bar {
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.session-scroll {
  width: 100%;
  overflow-y: auto;
  height: 100%;
}

.session-item {
  display: flex;
  align-items: center;
  padding: 10px;
  justify-content: space-between;
  cursor: pointer;
  position: relative;
  height: 70px;
  box-sizing: border-box;
  font-weight: 500;
  width: 100%;
  border-left: 4px solid transparent;
}

.session-item.active {
  background: rgba(228, 174, 110,0.1);
  border-left: 4px solid $theme-colorFront;
  .name{
    color: $theme-colorFront;
  }
}

.session-item:hover {
  background: #f5f7fa;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  margin-right: 14px;
}

.info {
  text-align: left;
  width: calc(100% - 50px);
  .name{
    text-align: left;
  }
  .msg{
    text-align: left;
  }
}

.name {
  font-size: 14px;
  height: 20px;
  line-height: 20px;
  color: #000;
}

.msg {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
  height: 14px;
  line-height: 14px;
}

.time {
  font-size: 12px;
  color: #ccc;
  margin-left: 6px;
  height: 38px;
}

.badge {
  position: absolute;
  left: 28px;
  top: 10px;
  background: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 0 6px;
  height: 16px;
  line-height: 16px;
  border-radius: 8px;
}

/* 右侧聊天窗口 */
.chat-window {
  display: flex;
  flex-direction: column;
  width: calc(100% - 280px);
}

.chat-header {
  height: 55px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-size: 16px;
  font-weight: bold;
  flex-shrink: 0;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #fafafa;
  .avatar{
    width: 32px;
    height: 32px;
  }
}

.msg-item {
  display: flex;
  margin-bottom: 18px;
}

.msg-item.other {
  flex-direction: row;
  .avatar{
    margin-right: 8px;
  }
  .bubbles{
    text-align: left;
  }
}

.msg-item.self {
  flex-direction: row-reverse;
  .avatar{
    margin-left: 8px;
  }
}

.bubble {
  background: white;
  padding: 10px 14px;
  border: 1px solid #eee;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  border-radius: 4px;
  overflow-wrap: break-word;
  word-break: break-all;
  //line-height: 30px;
  position: relative;
  height: auto;
  box-sizing: border-box;
  flex-shrink: 1;
  max-width: calc(100% - 270px);
  line-height: 20px;
}
.bubbles {
  border-radius: 4px;
  overflow-wrap: break-word;
  word-break: break-all;
  position: relative;
  height: auto;
  box-sizing: border-box;
  flex-shrink: 1;
  max-width: calc(100% - 150px);
  text-align: right;
}
.msg-img {
  //width: 60%;
  max-width: 360px;
  border-radius: 6px;
  cursor: zoom-in;
}

.msg-video {
  width: 60%;
  border-radius: 6px;
}

/* 底部输入区域 */
.chat-input-area {
  border-top: 1px solid #eee;
  //padding: 12px;
  height: 180px;
}

.tool-bar {
  display: flex;
  height: 36px;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #eee;
}

.tool-icon {
  font-size: 20px;
  cursor: pointer;
}

.send-btn {
  margin-top: 6px;
  float: right;
}
::v-deep{
  .el-textarea__inner{
    border: none;
    min-height:calc(180px - 37px - 32px - 6px - 12px) !important;
  }
}
.time-tag {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin: 15px 0;
}
.image-slot{
  padding: 10px 14px;
  border: 1px solid #eee;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  border-radius: 4px;
  background: #fff;
  white-space: nowrap;
  text-align: center;
  margin-right: 154px;
  width: 200px;
}
.title-flex {
  display: flex;
  align-items: center;
  line-height: 55px;
  min-width: 0;
}

.goods-pic {
  width: 36px;
  height: 36px;
  margin-right: 8px;
  flex-shrink: 0;
}

.title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
  flex: 1;
}

.order-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  //min-width: 260px;
  width: 360px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  text-align: left;
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;

  i {
    color: $theme-colorFront;
    margin-right: 6px;
  }

  .order-title {
    font-weight: 600;
    font-size: 16px;
  }

  .order-status {
    font-size: 12px;
  }

  .status--1 { color: #999; }
  .status--2 { color: #999; }
  .status-1 { color: #e6a23c; }
  .status-2 { color: #409eff; }
  .status-3 { color: #67c23a; }
  .status-4 { color: #67c23a; }
  .status-5 { color: #67c23a; }
}

.order-body {
  .order-goods {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 6px;
  }

  .order-meta {
    font-size: 14px;
    color: #999;
    margin-bottom: 6px;
  }

  .order-price {
    font-size: 16px;
    font-weight: bold;
    color: #f56c6c;
    text-align: right;
  }
}

.order-footer {
  margin-top: 8px;
  text-align: center;
}
 // 商品信息
.goods-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  //min-width: 260px;
  width: 360px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  text-align: left;

  .order-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;

    i {
      color: $theme-colorFront;
      margin-right: 6px;
    }

    .order-title {
      font-weight: 600;
      font-size: 16px;
    }
  }
}

.goods-body{
  display: flex;
}

.goods-pics {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
  margin-right: 10px;
  flex-shrink: 0;
}

.goods-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.goods-name {
  font-size: 16px;
  color: #000000;
  font-weight: bold;
  line-height: 18px;
}

.goods-price {
  font-size: 15px;
  font-weight: bold;
  color: #f56c6c;
  text-align: left;
}

.goods-btn {
  text-align: right;
}
</style>
