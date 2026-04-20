<template>
  <div class="own-mvue-list-box">
    <div class="own-list-search-box">
      <search-top
          ref="searchTop"
          :searchForm.sync="searchForm"
          :list.sync="searchList"
          @searchData="searchData"
          @clearSearch="clearSearch"
      />
    </div>

    <div class="newTableStyle">
      <div v-if="userRole == '3'" class="own-list-action-box">
        <el-button plain v-if="userRole == '3'" type="primary" size="small" @click="handleWriteOff">核销</el-button>
      </div>

      <div class="own-list-table-box">
        <div class="own-list-table-data-box">
          <el-table
              ref="table"
              height="100%"
              :data="tableData"
              style="width: 100%"
              :cell-style="{ textAlign: 'center' }"
              :header-cell-style="{ textAlign: 'center' }"
          >
            <el-table-column prop="orderCode" label="订单编号" width="180"/>

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
                <span v-if="userRole == '3'" class="span-button" style="color:red;border-color: red" @click="openForm('view','酒店订单详情',row)">查看详情</span>
                <span v-if="userRole == '3' && row.orderState == -1" class="span-button" style="color:red;border-color: red" @click="delById(row, delUrl)">删除</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-pagination
            :current-page="pageBean.page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageBean.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pageBean.total"
            @current-change="currPageChange"
            @size-change="handleSizeChange"
        />
      </div>
    </div>

    <HotelOrderForm
        ref="form"
        @reload="query(searchForm,pageBean)"
        :dialog-visible.sync="formDialog"
        :title.sync="formTitle"
        :action.sync="formAction"
    />
    <el-dialog title="酒店订单核销" :visible.sync="writeOffVisible" width="550px" center @close="resetCaptcha">
      <div class="captcha-container">
        <template v-if="!verifyOrder">
          <p class="captcha-tip">请输入 8 位数字核验码进行查询</p>
          <div class="captcha-inputs">
            <input v-for="(item, index) in 8" :key="index" :ref="'captchaInput' + index"
                   v-model="captchaArray[index]" type="text" maxlength="1"
                   @input="handleCaptchaInput($event, index)" @keydown.delete="handleCaptchaDelete(index)"/>
          </div>
        </template>

        <template v-else>
          <div class="verify-detail-card" :class="{ 'is-used': verifyOrder.orderState == 80 }">
            <div v-if="verifyOrder.orderState == 80" class="used-badge">已使用</div>

            <div class="verify-header">
              <i v-if="verifyOrder.orderState != 80" class="el-icon-circle-check" style="color: #67C23A; font-size: 24px;"></i>
              <i v-else class="el-icon-warning" style="color: #F56C6C; font-size: 24px;"></i>
              <span class="title">{{ verifyOrder.orderState == 80 ? '订单状态异常' : '查验成功，请核对信息' }}</span>
            </div>
            <div class="detail-content" v-if="verifyOrder.orderState != 80">
              <div class="detail-item">
                <span class="label">核验码：</span>
                <span class="value code-highlight">{{ verifyOrder.captcha }}</span>
              </div>
              <div class="detail-item">
                <span class="label">酒店名称：</span>
                <span class="value">{{ JSON.parse(verifyOrder.hotelShot).htoelName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">房型：</span>
                <span class="value">{{ JSON.parse(verifyOrder.roomShot).roomName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">预订信息：</span>
                <span class="value">{{ verifyOrder.buyCount }}间 / {{ verifyOrder.days }}晚</span>
              </div>
              <div class="detail-item">
                <span class="label">入住日期：</span>
                <span class="value">{{ verifyOrder.startDate }} 至 {{ verifyOrder.endDate }}</span>
              </div>
              <div class="detail-item">
                <span class="label">预订人：</span>
                <span class="value">{{ verifyOrder.createName }}</span>
              </div>
              <div class="detail-item total-price">
                <span class="label">合计金额：</span>
                <span class="value">￥{{ verifyOrder.totalPrice }}</span>
              </div>
            </div>
            <div class="detail-content">
              <div class="detail-item">
                <span class="label">当前状态：</span>
                <el-tag :type="verifyOrder.orderState == 80 ? 'success' : 'warning'" size="mini">
                  {{ verifyOrder.orderState == 80 ? '已核销完成' : '待核销' }}
                </el-tag>
              </div>
            </div>

            <div v-if="verifyOrder.orderState == 80" class="error-msg-box">
              <i class="el-icon-info"></i> 该订单已于 {{ verifyOrder.updateTime || '此前' }} 完成核销，请勿重复操作。
            </div>
          </div>
        </template>
      </div>

      <span slot="footer">
        <el-button @click="writeOffVisible = false">取 消</el-button>
        <el-button v-if="!verifyOrder" type="primary" :loading="writingOff" @click="queryOrderByCaptcha">查询订单</el-button>
        <template v-else>
          <el-button @click="verifyOrder = null">重新输入</el-button>
          <el-button
              type="success"
              :disabled="verifyOrder.orderState == 80"
              :loading="writingOff"
              @click="confirmFinalWriteOff"
          >
            {{ verifyOrder.orderState == 80 ? '无法重复核销' : '确认核销' }}
          </el-button>
        </template>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import HotelOrderForm from "@/views/manage/hotelOrder/HotelOrderForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "HotelOrderList",
  components: { HotelOrderForm, SearchTop },
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/hotelOrder/delById/",
      delBatchUrl: "/hotelOrder/delBatch",
      orderStateOptions: [
        { value: "10", label: "待付款" },
        { value: "20", label: "待核销" },
        { value: "80", label: "已完成" },
        { value: "-1", label: "已取消" }
      ],
      searchList: [
        { type: 'input', name: '订单编码', placeholder: '请输入订单号', value: 'orderCode' },
        {
          type: 'select',
          name: '订单状态',
          placeholder: '选择状态',
          value: 'orderState',
          data: [
            { value: "10", label: "待付款" },
            { value: "20", label: "待核销" },
            { value: "80", label: "已完成" },
            { value: "-1", label: "已取消" }
          ],
          key: 'value',
          label: 'label'
        }
      ],
      verifyOrder: null,
      writeOffVisible: false,
      writingOff: false,
      captchaArray: new Array(8).fill(''),
    };
  },
  computed: {
    userRole() {
      return this.$store.getters.getUser.userRole;
    }
  },
  mounted() {
    this.query(this.searchForm, this.pageBean);
  },
  methods: {
    async queryOrderByCaptcha() {
      const captcha = this.captchaArray.join('');
      if (captcha.length < 8) return this.$message.warning("请输入完整的8位验证码");

      this.writingOff = true;
      try {
        const res = await request({ url: config.backHost + `/hotelOrder/getByCaptcha/${captcha}` });
        if (res.code === 200 && res.data) {
          this.verifyOrder = res.data;
          if (this.verifyOrder.orderState == 80) {
            this.$notify.error({
              title: '核销失败',
              message: '该核验码已被核验过，请检查是否已办理过入住。'
            });
          }
        } else {
          this.$message.error(res.msg || "无效的核验码");
        }
      } finally {
        this.writingOff = false;
      }
    },
    async confirmFinalWriteOff() {
      const captcha = this.verifyOrder.captcha;
      this.writingOff = true;
      try {
        const res = await request({
          url: config.backHost + `/hotelOrder/writeOff/${captcha}`
        });
        if (res.code === 200) {
          this.$message.success("核销成功，已办理入住！");
          this.writeOffVisible = false;
          this.query(this.searchForm, this.pageBean);
        }
      } finally {
        this.writingOff = false;
      }
    },
    resetCaptcha() {
      this.captchaArray = new Array(8).fill('');
      this.verifyOrder = null;
      this.writingOff = false;
    },
    handleWriteOff() {
      this.writeOffVisible = true;
      this.$nextTick(() => this.$refs.captchaInput0[0].focus());
    },
    handleCaptchaInput(e, index) {
      const val = e.target.value;
      if (!/^\d$/.test(val)) { this.captchaArray[index] = ''; return; }
      if (val && index < 7) this.$refs['captchaInput' + (index + 1)][0].focus();
    },
    handleCaptchaDelete(index) {
      if (!this.captchaArray[index] && index > 0) this.$refs['captchaInput' + (index - 1)][0].focus();
    },
    async submitWriteOff() {
      const captcha = this.captchaArray.join('');
      if (captcha.length < 8) return this.$message.warning("验证码不完整");
      this.writingOff = true;
      try {
        const res = await request({ url: config.backHost + `/hotelOrder/writeOff/${captcha}` });
        if (res.code === 200) {
          this.$message.success("核销成功！");
          this.writeOffVisible = false;
          this.query(this.searchForm, this.pageBean);
        }
      } finally {
        this.writingOff = false;
      }
    },
    getStateTag(state) {
      const tags = { "10": "danger", "20": "warning", "80": "success", "-1": "info" };
      return tags[state] || "info";
    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/hotelOrder/listPage",
        data: { params: searchForm, pageBean: pageBean }
      }).then(res => {
        if (res.code === 200) {
          this.tableData = res.data;
          this.pageBean = res.pageBean;
        }
      });
    }
  }
};
</script>

<style scoped lang="scss">
@use "@/assets/css/table.scss";

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
.verify-detail-card {
  text-align: left;
  background: #fdfdfd;
  border: 1px solid #e1e4e8;
  border-radius: 12px;
  padding: 20px;

  .verify-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f0f0f0;
    .title { font-size: 16px; font-weight: bold; color: #333; }
  }

  .detail-content {
    .detail-item {
      margin-bottom: 12px;
      font-size: 14px;
      display: flex;
      line-height: 20px;
      .label { color: #909399; width: 80px; flex-shrink: 0; }
      .value { color: #303133; font-weight: 500; }
      .code-highlight { color: #409EFF; letter-spacing: 1px; font-weight: bold; }
    }
    .total-price {
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px dashed #eee;
      .value { color: #f56c6c; font-size: 18px; font-weight: bold; }
    }
  }
  &.is-used {
    background-color: #fcfcfc;
    border-color: #f56c6c;
    .code-highlight { color: #909399 !important; text-decoration: line-through; }
  }

  .used-badge {
    position: absolute;
    right: -20px;
    top: 10px;
    background: #f56c6c;
    color: white;
    padding: 5px 25px;
    transform: rotate(45deg);
    font-size: 12px;
    font-weight: bold;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }

  .error-msg-box {
    margin-top: 15px;
    padding: 10px;
    background: #fef0f0;
    color: #f56c6c;
    border-radius: 4px;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 5px;
  }
}

.captcha-inputs {
  display: flex;
  justify-content: center;
  gap: 8px;
  input {
    width: 42px;
    height: 52px;
    text-align: center;
    font-size: 24px;
    border: 2px solid #dcdfe6;
    border-radius: 8px;
    outline: none;
    &:focus { border-color: #409EFF; }
  }
}
</style>
