<template>
  <div class="own-mvue-list-box">
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch" />
    </div>

    <div class="newTableStyle">
      <div class="own-list-table-box">
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center', background: '#f8f9fa' }">
            <el-table-column prop="orderCode" label="订单编号" width="160" />

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

            <el-table-column fixed="right" label="操作" width="160">
              <template #default="{ row }">
                <template v-if="row.orderState == '10'">
                  <el-button type="text" size="small" icon="el-icon-wallet" @click="handlePay(row)">去支付</el-button>
                  <el-button type="text" size="small" style="color: #909399" icon="el-icon-close" @click="handleCancel(row)">取消订单</el-button>
                </template>
<!--                <el-button type="text" size="small" @click="openForm('view','订单详情',row)">详情</el-button>-->
                <el-button v-if="row.orderState == '20'" type="text" size="small" style="color: #E6A23C" icon="el-icon-view" @click="showCaptcha(row)">查看码</el-button>
                <el-button v-if="row.orderState == '-1'" type="text" size="small" style="color:red" icon="el-icon-delete" @click="delById(row, delUrl)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-pagination :current-page="pageBean.page" :page-sizes="[10, 20, 50, 100]" :page-size="pageBean.pageSize" layout="total, sizes, prev, pager, next" :total="pageBean.total" @current-change="currPageChange" @size-change="handleSizeChange" />
      </div>
    </div>

    <el-dialog title="入住凭证" :visible.sync="captchaVisible" width="400px" center>
      <div class="captcha-display-card" v-if="currentCaptcha">
        <p class="tip">办理入住时请出示此核销码</p>
        <div class="code-number">{{ currentCaptcha }}</div>
      </div>
    </el-dialog>

    <HotelOrderForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import HotelOrderForm from "@/views/manage/hotelOrder/HotelOrderForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "UserHotelOrderList",
  components: { HotelOrderForm, SearchTop },
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/hotelOrder/delById/",
      captchaVisible: false,
      currentCaptcha: '',
      currentOrderCode: '',
      orderStateOptions: [
        { value: "10", label: "待付款" },
        { value: "20", label: "待入住" },
        { value: "80", label: "已完成" },
        { value: "-1", label: "已取消" }
      ],
      searchList: [
        { type: 'input', name: '订单号', placeholder: '请输入订单号', value: 'orderCode' }
      ]
    };
  },
  mounted() {
    this.query(this.searchForm, this.pageBean);
  },
  methods: {
    handlePay(row) {
      this.$confirm('确认支付该门票订单吗？', '提示', { type: 'success' }).then(() => {
        request({
          url: config.backHost + "/hotelOrder/payById/"+row.id,
        }).then(res => {
          if (res.code === 200) {
            document.querySelector("body").innerHTML = res.data;
            document.forms[0].submit();
          }
        });
      });
    },
    handleCancel(row) {
      this.$confirm('确定取消该订单吗？', '警告', { type: 'warning' }).then(() => {
        request({
          url: config.backHost + "/hotelOrder/cancel/"+row.id,
        }).then(res => {
          if (res.code === 200) {
            this.$message.success("订单已取消");
            this.query(this.searchForm, this.pageBean);
          }
        });
      });
    },
    showCaptcha(row) {
      this.currentCaptcha = row.captcha;
      this.currentOrderCode = row.orderCode;
      this.captchaVisible = true;
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
.own-mvue-list-box{
  max-width: 1220px;
  margin: 0 auto;
  padding: 20px;
  box-sizing: border-box;
}
.captcha-display-card {
  text-align: center;
  padding: 10px 0 30px;
  .tip { color: #909399; font-size: 14px; margin-bottom: 15px; }
  .code-number {
    font-size: 36px;
    font-weight: bold;
    color: #409EFF;
    letter-spacing: 4px;
    background: #f0f7ff;
    padding: 20px;
    border-radius: 8px;
    border: 1px dashed #409EFF;
    display: inline-block;
    min-width: 200px;
  }
  .info { margin-top: 20px; color: #606266; font-size: 13px; }
}

.info-cell {
  .hotel-name { font-weight: bold; color: #303133; margin-bottom: 4px; }
  .room-details { font-size: 12px; color: #909399; }
}

.date-cell {
  font-size: 13px;
  .dot {
    display: inline-block; width: 6px; height: 6px; border-radius: 50%; margin-right: 8px;
    &.start { background: #67C23A; }
    &.end { background: #F56C6C; }
  }
}
.price-text { color: #f56c6c; font-weight: bold; font-size: 15px; }
</style>
