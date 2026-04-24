<template>
  <!--管理端侧边栏-->
  <div class="own-side-bar-box">
    <div class="own-icon-box" :class="{'is-collapse': isCollapse}">
      <a :href="config.mvueUrl" style="display: flex;align-items: center;justify-content: center;height: 100%;text-decoration: none;">
        <img :src="iconMinUrl" alt="logo" style="height: 42px">
        <div class="logoTitle" v-if="!isCollapse" style="font-size: 20px;font-weight: bold;margin-left: 8px">{{config.logoTitle}}</div>
      </a>
    </div>
    <el-scrollbar
        class="side-bar-container"
        :class="{'is-collapse': isCollapse}"
        :collapse="isCollapse"
        :collapse-transition="false"
    >
      <el-menu :default-active="activeMenu"
               :collapse-transition="false"
               :class="{'is-collapse': isCollapse}"
               class="own-el-menu-vertical"
               :collapse="isCollapse">
        <el-menu-item index="manageHome" @click="$router.push({ name: 'manageHome' })">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="dataStatisticsOne" v-if="userInfo.userRole == 1" @click="$router.push({ name: 'dataStatisticsOne'})">
          <i class="el-icon-s-data"></i>
          <span slot="title">数据统计</span>
        </el-menu-item>
        <el-menu-item index="dataStatisticsTwo" v-if="userInfo.userRole != 1" @click="$router.push({ name:'dataStatisticsTwo' })">
          <i class="el-icon-s-data"></i>
          <span slot="title">数据统计</span>
        </el-menu-item>
        <el-submenu index="3c71c63f" v-if="userRole == '1'">
          <template slot="title">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </template>
          <el-menu-item v-if="userRole == '1'" index="userManage" @click="toMenu('userManage')">
            <i class="el-icon-user"></i>
            用户管理
          </el-menu-item>
        </el-submenu>
        <el-submenu index="3f5b1a6b" v-if="userRole == '1' || userRole == '3'">
          <template slot="title">
            <i class="el-icon-menu"></i>
            <span>业务管理</span>
          </template>
          <el-menu-item v-if="userRole == '1'" index="attractionType" @click="toMenu('attractionType')">
            <i class="el-icon-c-scale-to-original"></i>
            分类管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="attractionInfo" @click="toMenu('attractionInfo')">
            <i class="el-icon-bangzhu"></i>
            景点管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="ticketInfo" @click="toMenu('ticketInfo')">
            <i class="el-icon-s-ticket"></i>
            门票管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="attractionOrder" @click="toMenu('attractionOrder')">
            <i class="el-icon-tickets"></i>
            景点订单
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="tripStrategy" @click="toMenu('tripStrategy')">
            <i class="el-icon-edit-outline"></i>
            攻略管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="foodCategory" @click="toMenu('foodCategory')">
            <i class="el-icon-s-grid"></i>
            小吃分类管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="foodInfo" @click="toMenu('foodInfo')">
            <i class="el-icon-food"></i>
            小吃信息管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="foodShop" @click="toMenu('foodShop')">
            <i class="el-icon-office-building"></i>
            小吃店铺管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '1'" index="foodComment" @click="toMenu('foodComment')">
            <i class="el-icon-chat-dot-round"></i>
            小吃评论管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '3'" index="hotelInfo" @click="toMenu('hotelInfo')">
            <i class="el-icon-s-home"></i>
            酒店管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '3'" index="hotelRoom" @click="toMenu('hotelRoom')">
            <i class="el-icon-school"></i>
            房间管理
          </el-menu-item>
          <el-menu-item v-if="userRole == '3'" index="hotelOrder" @click="toMenu('hotelOrder')">
            <i class="el-icon-document"></i>
            酒店订单
          </el-menu-item>
        </el-submenu>
        <el-submenu index="0d6d99cb" v-if="userRole == '1'">
          <template slot="title">
            <i class="el-icon-s-tools"></i>
            <span>系统设置</span>
          </template>
          <el-menu-item v-if="userRole == '1'" index="carouselImage" @click="toMenu('carouselImage')">
            <i class="el-icon-picture-outline"></i>
            轮播图管理
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </el-scrollbar>

    <div class="own-side-action-box">
      <i  @click="handleCollapse" style="cursor: pointer;font-size: 25px" :class="!isCollapse ? 'el-icon-s-fold' : 'el-icon-s-unfold'"></i>
    </div>
  </div>
</template>

<script>
import config from "@/config/config";
import request from "@/utils/request";
export default {
  name: "OwnSideBar",
  props:{
    isCollapse:{
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      iconMinUrl: require("@/assets/imgs/min-logo.png"),
    }
  },
  computed:{
    activeMenu(){
      if(this.nowRoute.name){
        return this.nowRoute.name
      }else{
        return '';
      }
    },
    userRole() {
      const user = this.$store.getters.getUser;
      return user ? user.userRole : null;
    },
    config() {
      return config
    },
    nowRoute(){
      return this.$route;
    },
    userInfo() {
      return this.$store.getters.getUser || {};
    }
  },
  methods:{
    toMenu(name){
      this.$router.push({ name: name });
    },
    handleCollapse(){
      this.$emit("update:isCollapse",!this.isCollapse)
    },
  }
}
</script>

<style scoped lang="scss">
        $own-side-bar-box-height: calc(100vh);
        $own-side-bar-action-height: 50px;

  div {
    box-sizing: border-box !important;
    transition: $base-transition;
  }

  .own-side-bar-box {
    height: $own-side-bar-box-height;
    box-shadow: 2px 0 4px rgba(0, 21, 41, .35);
    //background: $side-bar-background;
    text-align: left;
    overflow: hidden;

    &.is-collapse {
      width: $base-left-menu-width-min;
    }

    .own-icon-box {
      background: $side-bar-background;
      width: 100%;
      height: $icon-height;
      box-shadow: $base-box-shadow;
      padding: 2px;
      text-align: center;

      &.is-collapse {
        width: $base-left-menu-width-min;
      }

      .logoTitle {
        color: $side-bar-color;
      }
    }

    .own-side-action-box {
      height: $own-side-bar-action-height;
      line-height: $own-side-bar-action-height;
      width: $base-left-menu-width;
      padding: 0 20px;
      overflow: hidden;
      background: $side-bar-background;
      border-top: 1px solid rgba(0, 0, 0, .1);

      &.is-collapse {
        width: $base-left-menu-width-min;
      }
    }

    .side-bar-container {
      box-sizing: border-box;
      height: calc($own-side-bar-box-height - $icon-height - $own-side-bar-action-height);
      width: $base-left-menu-width;
      background: $side-bar-background;

      &.is-collapse {
        width: $base-left-menu-width-min;
      }
    }

    .own-side-menu-empty-box {
      height: calc($own-side-bar-box-height - $icon-height - $own-side-bar-action-height);
      width: $base-left-menu-width;
      background: $side-bar-background;
      cursor: pointer;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      //writing-mode: lr-tb;
      font-size: 20px;

      &.is-collapse {
        width: $base-left-menu-width-min;
      }
    }

    .own-side-menu-empty-box:hover {
      background: $base-sub-menu-active-bg;
    }

    ::v-deep .own-el-menu-vertical.el-menu {
      height: 100% !important;
      background: $side-bar-background;
      border: none;
      overflow: hidden;

      &.is-collapse {
        width: $base-left-menu-width-min;

        span, .el-submenu__icon-arrow.el-icon-arrow-right {
          width: 0;
          height: 0;
          display: none !important;
        }

        i {
          transform: scale(1.3);
        }
      }

      .el-submenu {
        i {
          color: $side-bar-container-fontColor;
        }

        .el-submenu__title {
          color: $side-bar-container-fontColor !important;

          &:hover {
            background-color: $side-bar-container-hoverBackColor;
          }
        }
      }

      .el-menu-item {
        background: $side-bar-background;
        transition: all 0.3s ease-in-out;
        color: $side-bar-container-fontColor !important;

        i, span {
          color: $side-bar-container-fontColor;
        }

        &.is-active {
          background: $side-bar-container-isActiveBackColor !important;
          color: $side-bar-container-isActiveColor !important;
          font-weight: bold;

          i, span {
            color: $side-bar-container-isActiveColor !important;
          }
        }

        &:hover {
          background-color: $side-bar-container-hoverBackColor !important;
          color: $side-bar-container-hoverColor !important;
          i, span {
            color: $side-bar-container-hoverColor !important;
          }
        }
      }
    }

    .own-side-action-box {
      i{
        color: $side-bar-container-fontColor;
      }
    }
  }
</style>
