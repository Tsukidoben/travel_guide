<template>
  <div class="form-box">
    <own-sidebar-dialog
        width="600px"
        title="门票订单详情"
        :visible.sync="visible"
        :before-close="closeDialog"
        :modal="true"
    >
      <template #default>
        <div v-if="formData" class="detail-container">
          <div class="status-banner">
            <span class="label">订单状态：</span>
            <el-tag :type="getStateTag(formData.orderState)" effect="dark">
              {{ getLabelByValue(orderStateOptions, formData.orderState) }}
            </el-tag>
          </div>

          <div v-if="formData.orderState == '20' || formData.orderState == '80'" class="ticket-voucher">
            <div class="voucher-title">入园核销码</div>
            <div class="voucher-code">{{ formData.captcha }}</div>
          </div>

          <el-descriptions title="景点信息" :column="1" border>
            <el-descriptions-item label="所属景点">
              <span class="highlight-text">{{ getLabelByValue(attractionIdOptions, formData.attractionId, 'attractionName') }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="门票类型">
              <el-tag size="small" type="info">{{ getLabelByValue(ticketIdOptions, formData.ticketId, 'ticketName') }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="购票数量">
              {{ formData.buyCount }} 张
            </el-descriptions-item>
          </el-descriptions>

          <el-descriptions title="支付信息" :column="1" border style="margin-top: 20px">
            <el-descriptions-item label="订单编码">
              <span class="order-code">{{ formData.orderCode }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="支付金额">
              <span class="price-amount">￥{{ formData.totalPrice }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">
              {{ formData.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="核销时间" v-if="formData.writeoffTime">
              {{ formData.writeoffTime }}
            </el-descriptions-item>
          </el-descriptions>

          <el-descriptions title="购票人" :column="1" border style="margin-top: 20px">
            <el-descriptions-item label="购票用户">
              {{ formData.createName || '匿名用户' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </template>

      <template #footer>
        <el-button size="small" @click="closeDialog">关闭</el-button>
      </template>
    </own-sidebar-dialog>
  </div>
</template>

<script>
import FormUtils from '@/utils/formUtils'
import request from "@/utils/request";
import config from "@/config/config";
import OwnSidebarDialog from "@/components/OwnSidebarDialog.vue";

export default {
  name: "AttractionOrderDetail",
  mixins: [FormUtils],
  components: { OwnSidebarDialog },
  data() {
    return {
      attractionIdOptions: [],
      ticketIdOptions: [],
      orderStateOptions: [
        { "value": "10", "label": "待付款" },
        { "value": "20", "label": "待核销" },
        { "value": "80", "label": "已核销" },
        { "value": "-1", "label": "已取消" }
      ],
      formData:{}
    };
  },
  methods: {
    initForm() {
      request({
        url: config.backHost + "/attractionInfo/listPage",
        data: {}
      }).then(res => { this.attractionIdOptions = res.data; });

      request({
        url: config.backHost + "/ticketInfo/listPage",
        data: {}
      }).then(res => { this.ticketIdOptions = res.data; });
    },
    getLabelByValue(options, value, labelField = 'label') {
      const item = options.find(opt => opt.id == value || opt.value == value);
      return item ? item[labelField] : '加载中...';
    },
    getStateTag(state) {
      const map = { "10": "info", "20": "warning", "80": "success", "-1": "danger" };
      return map[state] || "info";
    }
  }
};
</script>

<style scoped lang="scss">
.detail-container {
  padding: 0 10px 20px;
}
.status-banner {
  background: #f0f9eb;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 15px;
  .label { font-weight: bold; color: #606266; }
}
.ticket-voucher {
  text-align: center;
  background: #fdf6ec;
  border: 2px dashed #e6a23c;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  .voucher-title { color: #e6a23c; font-size: 14px; margin-bottom: 10px; }
  .voucher-code { font-size: 32px; font-weight: bold; color: #303133; letter-spacing: 4px; }
  .voucher-tip { color: #909399; font-size: 12px; margin-top: 10px; }
}
.highlight-text { font-weight: bold; color: #303133; }
.order-code { font-family: monospace; }
.price-amount { color: #f56c6c; font-weight: bold; font-size: 18px; }

::v-deep .el-descriptions__header {
  margin-bottom: 10px;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
  font-size: 15px;
}
::v-deep{
  th{
    width: 100px;
    text-align: center !important;
  }
}
</style>
