<template>
  <div class="form-box">
    <own-sidebar-dialog
        width="600px"
        :title="title"
        :visible.sync="visible"
        :before-close="closeDialog"
        :modal="true"
    >
      <template #default>
        <div v-if="formData" class="detail-container">
          <div class="status-banner">
            <span class="label">当前状态：</span>
            <el-tag :type="getStateTag(formData.orderState)" effect="dark">
              {{ getLabelByValue(orderStateOptions, formData.orderState) }}
            </el-tag>
          </div>

          <el-descriptions title="基础信息" :column="1" border>
            <el-descriptions-item label="订单编码">
              <span class="order-code">{{ formData.orderCode }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="入住酒店">
              {{ formData.hotelShot?JSON.parse(formData.hotelShot).htoelName:'' }}
            </el-descriptions-item>
            <el-descriptions-item label="房型名称">
              <el-tag size="small">{{ getLabelByValue(roomIdOptions, formData.roomId, 'roomName') }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="入住周期">
              <span class="date-text">{{ formData.startDate }}</span>
              至
              <span class="date-text">{{ formData.endDate }}</span>
              <span class="days-tag">（共 {{ formData.days }} 晚）</span>
            </el-descriptions-item>
            <el-descriptions-item label="购买数量">
              {{ formData.buyCount }} 间
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">
              <span class="price-amount">￥{{ formData.totalPrice }}</span>
            </el-descriptions-item>
          </el-descriptions>

          <el-descriptions title="核销与客户" :column="1" border style="margin-top: 20px">
            <el-descriptions-item label="购买用户">
              {{ formData.createName }}
            </el-descriptions-item>
            <el-descriptions-item label="核销码">
              <span class="captcha-text">{{ formData.captcha || '未生成' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="核销时间">
              {{ formData.writeoffTime || '尚未核销' }}
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">
              {{ formData.createTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </template>

      <template #footer>
        <el-button type="info" size="small" plain @click="closeDialog">关闭</el-button>
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
  name: "HotelOrderDetail",
  mixins: [FormUtils],
  components: { OwnSidebarDialog },
  data() {
    return {
      hotelIdOptions: [],
      orderStateOptions: [
        { "value": "10", "label": "待付款" },
        { "value": "20", "label": "待核销" },
        { "value": "80", "label": "已核销" },
        { "value": "-1", "label": "已取消" }
      ],
      userIdOptions: [],
      roomIdOptions: [],
      formData:{}
    };
  },
  methods: {
    initForm() {
      request({
        url: config.backHost + "/hotelInfo/listPage",
        data: {}
      }).then(res => { this.hotelIdOptions = res.data; });

      request({
        url: config.backHost + "/user/listPage",
        data: { "params": { "userRole": "2" } }
      }).then(res => { this.userIdOptions = res.data; });

      request({
        url: config.backHost + "/hotelRoom/listPage",
        data: {}
      }).then(res => { this.roomIdOptions = res.data; });
    },
    getLabelByValue(options, value, labelField = 'label') {
      const item = options.find(opt => opt.id == value || opt.value == value);
      return item ? item[labelField] : '加载中...';
    },
    getStateTag(state) {
      const map = { "10": "danger", "20": "warning", "80": "success", "-1": "info" };
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
  background: #fdf6ec;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  .label {
    font-weight: bold;
    color: #606266;
    margin-right: 10px;
  }
}
.order-code {
  font-family: monospace;
  font-weight: bold;
  color: #409EFF;
}
.price-amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.date-text {
  font-weight: bold;
  color: #303133;
}
.days-tag {
  color: #909399;
  font-size: 12px;
}
.captcha-text {
  letter-spacing: 2px;
  font-weight: bold;
  color: #67c23a;
}
::v-deep .el-descriptions__header {
  margin-bottom: 10px;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}
::v-deep{
  th{
    width: 100px;
    text-align: center !important;
  }
}
</style>
