import Vue from 'vue'
import VueRouter from 'vue-router'
import manageRouters from "@/router/modules/manage";
import frontRouters from "@/router/modules/front";
import config from "@/config/config";
import {Message} from "element-ui";
import store from '@/store';

Vue.use(VueRouter)

const routes = [
  {
    path: "/",
    name: 'index',
    redirect: '/manage'
  },
  {
    path: "/login",
    name: 'login',
    component: () => import('@/views/login/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  ...manageRouters,
  ...frontRouters,
]

const router = new VueRouter({
  routes,
  mode: 'history',
  base: config.projectPrefix
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - ' +  config.projectName : config.projectName;

  let isLogin = localStorage.getItem("token");
  if (config.routesWhiteList.includes(to.name)) {
    if(store.getters.getUser === null || store.getters.getUser === {}){
      await store.dispatch('fetchUser');
    }
    let userRole = store.getters.getUser?.userRole
    if(userRole){
      if (userRole == 2 && (to.path.indexOf('manage') != -1)) {
        next({name: 'home'})
      }
      if (userRole != 2 && (to.path.indexOf('front') != -1)) {
        next({name: 'manageIndex'})
      }
    }
    next();
  } else if (!isLogin) {
    Message.error("用户未登录")
    next({name: "login"});
  } else {
    try {
      if(store.getters.getUser === null || store.getters.getUser === {}){
        await store.dispatch('fetchUser');
      }
      let userRole = store.getters.getUser.userRole
      if (userRole == 2 && (to.path.indexOf('manage') != -1)) {
        next({name: 'home'})
      }
      if (userRole != 2 && (to.path.indexOf('front') != -1)) {
        next({name: 'manageIndex'})
      }
      next();
    } catch (error) {
      Message.error("登录失效")
      next({name: "login"}); // 网络或后端错误也跳转登录页
    }
  }
})

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

export default router
