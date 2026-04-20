<template>
  <div class="form-box">
    <el-dialog top="10vh" width="500px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <el-form-item prop="imgUrl" label="轮播图图片">
            <upload-image-signel v-model="formData.imgUrl"/>
          </el-form-item>
          <el-form-item prop="imgTitle" label="轮播图标题">
            <el-input type="text" v-model="formData.imgTitle" style="width: 100%"  placeholder="请输入轮播图标题"/>
          </el-form-item>
          <el-form-item prop="status" label="状态">
            <el-select style="width: 100%;" clearable v-model="formData.status"  placeholder="请选择状态">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="sortNum" label="排序">
            <el-input-number style="width: 100%;" v-model="formData.sortNum" placeholder="排序" :step="1"></el-input-number>
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
import OwnSidebarDialog from "@/components/OwnSidebarDialog.vue";
import UploadImageSignel from "@/components/UploadImageSignel.vue";

export default {
  name: "CarouselImageForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog  , UploadImageSignel },
  data() {
    return {
      formData: {
          imgUrl: '',
          imgTitle: '',
          status: "1",
          sortNum: "1",
      },
      addUrl: '/carouselImage/saveOrUpdate',
      editUrl: '/carouselImage/saveOrUpdate',
      statusOptions: [{"value":"1","label":"正常"},{"value":"2","label":"不生效"}],

    };
  },
  methods: {
    initForm(){
    },
    clearData(){
      this.formData = {
        imgUrl: '',
        imgTitle: '',
        status: "1",
        sortNum: "1",
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.imgUrl) {
        message += "轮播图图片不能为空<br>";
      }
      if (!this.formData.imgTitle) {
        message += "轮播图标题不能为空<br>";
      }
      if (!this.formData.status) {
        message += "状态不能为空<br>";
      }
      if (!this.formData.sortNum) {
        message += "排序不能为空<br>";
      }
      return message;
    },
  }
};
</script>
<style scoped lang="scss">
  ::v-deep .el-dialog__body{
    max-height: 60vh;
    overflow-x: hidden;
    overflow-y: auto;
  }
</style>
