<template>
  <div class="own-mvue-list-box">
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>

    <div class="newTableStyle">
      <div class="own-list-table-box">
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
            <el-table-column type="index" width="55" label="序号" />
            <el-table-column prop="orderCode" label="订单编码" width="180"/>
            <el-table-column prop="attractionId" label="景点名称">
              <template #default="{row}">
                <span style="font-weight: bold; color: #303133;">{{ attractionIdOptions?.find(i=>i.id == row.attractionId)?.attractionName || '未知景点' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="ticketId" label="门票类型">
              <template #default="{row}">
                <el-tag size="small" type="info">{{ ticketIdOptions?.find(i=>i.id == row.ticketId)?.ticketName || '普通票' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="buyCount" label="张数" width="80"/>
            <el-table-column prop="totalPrice" label="支付金额">
              <template #default="{row}">
                <span style="color: #f56c6c; font-weight: bold; font-size: 16px;">￥{{ row.totalPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="orderState" label="订单状态">
              <template #default="{row}">
                <el-tag :type="getStateTag(row.orderState)" effect="dark">
                  {{ orderStateOptions?.find(i=>i.value == row.orderState)?.label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预约时间" width="160"/>

            <el-table-column fixed="right" label="操作" width="200">
              <template #default="{ row }">
                <template v-if="row.orderState == '10'">
                  <el-button type="text" size="small" icon="el-icon-wallet" @click="handlePay(row)">去支付</el-button>
                  <el-button type="text" size="small" style="color: #909399" icon="el-icon-close" @click="handleCancel(row)">取消订单</el-button>
                </template>

                <template v-if="row.orderState == '-1'">
                  <el-button type="text" size="small" style="color: #f56c6c" icon="el-icon-delete" @click="delById(row, delUrl)">删除</el-button>
                </template>

                <template v-if="row.orderState == '20' && userRole == '2'">
                  <el-button type="text" size="small" icon="el-icon-view" style="color: #E6A23C" @click="showTicketCode(row)">查看码</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-pagination :current-page="pageBean.page" :page-sizes="[10, 20, 50, 100]" :page-size="pageBean.pageSize" layout="total, sizes, prev, pager, next" :total="pageBean.total" @current-change="currPageChange" @size-change="handleSizeChange" />
      </div>
    </div>

    <el-dialog title="门票入园凭证" :visible.sync="ticketVisible" width="400px" center>
      <div class="ticket-display-card" v-if="activeTicket" style="padding-bottom: 30px">
        <p class="tip">请向景区工作人员出示此 8 位核销码</p>
        <div class="code-number">{{ activeTicket.captcha }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "AttractionOrderList",
  components: { SearchTop },
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/attractionOrder/delById/",
      attractionIdOptions: [],
      orderStateOptions: [
        {"value":"10","label":"待付款"},
        {"value":"20","label":"待核销"},
        {"value":"80","label":"已核销"},
        {"value":"-1","label":"已取消"}
      ],
      ticketIdOptions: [],
      ticketVisible: false,
      activeTicket: null,
      searchList: [
        {type:'select',name:'景点',placeholder:'选择景点',value:'attractionId', isApiData: true, apiConfig:{url: '/attractionInfo/listPage', data: {}}, key: 'id' ,label: 'attractionName'},
        {type:'input',name:'订单号',placeholder:'输入订单编码',value:'orderCode'},
      ],
    }
  },
  computed: {
    userRole() { return this.$store.getters.getUser.userRole; },
  },
  mounted() {
    this.initData();
    this.query(this.searchForm, this.pageBean);
  },
  methods: {
    showTicketCode(row) {
      this.activeTicket = row;
      this.ticketVisible = true;
    },
    handlePay(row) {
      this.$confirm('确认支付该门票订单吗？', '提示', { type: 'success' }).then(() => {
        request({ url: config.backHost + "/attractionOrder/payById/"+row.id }).then(res => {
          if (res.code === 200) {
            document.querySelector("body").innerHTML = res.data;
            document.forms[0].submit();
          }
        });
      });
    },
    handleCancel(row) {
      this.$confirm('确定取消该订单吗？', '警告', { type: 'warning' }).then(() => {
        request({ url: config.backHost + "/attractionOrder/cancel/"+row.id }).then(res => {
          if (res.code === 200) {
            this.$message.success("订单已取消");
            this.query(this.searchForm, this.pageBean);
          }
        });
      });
    },
    getStateTag(state) {
      const map = { "10": "danger", "20": "warning", "80": "success", "-1": "info" };
      return map[state] || "info";
    },
    initData(){
      request({ url: config.backHost + "/attractionInfo/listPage", data: {} }).then(res => { this.attractionIdOptions = res.data; });
      request({ url: config.backHost + "/ticketInfo/listPage", data: {} }).then(res => { this.ticketIdOptions = res.data; });
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
    },
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/css/table.scss";
.own-mvue-list-box{
  max-width: 1220px;
  margin: 0 auto;
  padding: 20px;
}

/* 统一的卡片样式 */
.ticket-display-card {
  text-align: center;
  padding: 10px 0;
  .tip { color: #909399; font-size: 14px; margin-bottom: 20px; }
  .code-number {
    font-size: 32px;
    font-weight: bold;
    color: #409EFF;
    letter-spacing: 4px;
    background: #f0f7ff;
    padding: 15px 25px;
    border-radius: 8px;
    border: 2px dashed #409EFF;
    display: inline-block;
  }
  .ticket-info {
    margin-top: 25px;
    padding: 15px;
    background: #fdfdfd;
    border-radius: 4px;
    text-align: left;
    border: 1px solid #eee;
    p { margin: 8px 0; font-size: 14px; color: #606266; }
  }
}
</style>
