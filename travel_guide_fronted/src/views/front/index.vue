<template>
  <div style="width: 100%;">
    <div class="nav">
      <div class="navLeft">
        <div style="display: flex;align-items: center;line-height: 30px;cursor: pointer" @click="$router.push({name:'home'})">
          <div style="font-size: 20px;font-weight: bold;margin-left: 16px;" class="titleE">{{config.companyNameE}}</div>
          <div style="font-size: 20px;font-weight: bold;margin-left: 16px;"> {{ config.projectName }}</div>
        </div>
      </div>
      <div class="navRight">
        <div class="menuList">
          <el-menu
              :default-active="tabPosition"
              class="custom-el-menu"
              mode="horizontal"
              router
          >
            <template v-for="(item, index) in dataList">
              <el-submenu v-if="item.children" :index="item.path" :key="index">
                <template slot="title">
                  {{ item.name }}
                </template>
                <el-menu-item
                    v-for="(sub, subIdx) in item.children"
                    :key="subIdx"
                    :index="sub.path"
                >
                  {{ sub.name }}
                </el-menu-item>
              </el-submenu>

              <el-menu-item v-else :index="item.path" :key="index">
                <div class="menu">
                  <div class="textOne">{{ item.name }}</div>
                  <div class="textTwo">{{ item.name }}</div>
                </div>
              </el-menu-item>
            </template>
          </el-menu>
        </div>
        <div  style="display: flex;align-items: center;margin-right: 8px">
          <el-badge :hidden="msgNum <= 0" :value="msgNum" :max="99" style="display: flex;align-items: center">
            <i @click="goChat" class="el-icon-chat-line-square iconFrontStyle"></i>
          </el-badge>
        </div>
        <el-avatar v-if="userInfo.id" :size="36" :src="getPicUrlByJson(userInfo.headPicUrl,0)" style="margin-left: 16px"></el-avatar>
        <el-dropdown v-if="userInfo.id" trigger="hover" @command="handleCommand" @visible-change="handleDropdownChange"
                     placement="bottom">
          <span class="avatar-dropdown">
            <div class="name" :class="{ active: isDropdownOpen }" slot="reference">
              <div style="flex-shrink: 0">{{ userInfo.userName }}</div>
              <i class="el-icon-caret-bottom"></i>
            </div>
          </span>
          <el-dropdown-menu slot="dropdown" style="width: 170px;z-index: 9999;transform: translateX(-10px)">
            <el-dropdown-item :command="item.type" v-for="(item,index) in menuList" :key="index">
              <div class="icon-item">
                <i class="iconfont" v-if="item.type==='userInfo'">&#xe615;</i>
                <i class="iconfont" v-if="item.type==='changePassWord'">&#xe61a;</i>
                <i class="iconfont" v-if="item.type==='myApply'">
                  <i class="el-icon-s-promotion" style="font-size: 16px;width: 16px;margin: 0"></i>
                </i>
                <i class="iconfont" v-if="item.type==='myContest'">
                  <i class="el-icon-trophy" style="font-size: 16px;width: 16px;margin: 0"></i>
                </i>
                <span>{{ item.name }}</span>
              </div>
            </el-dropdown-item>
<!--            <el-dropdown-item command="toManage" v-if="userInfo.userRole != 0" divided>-->
<!--              <div style="color: #0274bd">-->
<!--                <i class="iconfont">&#xe638;</i>-->
<!--                <span>管理端</span>-->
<!--              </div>-->
<!--            </el-dropdown-item>-->
            <el-dropdown-item command="logout" divided>
              <div style="color: red">
                <i class="iconfont">&#xe7b0;</i>
                <span>退出登录</span>
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <div class="navRight" v-if="!userInfo.id">
          <button class="nav-btn primary"
                  @click="$router.push({ name: 'login', query: { action: 'login' } })">
            登录
          </button>

          <button class="nav-btn primary outline"
                  @click="$router.push({ name: 'login', query: { action: 'register' } })">
            注册
          </button>
        </div>
      </div>
    </div>
    <div class="content">
      <router-view/>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import router from "@/router";
import common from "@/utils/common";
export default {
  name: "frontIndex",
  mixins: [common],
  computed: {
    config() {
      return config
    },
  },
  watch: {
    $route: {
      handler(to) {
        this.tabPosition = to.path
      },
      immediate: true,
    },
  },
  data() {
    return {
      tabPosition: '/front/home',
      userInfo: {},
      isDropdownOpen: false,
      dataList: [
        { name: '首页', path: '/front/home' },
        { name: '景点查询', path: '/front/attractionList' },
        { name: '酒店列表', path: '/front/hotelInfoFront' },
        {
          name: '内容管理',
          path: 'content',
          children: [
            { name: '我的收藏', path: '/front/myCollect' },
            { name: '发布攻略', path: '/front/postReview' },
            { name: '我的攻略', path: '/front/myStrategyGuide' }
          ]
        },
        {
          name: '我的订单',
          path: 'orders',
          children: [
            { name: '门票订单', path: '/front/ticketOrder' },
            { name: '酒店订单', path: '/front/hotelOrderFront' }
          ]
        },
        { name: '攻略专区', path: '/front/strategyPlaza' }
      ],
      menuList: [
        {name: '个人信息', type: 'userInfo'},
        {
          name: '修改密码',
          type: 'changePassWord'
        }],
      passwordVisible: false,
      msgNum: 0
    }
  },
  mounted() {
    this.getCurrentUser();
    this.getMsgNum()
    if(config.msgNumFlag){
      this.startPolling()
    }
  },
  beforeDestroy() {
    this.stopPolling()
  },
  methods: {
    startPolling() {
      if (this.timer) return
      this.timer = setInterval(() => {
        this.getMsgNum()
      }, 5000)
    },
    stopPolling() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    },
    getMsgNum(){
      request({
        url: config.backHost + "/chatRoom/myWaitCount"
      }).then(res => {
        this.msgNum = res.data
      })
    },
    goChat(){
      request({
        url: config.backHost + "/chatRoom/myRoom"
      }).then(res => {
        if (res.code == 200) {
          if(res.data.length>0){
            this.$router.push({name:'frontChatRoom',query:{roomId:res.data[0].id}})
          } else {
            this.$router.push({name:'frontChatRoom'})
          }
        }
      })
    },
    router() {
      return router
    },
    handleDropdownChange(visible) {
      this.isDropdownOpen = visible;
    },
    goPath(val) {
      this.$router.push(val)
    },
    getCurrentUser() {
      request({
        url: config.backHost + "/user/getCurrentUser"
      }).then(res => {
        if (res.code == 200) {
          this.userInfo = res.data;
          // if (this.userInfo.id) {
          //   request({
          //     url: config.backHost + "/goodsCart/myCart",
          //   }).then(res => {
          //     this.shopCarNum = res.data.length;
          //   })
          // }
        }
      })
    },
    handleCommand(item) {
      switch (item) {
        case 'userInfo':
          this.$router.push({name: "userInfo"})
          break
        case 'changePassWord':
          this.$router.push({name: "changePassWord"})
          break
        case 'logout':
          this.logout();
          break
      }
    },
    logout() {
      this.$confirm('确定退出系统？', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: config.backHost + "/user/logout",
        }).then(res => {
          if (res.code == 200) {
            this.$message({
              type: 'success',
              message: '退出登录成功!'
            });
            localStorage.removeItem("token");
            this.userInfo = {}

            this.$router.push({name: 'home'})
            window.location.reload();
          } else {
            this.$message.error(res.msg);
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消退出'
        });
      });
    },
  }
}
</script>

<style scoped lang="scss">
.menuList{
  display: flex;
  .menu{
    border-top: 25px solid transparent;
    border-bottom: 25px solid transparent;
    border-left: 0px solid transparent;
    border-right: 0px solid transparent;
    font-size: 16px;
    cursor: pointer;
    //color: #16161a;
    //background-color: #fff;
    font-weight: 400;
    position: relative;
    line-height: 22px;
    overflow: hidden;
    height: 22px;
    transition: transform .3s linear, background-color .3s linear;
    .textOne,
    .textTwo {
      transition: transform .3s linear;
    }
  }
}
.iconFrontStyle{
  font-size: 30px;
  margin-left: 12px;
  color: #8f6537;
  cursor: pointer;
}
.nav {
  height: $nav-height;
  width: 100%;
  background: $nav-background;
  padding: 0 10%;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  color: $nav-color !important;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 100;
  .navLeft {
    display: flex;
    align-items: center;
    height: 100%;
  }

  .navRight {
    display: flex;
    align-items: center;
    height: 100%;
    $nav-img-width: 30px;
    font-size: 14px;
    color: $nav-color !important;
    transition: all 0.3s;

    .el-dropdown {
      color: $nav-color !important;
    }

    .navRightIcon {
      width: $nav-img-width;
      height: 30px;
      cursor: pointer;
    }

    .navRightIcon:hover {
      transform: scale(1.2);
    }

    .name {
      margin-left: 8px;
      display: flex;
      cursor: pointer;
      align-items: center;
      line-height: 22px;

      .el-icon-caret-bottom {
        transition: transform .3s;
        margin-left: 4px;
      }
    }

    .name:hover .el-icon-caret-bottom,
    .name.active .el-icon-caret-bottom {
      transform: rotate(180deg);
    }
  }
}

.content {
  height: calc(100vh - $nav-height);
  background-color: $pageBack;
  overflow-y: auto;
}

.el-radio-group {
  margin-bottom: 0 !important;
  height: 100%;
}

::v-deep {
  .el-radio-button--small .el-radio-button__inner {
    border-radius: 0 !important;
  }

  .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    border: none;
    box-shadow: none;
  }

  .el-radio-button:first-child .el-radio-button__inner {
    border-left: none;
  }

  .el-radio-button {
    height: 100%;
    border: none;
  }

  .navLeft {
    .el-radio-button__inner {
      height: 100%;
      display: flex;
      align-items: center;
      padding: 9px 34px;
      border: none;
      font-size: 16px;
      transition: border 0s;
      background: transparent !important;
      color: $nav-color;
    }

    .el-radio-button__inner:hover {
      color: $radioSel-color !important;
    }

    .el-radio-button.is-active .el-radio-button__inner {
      border-bottom: 2px solid $radioSel-color !important;
      font-weight: bold;
      background-color: transparent !important;
      color: $radioSel-color !important;
    }

    .el-radio-button.is-active .el-radio-button__inner:hover {
      color: $radioSel-color !important;
    }
  }

  .el-radio-button__inner {
    height: 100%;
    display: flex;
    align-items: center;
    padding: 9px 34px;
    border: none;
    font-size: 16px;
    transition: border 0s;
    background-color: transparent !important;
    color: #606266;
  }

  .el-radio-button__inner:hover {
    color: $color !important;
  }

  .el-radio-button.is-active .el-radio-button__inner {
    border-bottom: 2px solid $color !important;
    font-weight: bold;
    color: $color !important;
  }
}

::v-deep {
  .fontColor {
    color: $color !important;
  }

  .el-checkbox__input.is-checked + .el-checkbox__label {
    color: $color !important;
  }

  .el-checkbox__input.is-checked .el-checkbox__inner {
    background-color: $color !important;
    border-color: $color !important;
  }

  .el-checkbox__inner:hover {
    border-color: $color !important;
  }

  .el-checkbox__input.is-focus .el-checkbox__inner {
    border-color: $color !important;
  }
}
.nav-btn {
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  outline: none;
  transition: all 0.2s ease;
  margin-left: 12px;
  line-height: 20px;
}

.nav-btn.primary {
  background-color: $radioSel-color;
  color: #fff;
  padding: 8px 18px;
}

.nav-btn.primary:hover {
  transform: translateY(-2px);
}

.nav-btn.primary:active {
  transform: translateY(0);
}

.nav-btn.outline {
  padding: 6px 16px;
  background-color: transparent;
  color: $radioSel-color;
  border: 2px solid $radioSel-color;
  box-shadow: none;
}

.nav-btn.outline:hover {
  transform: translateY(-2px);
}
@media (max-width: 1466px) {
  .nav{
    flex-direction: column;
    height: 146px;
    align-items: center;
  }
  .content{
    height: calc(100vh - 146px);
  }
  .navLeft{
    height: 60px;
  }
}
@media (max-width: 1116px) {
  .nav{
    padding: 0;
  }
}
.el-dropdown {
  height: 100%;
  display: flex;
  align-items: center;
}

.menu {
  .el-icon-arrow-down {
    font-size: 12px;
    margin-left: 4px;
    transition: transform 0.3s;
  }
}

.el-dropdown:hover .el-icon-arrow-down {
  transform: rotate(180deg);
}

.menu.active {
  background-color: $theme-colorFront;
  .textOne, .textTwo {
    color: #fff !important;
  }
}
.navRight {
  display: flex;
  align-items: center;
  height: 100%;

  .custom-el-menu {
    border-bottom: none !important; // 去掉底部分割线
    background-color: transparent !important;
    height: 100%;
    display: flex;
    align-items: center;
    ::v-deep .el-menu-item {
      height: $nav-height !important;
      line-height: $nav-height !important;
      border-bottom: none !important;
      font-size: 16px;
      color: #16161a;
      padding: 0 20px;
      margin: 0 5px;
      transition: all 0.3s;

      &:hover .textOne{
        color: $theme-colorFront;
        font-weight: 600;
        transform: translate3d(0, -100%, 0);
      }
      &:hover .textTwo{
        color: $theme-colorFront;
        font-weight: 600;
        transform: translate3d(0, -100%, 0);
      }
      &:hover .textThree{
        color: $theme-colorFront;
        font-weight: 600;
      }
      &.is-active{
        color: #fff !important;
        background-color: $theme-colorFront !important;
        font-weight: 600;
        .textTwo,.textOne{
          color: #fff !important;
        }
      }
      &.is-active:after{
        display: none;
      }
      &:hover {
        .textOne{
          color: $theme-color !important;
        }
        .textTwo{
          color: $theme-color !important;
        }
      }
    }
    .el-menu-item:hover{
      background: $side-bar-container-hoverBackColor !important;
    }
    .custom-el-menu.active:hover{
      color: #fff !important;
    }
    ::v-deep .el-submenu {
      .el-submenu__title {
        height: $nav-height !important;
        line-height: $nav-height !important;
        border-bottom: none !important;
        font-size: 16px;
        color: #16161a;
        &:hover {
          background-color: rgba($theme-colorFront, 0.1) !important;
          color: $theme-color !important;
          .el-submenu__icon-arrow.el-icon-arrow-down{
            color: $theme-color !important;
          }
        }
      }

      &.is-active .el-submenu__title {
        color: #fff !important;
        background: $theme-color !important;
        font-weight: 600;
        .el-submenu__icon-arrow.el-icon-arrow-down{
          color: #fff;
        }
      }
    }
    ::v-deep .el-submenu.is-active:hover{
      .el-submenu__title{
        color: $theme-color !important;
        background: $side-bar-container-hoverBackColor !important;
      }
    }
  }
}
</style>
