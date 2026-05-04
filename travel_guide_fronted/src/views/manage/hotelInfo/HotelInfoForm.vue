<template>
  <div class="form-box">
    <own-sidebar-dialog width="1000px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <div style="display: flex;gap: 20px">
            <div style="width: 40%">
              <el-form-item prop="htoelPic" label="酒店图片">
                <upload-image-signel v-model="formData.htoelPic"/>
              </el-form-item>
              <el-form-item prop="htoelName" label="酒店名称">
                <el-input type="text" v-model="formData.htoelName" style="width: 100%"  placeholder="请输入酒店名称"/>
              </el-form-item>
              <el-form-item prop="address" label="酒店地址">
                <div style="display: flex; gap: 10px; align-items: flex-start;">
                  <el-input type="textarea" :rows="2" v-model="formData.address" style="flex: 1" placeholder="请输入酒店地址" @blur="handleGetCoordinates('hotel')" />
                  <el-button type="primary" size="small" :loading="geoLoading" @click="handleGetCoordinates('hotel')" style="flex-shrink: 0;">获取经纬度</el-button>
                </div>
                <!-- 隐藏字段存储经纬度 -->
                <el-input type="hidden" v-model="formData.longitude" />
                <el-input type="hidden" v-model="formData.latitude" />
              </el-form-item>
              <el-form-item prop="hotelService" label="酒店服务">
                <el-input type="textarea" :row="3" v-model="formData.hotelService" style="width: 100%"  placeholder="请输入酒店服务"/>
              </el-form-item>
              <el-form-item prop="hotelDesc" label="酒店介绍">
                <el-input type="textarea" :row="3" v-model="formData.hotelDesc" style="width: 100%"  placeholder="请输入酒店介绍"/>
              </el-form-item>
            </div>
            <div style="width: 60%">
              <el-form-item prop="hotelDetail" label="酒店描述">
                <wangEditorComponent :height="500" v-model="formData.hotelDetail" />
              </el-form-item>
            </div>
          </div>
        </el-form>
      </template>
      <template #footer>
        <el-button type="primary" size="small" @click="confirmSubmit(action,formData)">确认</el-button>
        <el-button type="warning" size="small" @click="closeDialog">关闭</el-button>
      </template>
    </own-sidebar-dialog>
  </div>
</template>

<script>
import FormUtils from '@/utils/formUtils'
import request from "@/utils/request";
import config from "@/config/config";
import OwnSidebarDialog from "@/components/OwnSidebarDialog.vue";
import UploadImageSignel from "@/components/UploadImageSignel.vue";
import wangEditorComponent from "@/components/WangEditorComponent.vue";

export default {
  name: "HotelInfoForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog  , UploadImageSignel  , wangEditorComponent },
  data() {
    return {
      formData: {
          htoelPic: '',
          htoelName: '',
          address: '',
          hotelDesc: '',
          hotelDetail: '',
          hotelService: '',
          longitude: '',
          latitude: ''
      },
      addUrl: '/hotelInfo/saveOrUpdate',
      editUrl: '/hotelInfo/saveOrUpdate',
      geoLoading: false // 经纬度获取loading状态
    };
  },
  methods: {
    initForm(){
    },
    clearData(){
      this.formData = {
        htoelPic: '',
        htoelName: '',
        address: '',
        hotelDesc: '',
        hotelDetail: '',
        hotelService: '',
        longitude: '',
        latitude: ''
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.htoelPic) {
        message += "酒店图片不能为空<br>";
      }
      if (!this.formData.htoelName) {
        message += "酒店名称不能为空<br>";
      }
      if (!this.formData.address) {
        message += "酒店地址不能为空<br>";
      }
      if (!this.formData.hotelService) {
        message += "酒店服务不能为空<br>";
      }
      if (!this.formData.hotelDesc) {
        message += "酒店介绍不能为空<br>";
      }
      if (!this.formData.hotelDetail) {
        message += "酒店描述不能为空<br>";
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
    },
  }
};
</script>
