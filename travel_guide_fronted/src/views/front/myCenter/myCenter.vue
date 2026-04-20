<template>
  <div class="my-center-container">
    <div class="my-center-layout">
      <aside class="sidebar-wrapper">
        <div class="user-brief">
          <div class="avatar-box">
            <img style="width: 100%;height: 100%" :src="getPicUrlByJson(user.headPicUrl,0)" alt="">
          </div>
          <div class="text-group">
            <span class="label">TRAVEL-GUIDE</span>
            <div class="welcome-text">{{ user.userName }}</div>
          </div>
        </div>

        <div class="menu-divider"></div>

        <el-menu :default-active="active" class="custom-menu">
          <el-menu-item
              :index="item.pathName"
              v-for="(item, index) in menuList"
              :key="index"
              @click="$router.push({ name: item.pathName });"
          >
            <div class="menu-item-inner">
              <i :class="item.newIcon" class="icon-style-el"></i>
              <span class="menu-name">{{ item.name }}</span>
            </div>
            <div class="active-indicator"></div>
          </el-menu-item>
        </el-menu>

        <div class="sidebar-footer">
          <p>TRAVEL GUIDE SYSTEM v2.0</p>
        </div>
      </aside>

      <main class="content-wrapper">
        <div class="content-header">
          <div class="breadcrumb-trail">个人中心 / {{ currentMenuName }}</div>
        </div>

        <div class="view-viewport">
          <transition name="page-fade" mode="out-in">
            <router-view />
          </transition>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import common from "@/utils/common";
export default {
  name: "myCenter",
  mixins: [common],
  data() {
    return {
      menuList: [
        {name: '个人信息', pathName: 'userInfo', newIcon: 'el-icon-user-solid'},
        {name: '修改密码', pathName: 'changePassWord', newIcon: 'el-icon-key'},
      ],
      active: '',
    }
  },
  computed: {
    user() {
      return this.$store.getters.getUser || {};
    },
    currentMenuName() {
      const current = this.menuList.find(item => item.pathName === this.active);
      return current ? current.name : '中心主页';
    }
  },
  watch: {
    $route: {
      handler(to) {
        this.active = to.name;
      },
      immediate: true,
    }
  }
}
</script>

<style scoped lang="scss">
$theme-dark: $theme-color;
$theme-active-bg: rgba(31, 45, 61, 0.08);
$text-light: #909399;
$border-color: #e6e6e6;

.my-center-container {
  background-color: #f4f7f9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  box-sizing: border-box;
  height: 100%;
}

.my-center-layout {
  width: 1200px;
  display: flex;
  background: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
  height: 100%;
}

.sidebar-wrapper {
  width: 280px;
  background: #ffffff;
  border-right: 1px solid $border-color;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  .user-brief {
    padding: 20px 35px 15px;

    .avatar-box {
      width: 64px;
      height: 64px;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 15px;
      box-shadow: 0 8px 15px rgba(31, 45, 61, 0.2);
      overflow: hidden;
      .inner-circle {
        color: #fff;
        font-size: 26px;
        font-weight: bold;
      }
    }

    .text-group {
      .label {
        font-size: 10px;
        color: $text-light;
        letter-spacing: 1.5px;
        display: block;
        margin-bottom: 4px;
      }
      .welcome-text {
        font-size: 22px;
        font-weight: 700;
        color: $theme-dark;
      }
    }
  }
}

.menu-divider {
  height: 1px;
  background: radial-gradient(circle, #ddd 0%, transparent 100%);
  margin: 0 30px 20px;
}

.custom-menu {
  background: transparent !important;
  border: none !important;
  padding: 0 15px;
  flex: 1;

  ::v-deep {
    .el-menu-item {
      height: 54px;
      line-height: 54px;
      border-radius: 10px;
      margin-bottom: 5px;
      color: $text-light !important;
      transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      position: relative;

      .menu-item-inner {
        padding-left: 15px;
        display: flex;
        align-items: center;

        .icon-style-el {
          font-size: 18px;
          margin-right: 12px;
        }
        .menu-name {
          font-weight: 500;
        }
      }

      &:hover {
        background-color: $side-bar-container-hoverBackColor !important;
        color: $theme-dark !important;
        transform: translateX(5px);
        i{
          color: $theme-dark;
        }
      }

      &.is-active {
        background-color: $theme-dark !important;
        color:#ffffff !important;
        font-weight: bold;
        i{
          color: #ffffff;
        }
        .active-indicator {
          position: absolute;
          left: -15px;
          top: 15px;
          bottom: 15px;
          width: 4px;
          background: $theme-dark;
          border-radius: 0 4px 4px 0;
        }
      }
    }
  }
}

.sidebar-footer {
  padding: 30px;
  p {
    font-size: 10px;
    color: #ccc;
    margin-bottom: 4px;
    letter-spacing: 0.5px;
  }
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  width: calc(100% - 280px);
  box-sizing: border-box;
  .content-header {

    .breadcrumb-trail {
      font-size: 12px;
      color: $text-light;
      margin-bottom: 20px;
      text-transform: uppercase;
    }
    .page-title {
      font-size: 32px;
      font-weight: 800;
      color: $theme-dark;
      margin: 0;
    }
    .title-underline {
      margin-top: 12px;
      width: 50px;
      height: 4px;
      background: $theme-dark;
      border-radius: 2px;
    }
  }

  .view-viewport {
    flex: 1;
    overflow-y: auto;
    padding-right: 10px;

    &::-webkit-scrollbar { width: 4px; }
    &::-webkit-scrollbar-thumb { background: $theme-dark; border-radius: 10px; }
  }
}

.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.page-fade-enter {
  opacity: 0;
  transform: translateY(10px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
@media (max-width: 1366px) {
  .my-center-container{
    padding: 5px 40px;
  }
}
</style>
