import Vue from 'vue'
import App from './App.vue'
import store from './store';
import config from './config/config'
import request from "./utils/request";
import router from './router'
import ElementUI from 'element-ui';
import $ from "jquery/dist/jquery"
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/css/scroll.css'
import '@/assets/css/mobile-responsive.css' // 移动端适配样式
import "@/assets/iconfont/iconfont.css"
import * as echarts from 'echarts';
Vue.prototype.$echarts = echarts;
Vue.config.productionTip = false

Vue.use(ElementUI);

Vue.config.productionTip = false
// axios
Vue.prototype.$http = request
Vue.prototype.$ = $
// 后台地址配置
Vue.prototype.$config = config
// 消除Vue警告
Vue.config.productionTip = false;
// Vue.config.devtools = false;
Vue.config.silent = true;

Vue.config.devtools = true;

// 注册 v-random-color 指令
Vue.directive('random-color', {
  inserted(el) {
    if (!el.style.color) {
      const colors = ['#e74c3c', '#3498db', '#2ecc71', '#9b59b6', '#f39c12']
      el.style.color = colors[Math.floor(Math.random() * colors.length)]
    }
  }
})

new Vue({
  store,
  router,
  $,
  render: h => h(App),
}).$mount('#app')
