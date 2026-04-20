<template>
  <div class="container">
    <div class="main-container">
      <div class="login-box">
        <login-component  @toRegister="action = 'register'" @toForget="action = 'forget'" v-if="action == 'login'"/>
        <register-component @toLogin="action = 'login'" @toForget="action = 'forget'" v-if="action == 'register'"/>
      </div>
      <!--      <div class="empty-box"></div>-->
    </div>
    <div class="footer">
      Copyright 版权所有 {{ config.companyName }}
    </div>
  </div>

</template>

<script>
import LoginComponent from "./components/LoginComponent.vue";
import RegisterComponent from "./components/RegisterComponent.vue";
import config from "@/config/config";

export default {
  name: "Login",
  computed: {
    config() {
      return config
    }
  },
  components: {RegisterComponent, LoginComponent},
  data() {
    return {
      action: 'login',
      loading: false,
    }
  },
  created() {
  },
  mounted() {
    clearInterval('showNotice')

    // 判断是不是注册
    let query= this.$route.query;
    if(query && query.action === 'register'){
      // 跳转至注册
      this.action = 'register'
    }
  },
  methods: {

  }
}

</script>

<style scoped lang="scss">
.container {
  width: 100%;
  height: 100vh;
  box-sizing: border-box;

}

.main-container {
  width: 100%;
  height: 95vh;
  background: url('~@/assets/imgs/login.png') center bottom fixed repeat;
  background-size: 100%;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box{
  display: flex;
  justify-content: center;
  align-items: center;
}

//.empty-box, .login-box {
//  width: 50%;
//  height: 95vh;
//  display: flex;
//  justify-content: center;
//  align-items: center;
//  margin-left: 300px;
//  margin-top: 50px;
//}

.footer {
  text-align: center;
  width: 100%;
  height: 5vh;
  line-height: 5vh;
  display: flex;
  justify-content: center;
}

</style>
