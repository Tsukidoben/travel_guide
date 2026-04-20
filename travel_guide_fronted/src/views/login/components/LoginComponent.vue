<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <img :src="iconMinUrl" alt="logo" style="height: 42px">
        </div>
        <h1>{{ Config.projectName }}</h1>
      </div>

      <el-form ref="loginForm" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="loginAccount">
          <el-input
              v-model="loginForm.loginAccount"
              prefix-icon="el-icon-user"
              placeholder="账号"
              clearable>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              type="password"
              prefix-icon="el-icon-lock"
              v-model="loginForm.password"
              placeholder="密码"
              show-password
              @keyup.enter.native="login">
          </el-input>
        </el-form-item>
      </el-form>

      <div class="btn-wrap">
        <el-button class="submit-btn" type="primary" @click="login">
          登 录
        </el-button>
      </div>

      <div class="footer-options">
        <span class="register-text">
          新用户？<span class="highlight" @click="toRegister">立即注册</span>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import Config from "@/config/config";

export default {
  name: "LoginComponent",
  computed: {
    Config() { return Config }
  },
  data(){
    return{
      iconMinUrl: require("@/assets/imgs/min-logo.png"),
      loginForm: { loginAccount: "", password: "" },
      rules: {
        loginAccount: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
        ]
      }
    }
  },
  methods:{
    toRegister(){ this.$emit('toRegister') },
    async login() {
      this.$refs.loginForm.validate((valid) => {
        if (!valid) return;
        request({
          url: config.backHost + "/user/login",
          method: 'post',
          data: this.loginForm,
        }).then(res => {
          if (res.code == 200) {
            this.$message.success("登录成功，开启您的旅程");
            this.$store.dispatch('login', { user: res.data.userInfo, token: res.data.token });
            this.$router.push({ name: "manageIndex" });
          } else {
            this.$message.error(res.msg);
          }
        });
      });
    }
  }
}
</script>

<style scoped lang="scss">

.login-page {
  width: 100%;
  position: relative;
}
::v-deep{
  .el-input__icon{
    line-height: 50px !important;
  }
}
.login-card {
  position: relative;
  z-index: 10;
  width: 400px;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .logo-icon {
    font-size: 40px;
    color: $theme-color;
    margin-bottom: 10px;
  }

  h1 {
    font-size: 26px;
    color: #333;
    margin: 10px 0;
    font-weight: 600;
  }

  .divider {
    width: 40px;
    height: 4px;
    background: $theme-color;
    margin: 0 auto;
    border-radius: 2px;
  }

  p {
    color: #666;
    font-size: 14px;
    margin-top: 15px;
    letter-spacing: 1px;
  }
}

::v-deep .el-input {
  .el-input__inner {
    height: 50px;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid #ddd;
    border-radius: 12px;
    font-size: 15px;
    padding-left: 45px;
    transition: all 0.3s;

    &:focus {
      border-color: $theme-color;
      background: #fff;
      box-shadow: 0 0 0 3px rgba(255, 138, 69, 0.1);
    }
  }

  .el-input__prefix {
    left: 15px;
    font-size: 18px;
    color: $theme-color;
    line-height: 50px;
  }
}

.el-form-item {
  margin-bottom: 25px;
}

.submit-btn {
  width: 100%;
  height: 50px;
  background-color: $theme-color !important;
  border-color: $theme-color !important;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 4px;
  box-shadow: 0 8px 20px rgba(255, 138, 69, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 24px rgba(255, 138, 69, 0.4);
    opacity: 0.9;
  }
}

.footer-options {
  margin-top: 25px;
  text-align: center;
  font-size: 14px;
  color: #555;

  .highlight {
    color: $theme-color;
    font-weight: 600;
    cursor: pointer;
    margin-left: 5px;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
