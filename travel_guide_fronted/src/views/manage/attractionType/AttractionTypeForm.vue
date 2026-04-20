<template>
  <div class="form-box">
    <el-dialog top="10vh" width="500px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <el-form-item prop="typeName" label="分类名称">
            <el-input type="text" v-model="formData.typeName" style="width: 100%"  placeholder="请输入分类名称"/>
          </el-form-item>
          <el-form-item prop="typeDesc" label="分类简介">
            <el-input type="textarea" :row="3" v-model="formData.typeDesc" style="width: 100%"  placeholder="请输入分类简介"/>
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

export default {
  name: "AttractionTypeForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog },
  data() {
    return {
      formData: {
          typeName: '',
          typeDesc: '',
      },
      addUrl: '/attractionType/saveOrUpdate',
      editUrl: '/attractionType/saveOrUpdate',

    };
  },
  methods: {
    initForm(){
    },
    clearData(){
      this.formData = {
        typeName: '',
        typeDesc: '',
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.typeName) {
        message += "分类名称不能为空<br>";
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
