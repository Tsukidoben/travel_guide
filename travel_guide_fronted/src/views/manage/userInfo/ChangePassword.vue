<template>
  <div class="password-box">
    <el-dialog
        class="password-dialog"
        top="100px"
        width="600px"
        title="修改密码"
        append-to-body
        :visible="passwordVisible"
        :close-on-click-modal="false"
        @close="closeDialog"
    >
      <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="旧密码" prop="oldPwd">
          <el-input prefix-icon="el-icon-lock" show-password auto-complete="new-password" v-model="ruleForm.oldPwd"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="newPwd">
          <el-input prefix-icon="el-icon-lock" show-password  type="password" v-model="ruleForm.newPwd" auto-complete="new-password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="repeatPwd">
          <el-input prefix-icon="el-icon-lock" show-password type="password" v-model="ruleForm.repeatPwd" auto-complete="new-password"></el-input>
        </el-form-item>

        <el-form-item>

        </el-form-item>

      </el-form>
      <template slot="footer">
        <el-button size="small" @click="resetForm('ruleForm')">重置</el-button>
        <el-button size="small" type="primary" @click="submitForm('ruleForm')">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";

export default {
  name: "ChangePassword",
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.ruleForm.repeatPwd !== '') {
          this.$refs.ruleForm.validateField('repeatPwd');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.ruleForm.newPwd) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      ruleForm: {
        oldPwd: '',
        newPwd: '',
        repeatPwd: '',
      },
      rules: {
        oldPwd: [
          { required: true, message: '请输入原密码', trigger: 'blur' },
        ],
        newPwd: [
          {required: true,validator: validatePass, trigger: 'blur'},
          { min: 6, max: 20, message: '密码长度应为6-20位', trigger: 'blur' }
        ],
        repeatPwd: [
          {required: true,validator: validatePass2, trigger: 'blur'},
          { min: 6, max: 20, message: '密码长度应为6-20位', trigger: 'blur' }
        ],
      },
    }
  },
  props: {
    passwordVisible: {
      type: Boolean,
      default: () => false,
    }
  },
  mounted() {
  },
  methods: {
    closeDialog() {
      this.$emit('update:passwordVisible', false)
    },
    resetForm(){
      this.$refs.ruleForm.resetFields();
    },
    submitForm(formName){
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          request({
            url:config.backHost + "/user/changePwd",
            data:this.ruleForm
          }).then(res=>{
            if(res.code===200){
              this.$message.success("密码修改成功,请重新登录");
              localStorage.clear();
              this.$router.replace({name: 'login'});
            }else{
              this.$message.error(res.msg);
            }
          })
        }
      });
    }
  }
}
</script>

<style scoped>
.password-dialog ::v-deep .el-dialog__body{
  padding: 20px 10px 0 10px !important;
}

.password-dialog ::v-deep .el-dialog__footer{
  padding: 10px !important;
  border-top: 1px solid RGB(211,211,211) !important;
}

.password-dialog ::v-deep .el-dialog__header{
  padding: 10px !important;
  border-bottom: 1px solid RGB(211,211,211) !important;
}
</style>

