<template>
  <div class="form-box">
    <el-dialog top="10vh" width="600px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <el-form-item prop="images" label="店铺图片">
            <upload-image-more :key="newUploadImgKey" v-model="formData.images" :max="3"/>
            <p class="upload-tip">最多可上传 3 张图片，支持 jpg、png 格式</p>
          </el-form-item>
          <el-form-item prop="name" label="店铺名称">
            <el-input type="text" v-model="formData.name" style="width: 100%"  placeholder="请输入店铺名称"/>
          </el-form-item>
          <el-form-item prop="address" label="店铺地址">
            <div style="display: flex; gap: 10px; align-items: flex-start;">
              <el-input type="textarea" :rows="2" v-model="formData.address" style="flex: 1" placeholder="请输入店铺地址" @blur="handleGetCoordinates('shop')" />
              <el-button type="primary" size="small" :loading="geoLoading" @click="handleGetCoordinates('shop')" style="flex-shrink: 0;">获取经纬度</el-button>
            </div>
            <!-- 隐藏字段存储经纬度 -->
            <el-input type="hidden" v-model="formData.longitude" />
            <el-input type="hidden" v-model="formData.latitude" />
          </el-form-item>
          <el-form-item prop="phone" label="联系电话">
            <el-input type="text" v-model="formData.phone" style="width: 100%"  placeholder="请输入联系电话"/>
          </el-form-item>
          <el-form-item prop="avgPrice" label="人均价格">
            <el-input-number v-model="formData.avgPrice" :min="0" :max="9999" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item prop="businessHours" label="营业时间">
            <el-select v-model="formData.businessHours" placeholder="请选择营业时间" style="width: 100%" clearable>
              <el-option label="06:00-22:00" value="06:00-22:00"></el-option>
              <el-option label="07:00-21:00" value="07:00-21:00"></el-option>
              <el-option label="08:00-20:00" value="08:00-20:00"></el-option>
              <el-option label="09:00-21:00" value="09:00-21:00"></el-option>
              <el-option label="09:00-22:00" value="09:00-22:00"></el-option>
              <el-option label="10:00-22:00" value="10:00-22:00"></el-option>
              <el-option label="全天营业" value="全天营业"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button type="primary" size="small" @click="confirmSubmit(action,formData)">确认</el-button>
        <el-button type="warning" size="small" @click="closeDialog">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import FormUtils from '@/utils/formUtils'
import request from "@/utils/request";
import config from "@/config/config";
import uploadImageMore from "@/components/UploadImageMore.vue";

export default {
  name: "FoodShopForm",
  mixins: [FormUtils],
  components: {uploadImageMore},
  data() {
    return {
      formData: {
        id: null,
        name: '',
        images: '',
        address: '',
        phone: '',
        avgPrice: 0,
        businessHours: '',
        longitude: '',
        latitude: ''
      },
      addUrl: '/api/food/shop/saveOrUpdate',
      editUrl: '/api/food/shop/saveOrUpdate',
      newUploadImgKey: new Date().getTime(),
      geoLoading: false // 经纬度获取loading状态
    };
  },
  methods: {
    initForm(){
      this.newUploadImgKey = new Date().getTime();
    },
    clearData(){
      this.formData = {
        id: null,
        name: '',
        images: '',
        address: '',
        phone: '',
        avgPrice: 0,
        businessHours: '',
        longitude: '',
        latitude: ''
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.name) {
        message += "店铺名称不能为空<br>";
      }
      if (!this.formData.address) {
        message += "店铺地址不能为空<br>";
      }
      if (!this.formData.phone) {
        message += "联系电话不能为空<br>";
      } else if (!/^1[3-9]\d{9}$/.test(this.formData.phone)) {
        message += "请输入正确的手机号码<br>";
      }
      return message;
    },
    // 获取经纬度
    async handleGetCoordinates(type) {
      const address = this.formData.address;
      if (!address || !address.trim()) {
        this.$message.warning('请先输入地址');
        return;
      }
      
      this.geoLoading = true;
      try {
        const res = await request({
          url: config.backHost + '/api/common/geoCode',
          method: 'POST',
          data: { address: address }
        });
        
        if (res.code === 200 && res.data) {
          this.formData.longitude = res.data.longitude || '';
          this.formData.latitude = res.data.latitude || '';
          this.$message.success('经纬度获取成功');
        } else {
          this.$message.error('地址解析失败，请检查地址格式或手动输入');
        }
      } catch (error) {
        console.error('地址解析失败:', error);
        this.$message.error('地址解析失败，请检查地址格式或手动输入');
      } finally {
        this.geoLoading = false;
      }
    }
  }
};
</script>

<style scoped lang="scss">
::v-deep .el-dialog__body{
  max-height: 60vh;
  overflow-x: hidden;
  overflow-y: auto;
}
.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
