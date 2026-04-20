<template>
  <div class="form-box">
    <own-sidebar-dialog width="520px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true">
      <template #default>
        <el-form :model="formData" ref="form" label-width="80px" style="padding: 10px 0px">

          <el-form-item label="攻略图片">
            <div class="image-preview-wrapper"  v-if="formData.strategyPic">
              <el-image
                  v-for="(item,index) in JSON.parse(formData.strategyPic)"
                  :src="getPicUrlByJson(formData.strategyPic,index)"
                  :preview-src-list="[getPicUrlByJson(formData.strategyPic,index)]"
                  class="detail-image">
              </el-image>
            </div>
            <div v-else class="no-image">暂无图片</div>
          </el-form-item>

          <el-form-item label="攻略内容">
            <div class="detail-content-text">
              {{ formData.strategyContent || '暂无内容' }}
            </div>
          </el-form-item>

          <el-form-item label="攻略状态">
            <el-tag :type="getStatusTag(formData.status)">
              {{ statusOptions.find(i => i.value == formData.status)?.label || '未知' }}
            </el-tag>
          </el-form-item>

          <el-form-item label="审核意见" v-if="formData.status != '10'">
            <div class="review-box">
              {{ formData.reviewReason || '已通过审核' }}
            </div>
          </el-form-item>

          <el-form-item label="审核时间" v-if="formData.reviewTime">
            <span class="detail-time">{{ formData.reviewTime }}</span>
          </el-form-item>

        </el-form>
      </template>
      <template #footer>
        <el-button size="small" @click="closeDialog" plain>关闭</el-button>
      </template>
    </own-sidebar-dialog>
  </div>
</template>

<script>
import FormUtils from '@/utils/formUtils'
import OwnSidebarDialog from "@/components/OwnSidebarDialog.vue";

export default {
  name: "TripStrategyForm",
  mixins: [FormUtils],
  components: { OwnSidebarDialog },
  data() {
    return {
      formData: {
        strategyPic: '',
        strategyContent: '',
        status: '',
        reviewReason: '',
        reviewTime: '',
      },
      statusOptions: [
        {"value":"10","label":"待审核"},
        {"value":"80","label":"审核通过"},
        {"value":"-2","label":"审核不通过"}
      ],
    };
  },
  methods: {
    getStatusTag(status) {
      const map = { "10": "warning", "80": "success", "-2": "danger" };
      return map[status] || "info";
    },
    clearData(){
      this.formData = {
        strategyPic: '',
        strategyContent: '',
        status: '',
        reviewReason: '',
        reviewTime: '',
      };
    },
    beforeSave() {
      return '';
    }
  }
};
</script>

<style scoped lang="scss">
.image-preview-wrapper {
  display: flex;
  gap: 10px;
  .detail-image {
    width: 120px;
    height: 120px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    border: 1px solid #eee;
    display: block;
  }
  .no-image {
    width: 180px;
    height: 180px;
    background: #f5f7fa;
    color: #909399;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
  }
}

.detail-content-text {
  background-color: #f8f9fb;
  padding: 12px 15px;
  border-radius: 8px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  border: 1px solid #ebf0f5;
  min-height: 80px;
}

.review-box {
  padding: 10px 15px;
  background: #fff5f5;
  color: #f56c6c;
  border-radius: 6px;
  border-left: 4px solid #f56c6c;
  font-size: 14px;
}

.detail-time {
  color: #909399;
  font-family: monospace;
}

::v-deep .el-form-item__label {
  font-weight: bold;
  color: #333;
}
</style>
