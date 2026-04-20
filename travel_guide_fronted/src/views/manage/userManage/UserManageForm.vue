<template>
  <div class="form-box">
    <own-sidebar-dialog width="500px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <el-form-item prop="headPicUrl" label="用户头像">
            <upload-image-signel v-model="formData.headPicUrl"/>
          </el-form-item>
          <el-form-item prop="userName" label="用户名">
            <el-input type="text" v-model="formData.userName" style="width: 100%"  placeholder="请输入用户名"/>
          </el-form-item>
          <el-form-item prop="userAccount" label="用户账号">
            <el-input type="text" v-model="formData.userAccount" style="width: 100%"  placeholder="请输入用户账号"/>
          </el-form-item>
          <el-form-item prop="userRole" label="角色">
            <el-select style="width: 100%;" clearable v-model="formData.userRole"  placeholder="请选择角色">
              <el-option v-for="item in userRoleOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="userSex" label="性别">
            <el-select style="width: 100%;" clearable v-model="formData.userSex"  placeholder="请选择性别">
              <el-option v-for="item in userSexOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="userPhone" label="联系电话">
            <el-input type="text" v-model="formData.userPhone" maxlength="11" show-word-limit style="width: 100%"  placeholder="请输入联系电话"/>
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
import pinyin from "js-pinyin";

export default {
  name: "UserManageForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog  , UploadImageSignel },
  data() {
    return {
      formData: {
        headPicUrl: '',
        userName: '',
        userAccount: '',
        userRole: '',
        userSex: '',
        userPhone: '',
      },
      addUrl: '/user/saveOrUpdate',
      editUrl: '/user/saveOrUpdate',
      userSexOptions: [{"value":"1","label":"男"},{"value":"0","label":"女"}],
      userRoleOptions: [{"value":"1","label":"管理员"},{"value":"3","label":"商家"},{"value":"2","label":"用户"}],
    };
  },
  watch:{
    'formData.userName': {
      handler(newVal, oldVal) {
        if(this.isEmpty(this.formData.id)){
          this.$set(this.formData,'userAccount',pinyin.getFullChars(newVal).toLowerCase());
        }
      },
      deep: true  // 深层监听
    },
  },
  methods: {
    initForm(){
    },
    clearData(){
      for (let formDataKey in this.formData) {
        this.formData[formDataKey] = ''
      }
    },
    beforeSave() {
      let message = '';
      if (!this.formData.userName) {
        message += "用户名不能为空<br>";
      }
      if (!this.formData.userAccount) {
        message += "用户账号不能为空<br>";
      }
      if (!this.formData.userRole) {
        message += "角色不能为空<br>";
      }
      if (!this.formData.userSex) {
        message += "性别不能为空<br>";
      }
      if (!this.formData.userPhone) {
        message += "联系电话不能为空<br>";
      } else {
        const phoneReg = /^1[3-9]\d{9}$/;
        if (!phoneReg.test(this.formData.userPhone)) {
          message += "联系电话格式不正确<br>";
        }
      }
      return message;
    },
  }
};
</script>
