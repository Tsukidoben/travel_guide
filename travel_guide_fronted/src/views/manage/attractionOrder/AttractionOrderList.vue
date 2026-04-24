<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
    <div class="newTableStyle">
      <!--列表操作区域-->
      <div v-if="userRole == '1'" class="own-list-action-box" style="display:block;">
        <el-button plain v-if="userRole == '1'" type="primary" size="small" @click="handleWriteOff">核销</el-button>
      </div>
      <!--列表数据区域-->
      <div class="own-list-table-box">
        <!-- 表格内容 -->
        <div class="own-list-table-data-box">
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
            <el-table-column prop="writeoffTime" label="核销时间" width="160"/>
            <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="150">
              <template #default="{ row }">
                <span class="span-button" @click="openForm('view', '景点订单详情', row)">查看详情</span>
                <span v-if="row.orderState == '-1'" class="span-button" style="color:red;border-color: red" @click="delById(row, delUrl)">删除</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!--分页区域-->
        <el-pagination
                :current-page="pageBean.page"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="pageBean.pageSize"
                layout="total, sizes, prev, pager, next"
                :total="pageBean.total"
                @current-change="currPageChange"
                @size-change="handleSizeChange" />
      </div>
    </div>
    <el-dialog title="景点门票核销" :visible.sync="writeOffVisible" width="550px" center @close="resetCaptcha">
      <div class="captcha-container">
        <template v-if="!verifyOrder">
          <p class="captcha-tip">请输入游客提供的 8 位数字验证码</p>
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
              <span class="title">{{ verifyOrder.orderState == 80 ? '该门票已失效' : '查验成功，请核对信息' }}</span>
            </div>
            <div class="detail-content" v-if="verifyOrder.orderState != 80">
              <div class="detail-item">
                <span class="label">景点名称：</span>
                <span class="value">{{ JSON.parse(verifyOrder.ticketShot).ticketName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">门票名称：</span>
                <span class="value">{{ ticketIdOptions?.find(i=>i.attractionId == (JSON.parse(verifyOrder.ticketShot).attractionId))?.ticketName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">预订信息：</span>
                <span class="value">{{ verifyOrder.buyCount }}人</span>
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
            <div class="detail-content" >
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
            {{ verifyOrder.orderState == 80 ? '已被核销' : '确认核销' }}
          </el-button>
        </template>
      </span>
    </el-dialog>
    <!--表单-->
    <AttractionOrderForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import AttractionOrderForm from "@/views/manage/attractionOrder/AttractionOrderForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "AttractionOrderList",
  components: { AttractionOrderForm ,SearchTop},
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/attractionOrder/delById/",
      delBatchUrl: "/attractionOrder/delBatch",
      attractionIdOptions: [],
      orderStateOptions: [
        {"value":"10","label":"待付款"},
        {"value":"20","label":"待核销"},
        {"value":"80","label":"已核销"},
        {"value":"-1","label":"已取消"}
      ],
      userIdOptions: [],
      ticketIdOptions: [],
      searchList: [
        {type:'select',name:'所属景点',placeholder:'所属景点',value:'attractionId', isApiData: true,apiConfig:{url: '/attractionInfo/listPage',data: {}}, key: 'id' ,label: 'attractionName'},
        {type:'select',name:'所属门票',placeholder:'所属门票',value:'ticketId', data:[], key: 'id' ,label: 'ticketName'},
        {type:'input',name:'订单编码',placeholder:'订单编码',value:'orderCode'},
      ],
      verifyOrder: null,
      writeOffVisible: false,
      writingOff: false,
      captchaArray: new Array(8).fill(''),
    }
  },
  watch: {
    'searchForm.attractionId': {
      handler(newVal) {
        this.searchForm.ticketId = '';
        if (!newVal) {
          this.loadTicketOptions(null);
          return;
        }
        this.loadTicketOptions(newVal);
      },
    }
  },
  computed: {
    userRole() {
      const user = this.$store.getters.getUser;
      return user ? user.userRole : null;
    },
  },
  mounted() {
    this.initData();
    this.query(this.searchForm, this.pageBean);
  },
  methods: {
    async queryOrderByCaptcha() {
      const captcha = this.captchaArray.join('');
      if (captcha.length < 8) return this.$message.warning("请输入完整的8位验证码");

      this.writingOff = true;
      try {
        const res = await request({
          url: config.backHost + `/attractionOrder/getByCaptcha/${captcha}`
        });
        if (res.code === 200 && res.data) {
          this.verifyOrder = res.data;
          if (this.verifyOrder.orderState == 80) {
            this.$notify.error({
              title: '核销失败',
              message: '该核验码已被核验过，请检查是否已办理过入住。'
            });
          }
        } else {
          this.$message.error(res.msg || "无效的验证码");
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
          url: config.backHost + `/attractionOrder/writeOff/${captcha}`,
        });
        if (res.code === 200) {
          this.$message.success("核销成功！门票已生效");
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
      this.captchaArray = new Array(8).fill('');
      this.$nextTick(() => {
        this.$refs.captchaInput0[0].focus();
      });
    },
    handleCaptchaInput(e, index) {
      const val = e.target.value;
      if (!/^\d$/.test(val)) {
        this.captchaArray[index] = '';
        return;
      }
      if (val && index < 7) {
        this.$refs['captchaInput' + (index + 1)][0].focus();
      }
    },
    handleCaptchaDelete(index) {
      if (!this.captchaArray[index] && index > 0) {
        this.$refs['captchaInput' + (index - 1)][0].focus();
      }
    },
    async submitWriteOff() {
      const captcha = this.captchaArray.join('');
      if (captcha.length < 8) {
        return this.$message.warning("请输入完整的8位验证码");
      }
      this.writingOff = true;
      try {
        const res = await request({
          url: config.backHost + `/attractionOrder/writeOff/${captcha}`,
        });
        if (res.code === 200) {
          this.$message.success("核销成功！");
          this.writeOffVisible = false;
          this.query(this.searchForm, this.pageBean);
        } else {
          this.$message.error(res.msg || "核销失败");
        }
      } finally {
        this.writingOff = false;
      }
    },
    async loadTicketOptions(attractionId) {
      const res = await request({
        url: config.backHost + "/ticketInfo/listPage",
        data: {
          "pageBean": {
            "page": 1,
            "pageSize": -1
          },
          "params": {
            "attractionId": attractionId || "",
          }
        }
      });
      if (res.code === 200) {
        this.searchList[1].data = res.data;
      }
    },
    getStateTag(state) {
      const map = { "10": "info", "20": "warning", "80": "success", "-1": "danger" };
      return map[state] || "";
    },
    initData(){
      request({
        url: config.backHost + "/attractionInfo/listPage",
        data: {}
      }).then(res => {
        this.attractionIdOptions = res.data;
      });

      request({
        url: config.backHost + "/user/listPage",
        data: {"params":{"userRole":"2"}}
      }).then(res => {
        this.userIdOptions = res.data;
      });

      request({
        url: config.backHost + "/ticketInfo/listPage",
        data: {}
      }).then(res => {
        this.ticketIdOptions = res.data;
      });

    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/attractionOrder/listPage",
        data: { params: searchForm, pageBean: pageBean }
      }).then(res => {
        if (res.code === 200) {
          this.tableData = res.data;
          this.pageBean = res.pageBean;
        }
      });
    }
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/css/table.scss";
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
</style>
