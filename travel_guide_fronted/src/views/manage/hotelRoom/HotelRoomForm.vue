<template>
  <div class="form-box">
    <own-sidebar-dialog width="500px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <el-form-item prop="hotelId" label="所属酒店">
            <el-select style="width: 100%;" clearable v-model="formData.hotelId"  placeholder="请选择所属酒店">
              <el-option v-for="item in hotelIdOptions" :key="item.id" :label="item.htoelName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="roomPic" label="房间图片">
            <upload-image-signel v-model="formData.roomPic"/>
          </el-form-item>
          <el-form-item prop="roomName" label="房间名称">
            <el-input type="text" v-model="formData.roomName" style="width: 100%"  placeholder="请输入房间名称"/>
          </el-form-item>
          <el-form-item prop="roomDesc" label="房间介绍">
            <el-input type="textarea" :row="3" v-model="formData.roomDesc" style="width: 100%"  placeholder="请输入房间介绍"/>
          </el-form-item>
          <el-form-item prop="price" label="房间价格">
            <el-input type="text" v-model="formData.price" style="width: 100%"  placeholder="请输入房间价格"/>
          </el-form-item>
          <el-form-item prop="roomCount" label="房间数量">
            <el-input-number style="width: 100%;" v-model="formData.roomCount" placeholder="房间数量" :step="1"></el-input-number>
          </el-form-item>
          <el-form-item prop="peopleCount" label="可住人数">
            <el-input-number style="width: 100%;" v-model="formData.peopleCount" placeholder="可住人数" :step="1"></el-input-number>
          </el-form-item>
          <el-form-item prop="roomFloor" label="房间楼层">
            <el-input-number style="width: 100%;" v-model="formData.roomFloor" placeholder="房间楼层" :step="1"></el-input-number>
          </el-form-item>
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

export default {
  name: "HotelRoomForm",
  mixins: [FormUtils],
  components: {UploadImageSignel, OwnSidebarDialog },
  data() {
    return {
      formData: {
          hotelId: '',
          roomName: '',
          roomDesc: '',
          price: "99",
          roomCount: "10",
          peopleCount: "2",
          roomFloor: "12",
        htoelPic: '',
      },
      addUrl: '/hotelRoom/saveOrUpdate',
      editUrl: '/hotelRoom/saveOrUpdate',
      hotelIdOptions: [],

    };
  },
  methods: {
    initForm(){
        request({
          url: config.backHost + "/hotelInfo/listPage",
          data: {}
        }).then(res => {
          this.hotelIdOptions = res.data;
        });

    },
    clearData(){
      this.formData = {
        htoelPic: '',
        hotelId: '',
        roomName: '',
        roomDesc: '',
        price: "99",
        roomCount: "10",
        peopleCount: "2",
        roomFloor: "12",
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.hotelId) {
        message += "所属酒店不能为空<br>";
      }
      if (!this.formData.roomPic) {
        message += "房间图片不能为空<br>";
      }
      if (!this.formData.roomName) {
        message += "房间名称不能为空<br>";
      }
      if (!this.formData.roomDesc) {
        message += "房间介绍不能为空<br>";
      }
      if (!this.formData.price) {
        message += "房间价格不能为空<br>";
      }
      if (!this.formData.roomCount) {
        message += "房间数量不能为空<br>";
      }
      if (!this.formData.peopleCount) {
        message += "可住人数不能为空<br>";
      }
      if (!this.formData.roomFloor) {
        message += "房间楼层不能为空<br>";
      }
      return message;
    },
  }
};
</script>
<style scoped lang="scss">
</style>
