<template>
  <div class="login-card register-card">
    <div class="login-header">
      <div class="logo-icon">
        <img :src="iconMinUrl" alt="logo" style="height: 42px">
      </div>
      <h1>加入 {{ Config.projectName }}</h1>
    </div>

    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="custom-form" label-position="top">
      <div class="form-grid">
        <el-form-item prop="userName" label="昵称">
          <el-input v-model="registerForm.userName" prefix-icon="el-icon-user" placeholder="昵称" clearable></el-input>
        </el-form-item>

        <el-form-item prop="userAccount" label="账号">
          <el-input v-model="registerForm.userAccount" prefix-icon="el-icon-star-off" placeholder="账号" maxlength="20" clearable></el-input>
        </el-form-item>

        <el-form-item prop="userSex" label="性别">
          <el-select v-model="registerForm.userSex" placeholder="请选择" style="width: 100%;">
            <i slot="prefix" class="el-icon-female" style="line-height: 40px; margin-left: 5px;"></i>
            <el-option v-for="item in userSexOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item prop="userPhone" label="手机号码">
          <el-input v-model="registerForm.userPhone" maxlength="11" prefix-icon="el-icon-mobile-phone" placeholder="手机号" clearable></el-input>
        </el-form-item>

        <el-form-item prop="password" label="设置密码" class="full-width">
          <el-input type="password" v-model="registerForm.password" prefix-icon="el-icon-lock" placeholder="6-20位字符" show-password clearable></el-input>
        </el-form-item>
      </div>
    </el-form>

    <div class="btn-wrap">
      <el-button class="submit-btn" type="primary" @click="registerDiy">注 册</el-button>
    </div>

    <div class="footer-options">
      <span>已有账号？</span>
      <span class="highlight" @click="toLogin">立即登录</span>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import Config from "@/config/config";
import pinyin from "js-pinyin";

export default {
  name: "RegisterComponent",
  computed: {
    Config() { return Config }
  },
  data() {
    return {
      iconMinUrl: require("@/assets/imgs/min-logo.png"),
      userSexOptions: [{"value":"1","label":"男"},{"value":"0","label":"女"}],
      registerForm: {
        userName: '',
        userAccount: '',
        userPhone: '',
        password: '',
        userSex:'',
        userRole:2,
      },
      registerRules: {
        userName: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 20, message: '昵称长度在2到20个字符', trigger: 'blur' }
        ],
        userAccount: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 4, max: 20, message: '账号长度4-20个字符', trigger: 'blur' }
        ],
        userPhone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        userSex: [
          { required: true, message: '请选择性别', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度应为6-20位', trigger: 'blur' }
        ],
      }
    }
  },
  watch: {
    'registerForm.userName': {
      handler(newVal) {
        if (newVal) {
          this.$set(this.registerForm, 'userAccount', pinyin.getFullChars(newVal).toLowerCase());
        }
      },
      deep: true
    },
  },
  methods: {
    toLogin() { this.$emit('toLogin') },
    registerDiy() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          request({
            url: config.backHost + "/user/register",
            data: this.registerForm,
          }).then(res => {
            if (res.code == 200) {
              this.$message.success("注册成功，已自动登录");
              this.$store.dispatch('login', { user: res.data.userInfo, token: res.data.token });
              this.$router.push({ name: "manageIndex" });
            } else {
              this.$message.error(res.msg);
            }
          })
        }
      });
    },
  }
}
</script>

<style scoped lang="scss">
.register-card {
  width: 480px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  padding: 40px 45px;
  border-radius: 24px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .logo-icon {
    font-size: 32px;
    color: $theme-color;
  }

  h1 {
    font-size: 24px;
    color: #333;
    margin: 8px 0;
  }

  .divider {
    width: 30px;
    height: 3px;
    background: $theme-color;
    margin: 0 auto;
    border-radius: 2px;
  }

  p {
    font-size: 13px;
    color: #888;
    margin-top: 10px;
  }
}
::v-deep{
  .el-input__icon{
    line-height: 40px !important;
  }
}
.custom-form {
  ::v-deep .el-form-item {
    margin-bottom: 18px;

    .el-form-item__label {
      padding-bottom: 4px;
      line-height: 20px;
      font-weight: 600;
      color: #555;
      font-size: 14px;
      &::before { color: $theme-color !important; }
    }
    .el-input__inner {
      height: 45px;
      border-radius: 10px;
      background: #fdfdfd;
      padding-left: 40px;
      border: 1px solid #e1e1e1;
      &:focus {
        border-color: $theme-color;
        box-shadow: 0 0 0 3px rgba(255, 138, 69, 0.1);
      }
    }

    .el-input__prefix {
      left: 12px;
      color: $theme-color;
      display: flex;
      align-items: center;
    }
  }
}

.submit-btn {
  width: 100%;
  height: 48px;
  background-color: $theme-color !important;
  border-color: $theme-color !important;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 5px;
  margin-top: 10px;
  box-shadow: 0 8px 20px rgba(255, 138, 69, 0.2);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px rgba(255, 138, 69, 0.3);
  }
}

.footer-options {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666;

  .highlight {
    color: $theme-color;
    font-weight: bold;
    cursor: pointer;
    margin-left: 5px;

    &:hover {
      text-decoration: underline;
    }
  }
}
.register-card {
  width: 520px;
  padding: 50px 40px;
  border-radius: 20px;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 15px;

  .full-width {
    grid-column: span 2;
  }
}

.custom-form {
  ::v-deep .el-form-item {

    .el-form-item__label {
      padding-bottom: 2px;
      font-size: 13px;
    }

    .el-input__inner {
      height: 38px;
      line-height: 38px;
    }

    .el-input__prefix {
      i { line-height: 38px !important; }
    }
  }
}

.submit-btn {
  height: 42px;
  margin-top: 5px;
}

.footer-options {
  margin-top: 15px;
}
</style>
