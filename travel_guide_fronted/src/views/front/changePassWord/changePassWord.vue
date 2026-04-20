<template>
  <div class="password-page-container">
    <div class="security-panel">
      <div class="panel-content">
        <div class="panel-header">
          <div class="header-icon-box">
            <i class="el-icon-lock"></i>
          </div>
          <div class="header-text">
            <h3>账号安全 / 密码更新</h3>
            <p>SECURITY VERIFICATION & ACCOUNT PROTECTION</p>
          </div>
        </div>

        <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-position="top" class="custom-form">
          <el-form-item label="当前原密码" prop="oldPwd">
            <el-input show-password type="password" v-model="ruleForm.oldPwd" class="custom-input" placeholder="请输入当前使用的密码"></el-input>
          </el-form-item>
          <el-form-item label="设置新密码" prop="newPwd">
            <el-input show-password type="password" v-model="ruleForm.newPwd" class="custom-input" placeholder="6-20位字符，建议组合字母数字"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="repeatPwd">
            <el-input show-password type="password" v-model="ruleForm.repeatPwd" class="custom-input" placeholder="请再次输入新密码"></el-input>
          </el-form-item>
        </el-form>

        <div class="panel-footer">
          <button class="submit-btn" @click="submitForm('ruleForm')">
            保存并应用新密码
          </button>
          <button class="reset-link" @click="resetForm">清空表单</button>
        </div>
      </div>
    </div>
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
        callback(new Error('请输入新密码'));
      } else {
        if (this.ruleForm.repeatPwd !== '') {
          this.$refs.ruleForm.validateField('repeatPwd');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次确认新密码'));
      } else if (value !== this.ruleForm.newPwd) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      ruleForm: { oldPwd: '', newPwd: '', repeatPwd: '' },
      rules: {
        oldPwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPwd: [
          { required: true, validator: validatePass, trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度应为6-20位', trigger: 'blur' }
        ],
        repeatPwd: [
          { required: true, validator: validatePass2, trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度应为6-20位', trigger: 'blur' }
        ],
      },
    }
  },
  methods: {
    resetForm(){ this.$refs.ruleForm.resetFields(); },
    submitForm(formName){
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          request({
            url:config.backHost + "/user/changePwd",
            data:this.ruleForm
          }).then(res=>{
            if(res.code===200){
              this.$message.success("密码修改成功, 请重新登录");
              localStorage.clear();
              this.$router.replace({name: 'login'});
            }else{
              this.$message.error(res.msg || "修改失败，请检查原密码");
            }
          })
        }
      });
    }
  }
}
</script>

<style scoped lang="scss">
$theme-dark: $theme-color;
$text-light: #94a3b8;

.password-page-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
}

.security-panel {
  width: 100%;
  max-width: 500px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(31, 45, 61, 0.08);
  border: 1px solid #edf2f7;
}

.panel-content {
  padding: 20px 50px;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;

  .header-icon-box {
    width: 50px;
    height: 50px;
    background: $theme-dark;
    color: #ffffff;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    box-shadow: 0 6px 15px rgba(31, 45, 61, 0.2);
  }

  .header-text {
    h3 { color: $theme-dark; margin: 0; font-size: 18px; font-weight: 700; }
    p { color: $text-light; margin: 4px 0 0; font-size: 11px; letter-spacing: 0.5px; }
  }
}

::v-deep .custom-form {
  .el-form-item__label {
    color: #475569 !important;
    font-weight: 600 !important;
    font-size: 13px !important;
    padding-bottom: 8px !important;
  }

  .custom-input {
    .el-input__inner {
      background-color: #f8fafc !important;
      border: 1px solid #e2e8f0 !important;
      border-radius: 8px !important;
      height: 48px;
      transition: all 0.3s;

      &:focus {
        border-color: $theme-dark !important;
        background-color: #ffffff !important;
        box-shadow: 0 0 0 3px rgba(31, 45, 61, 0.05);
      }
    }
  }

  .el-form-item__error {
    font-weight: 500;
    margin-top: 4px;
  }
}

.panel-footer {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;

  .submit-btn {
    width: 100%;
    height: 52px;
    background: $theme-dark;
    color: #ffffff;
    border: none;
    border-radius: 8px;
    font-weight: 700;
    font-size: 15px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(31, 45, 61, 0.2);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .reset-link {
    background: none;
    border: none;
    color: $text-light;
    font-size: 12px;
    cursor: pointer;
    text-decoration: underline;
    text-underline-offset: 4px;

    &:hover {
      color: $theme-dark;
    }
  }
}
::v-deep{
  .el-form-item__label{
    line-height: 20px;
  }
}
</style>
