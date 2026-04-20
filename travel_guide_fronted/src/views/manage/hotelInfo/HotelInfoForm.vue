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
          hotelDesc: '',
          hotelDetail: '',
          hotelService: '',
      },
      addUrl: '/hotelInfo/saveOrUpdate',
      editUrl: '/hotelInfo/saveOrUpdate',

    };
  },
  methods: {
    initForm(){
    },
    clearData(){
      this.formData = {
        htoelPic: '',
        htoelName: '',
        hotelDesc: '',
        hotelDetail: '',
        hotelService: '',
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
  }
};
</script>
