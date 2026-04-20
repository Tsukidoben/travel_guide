<template>
  <div class="home-container">
    <div class="welcome-section card-shadow">
      <div class="welcome-content">
        <div class="user-avatar-wrapper">
          <img
              :src="getPicUrlByJson(userInfo.headPicUrl,0)"
              class="user-avatar"
              alt="avatar"
          >
        </div>
        <div class="welcome-text">
          <h2 class="greeting">
            {{ getTimeState() }}，<span>{{ userInfo.userName }}</span>
          </h2>
          <p class="project-info">
            欢迎登录 <span class="project-name">{{ config.projectName }}</span>
          </p>
        </div>
      </div>

      <div class="time-status">
        <div class="time">{{ currentTime }}</div>
        <div class="date">{{ currentDate }}</div>
      </div>
    </div>
    <div class="own-list-table-box card-shadow empty-box" v-if="userInfo.userRole == 1">
      <div style="margin-bottom: 15px; font-weight: bold; color: #F56C6C;">
        <i class="el-icon-warning"></i> 新的景点订单列表
      </div>

      <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column type="index" width="55" label="序号" />
        <el-table-column prop="orderCode" label="订单编码" width="180"/>
        <el-table-column prop="attractionId" label="所属景点">
          <template #default="{row}">
            <span>{{ attractionIdOptions?.find(i=>i.id == row.attractionId)?.attractionName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ticketId" label="所属门票">
          <template #default="{row}">
            <span>{{ ticketIdOptions?.find(i=>i.id == row.ticketId)?.ticketName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="buyCount" label="数量" width="80"/>
        <el-table-column prop="totalPrice" label="订单价格">
          <template #default="{row}">
            <span style="color: #f56c6c; font-weight: bold;">￥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderState" label="订单状态">
          <template #default="{row}">
            <el-tag :type="getStateTag(row.orderState)">
              {{ orderStateOptions?.find(i=>i.value == row.orderState)?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160"/>
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="{ row }">
            <span style="color:red;cursor: pointer" @click="$router.push({name:'attractionOrder'})">去处理</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="own-list-table-box card-shadow empty-box" v-else>
      <div style="margin-bottom: 15px; font-weight: bold; color: #F56C6C;">
        <i class="el-icon-warning"></i> 新的酒店订单列表
      </div>
      <div style="height: calc(100% - 24px - 15px)">
        <el-table
            ref="table"
            height="100%"
            :data="tableData"
            style="width: 100%"
            :cell-style="{ textAlign: 'center' }"
            :header-cell-style="{ textAlign: 'center', background: '#f8f9fa' }"
        >
          <el-table-column prop="orderCode" label="订单编号" width="180" />

          <el-table-column label="预订信息" min-width="150">
            <template #default="{row}">
              <div class="info-cell">
                <div class="hotel-name">{{ JSON.parse(row.hotelShot).htoelName }}</div>
                <div class="room-details">
                  <span>{{ JSON.parse(row.roomShot).roomName }}</span>
                  <el-divider direction="vertical"></el-divider>
                  <span>{{ JSON.parse(row.roomShot).roomFloor }}层</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="入住周期" width="150">
            <template #default="{row}">
              <div class="date-cell">
                <div><span class="dot start"></span>{{ row.startDate }}</div>
                <div><span class="dot end"></span>{{ row.endDate }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="预约详情" width="110">
            <template #default="{row}">
              <div class="count-tag">{{ row.days }}晚 / {{ row.buyCount }}间</div>
            </template>
          </el-table-column>

          <el-table-column prop="totalPrice" label="订单金额" width="100">
            <template #default="{row}">
              <span class="price-text">￥{{ row.totalPrice }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="orderState" label="状态" width="100">
            <template #default="{row}">
              <el-tag :type="getStateTag(row.orderState)" size="small" effect="dark">
                {{ orderStateOptions.find(i => i.value == row.orderState)?.label }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="createName" label="预订人" width="90" />

          <el-table-column fixed="right" label="操作" width="140">
            <template #default="{ row }">
              <span style="color:red;cursor: pointer" @click="$router.push({name:userInfo.userRole == 1?'attractionOrder':'hotelOrder'})">去处理</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import config from "@/config/config";
import common from "@/utils/common";
import request from "@/utils/request";

export default {
  name: "Home",
  mixins: [common],
  data() {
    return {
      currentTime: '',
      currentDate: '',
      timer: null,
      tableData:[],
      attractionIdOptions:[],
      ticketIdOptions:[],
      orderStateOptions: [
        {"value":"10","label":"待付款"},
        {"value":"20","label":"待核销"},
        {"value":"80","label":"已核销"},
        {"value":"-1","label":"已取消"}
      ],
    }
  },
  computed: {
    config() { return config },
    userInfo() { return this.$store.getters.getUser || {} }
  },
  mounted() {
    this.startClock();
    this.initData()
  },
  beforeDestroy() {
    if (this.timer) clearInterval(this.timer);
  },
  methods: {
    getStateTag(state) {
      const map = { "10": "info", "20": "warning", "80": "success", "-1": "danger" };
      return map[state] || "";
    },
    initData(){
      let url = ''
      if(this.userInfo.userRole == 1){
        url = "/attractionOrder/listPage"
        request({
          url: config.backHost + "/attractionInfo/listPage",
          data: {}
        }).then(res => {
          this.attractionIdOptions = res.data;
        });
        request({
          url: config.backHost + "/ticketInfo/listPage",
          data: {}
        }).then(res => {
          this.ticketIdOptions = res.data;
        });
      } else {
        url = "/hotelOrder/listPage"
      }
      request({
        url: config.backHost + url,
        data: {
          "params": {
            orderState:20
          },
          "pageBean": {
            "page": 1,
            "pageSize": -1,
            "total": 1
          }
        }
      }).then(res => {
        this.tableData = res.data;
      });
    },
    getTimeState() {
      const hour = new Date().getHours();
      if (hour < 9) return '早上好';
      if (hour < 12) return '上午好';
      if (hour < 14) return '中午好';
      if (hour < 18) return '下午好';
      return '晚上好';
    },
    startClock() {
      this.timer = setInterval(() => {
        const now = new Date();
        this.currentTime = now.toLocaleTimeString('zh-CN', { hour12: false });
        this.currentDate = now.toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
      }, 1000);
    },
  }
}
</script>

<style scoped lang="scss">
.home-container {
  //padding: 24px;
  //background-color: #f0f2f5;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-shadow {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s;
  &:hover {
    transform: translateY(-2px);
  }
}
.welcome-section {
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%);

  .welcome-content {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-avatar-wrapper {
      width: 72px;
      height: 72px;
      border-radius: 50%;
      border: 4px solid #eef2f7;
      overflow: hidden;
      .user-avatar { width: 100%; height: 100%; object-fit: cover; }
    }

    .greeting {
      font-size: 24px;
      color: #303133;
      margin: 0 0 8px 0;
      span { color: #409EFF; font-weight: 600; }
    }

    .project-info {
      color: #909399;
      margin: 0;
      .project-name { font-weight: bold; color: #606266; }
    }
  }

  .time-status {
    text-align: right;
    .time { font-size: 32px; font-weight: bold; color: #303133; font-family: 'PingFang SC'; }
    .date { color: #909399; font-size: 14px; margin-top: 4px; }
  }
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;

  .stat-card {
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: #fff;
    }
    .icon-bg-1 { background: #409EFF; }
    .icon-bg-2 { background: #67C23A; }
    .icon-bg-3 { background: #E6A23C; }
    .icon-bg-4 { background: #909399; }

    .stat-label { font-size: 14px; color: #909399; }
    .stat-value { font-size: 22px; font-weight: bold; color: #303133; margin-top: 4px; }
  }
}
.own-list-table-box {
  height: calc(100% - 140px - 20px);
  padding: 20px;box-sizing: border-box;
}

@media (max-width: 1200px) {
  .dashboard-grid { grid-template-columns: repeat(2, 1fr); }
}


.info-cell {
  text-align: center;
  .hotel-name { font-weight: bold; color: #303133; margin-bottom: 4px; }
  .room-details { font-size: 12px; color: #909399; }
}

.date-cell {
  text-align: center;
  font-size: 13px;
  color: #606266;
  .dot {
    display: inline-block; width: 6px; height: 6px; border-radius: 50%; margin-right: 8px;
    &.start { background: #67C23A; }
    &.end { background: #F56C6C; }
  }
}

.price-text { color: #f56c6c; font-weight: bold; font-size: 15px; }
.count-tag { font-size: 12px; color: #409EFF; background: #ecf5ff; border-radius: 4px; padding: 2px 4px; }
.del-btn { color: #f56c6c; }
.captcha-container {
  text-align: center;
  padding: 20px 0;

  .captcha-tip {
    font-size: 14px;
    color: #666;
    margin-bottom: 25px;
  }

  .captcha-inputs {
    display: flex;
    justify-content: center;
    gap: 10px;

    input {
      width: 40px;
      height: 50px;
      text-align: center;
      font-size: 24px;
      font-weight: bold;
      border: 2px solid #ddd;
      border-radius: 8px;
      outline: none;
      transition: all 0.2s;

      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 5px rgba(64, 158, 255, 0.2);
      }
    }
  }
}
</style>
