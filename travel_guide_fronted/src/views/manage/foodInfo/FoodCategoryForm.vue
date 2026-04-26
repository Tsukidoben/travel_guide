<template>
  <div class="form-box">
    <el-dialog top="10vh" width="500px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <el-form-item prop="name" label="分类名称">
            <el-input type="text" v-model="formData.name" style="width: 100%"  placeholder="请输入分类名称"/>
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
  name: "FoodCategoryForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog },
  props: {
    parentCategoryList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      formData: {
          id: null,
          name: '',
      },
      addUrl: '/api/food/category/saveOrUpdate',
      editUrl: '/api/food/category/saveOrUpdate',

    };
  },
  methods: {
    initForm(){
    },
    clearData(){
      this.formData = {
        id: null,
        name: '',
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.name) {
        message += "分类名称不能为空<br>";
      }
      // 验证分类名称是否重复
      if (this.formData.name) {
        const isDuplicate = this.parentCategoryList.some(item => 
          item.name === this.formData.name && item.id !== this.formData.id
        );
        if (isDuplicate) {
          message += "分类名称已存在,请使用其他名称<br>";
        }
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
