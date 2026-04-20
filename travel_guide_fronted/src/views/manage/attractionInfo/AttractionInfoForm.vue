<template>
  <div class="form-box">
    <own-sidebar-dialog width="1200px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <div style="display: flex;gap: 20px">
            <div style="flex-shrink: 0;width: 40%">
              <el-form-item prop="attractionPic" label="景点图片">
                <upload-image-more :key="newUploadImgKey" v-model="formData.attractionPic" :max="3"/>
                <p class="upload-tip">最多可上传 3 张图片，支持 jpg、png 格式</p>
              </el-form-item>
              <el-form-item prop="attractionName" label="景点名称">
                <el-input type="text" v-model="formData.attractionName" style="width: 100%"  placeholder="请输入景点名称"/>
              </el-form-item>
              <el-form-item prop="typeId" label="景点分类">
                <el-select style="width: 100%;" clearable v-model="formData.typeId"  placeholder="请选择景点分类">
                  <el-option v-for="item in typeIdOptions" :key="item.id" :label="item.typeName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item prop="attractionDesc" label="景点简介">
                <el-input type="textarea" :row="3" v-model="formData.attractionDesc" style="width: 100%"  placeholder="请输入景点简介"/>
              </el-form-item>
              <el-form-item prop="attractionPlace" label="景点位置">
                <el-input type="textarea" :row="3" v-model="formData.attractionPlace" style="width: 100%"  placeholder="请输入景点位置"/>
              </el-form-item>
            </div>
            <div style="flex-shrink: 0;width: 60%">
              <el-form-item prop="attractionDetail" label="景点描述">
                <wangEditorComponent :height="500" v-model="formData.attractionDetail" />
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
import uploadImageMore from "@/components/UploadImageMore.vue";
import wangEditorComponent from "@/components/WangEditorComponent.vue";

export default {
  name: "AttractionInfoForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog  , uploadImageMore  , wangEditorComponent },
  data() {
    return {
      formData: {
          attractionPic: '',
          attractionName: '',
          typeId: '',
          attractionDesc: '',
          attractionDetail: '',
          attractionPlace: '',
      },
      addUrl: '/attractionInfo/saveOrUpdate',
      editUrl: '/attractionInfo/saveOrUpdate',
      typeIdOptions: [],
      newUploadImgKey:new Date().getTime(),
    };
  },
  methods: {
    initForm(){
      this.newUploadImgKey = new Date().getTime()
        request({
          url: config.backHost + "/attractionType/listPage",
          data: {}
        }).then(res => {
          this.typeIdOptions = res.data;
        });

    },
    clearData(){
      this.formData = {
        attractionPic: '',
        attractionName: '',
        typeId: '',
        attractionDesc: '',
        attractionDetail: '',
        attractionPlace: '',
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.attractionPic) {
        message += "景点图片不能为空<br>";
      }
      if (!this.formData.attractionName) {
        message += "景点名称不能为空<br>";
      }
      if (!this.formData.typeId) {
        message += "景点分类不能为空<br>";
      }
      if (!this.formData.attractionDesc) {
        message += "景点简介不能为空<br>";
      }
      if (!this.formData.attractionDetail) {
        message += "景点描述不能为空<br>";
      }
      if (!this.formData.attractionPlace) {
        message += "景点位置不能为空<br>";
      }
      return message;
    },
  }
};
</script>
