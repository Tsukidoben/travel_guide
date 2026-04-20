<template>
  <!--管理端侧边栏-->
  <div class="own-nav-bar-box">
    <div class="own-nav-bar-left-box">
      <own-tabs-bar/>
    </div>
    <div class="own-nav-bar-right-box" v-if="userInfo">
      <change-password ref="passwordPage" :password-visible.sync="passwordVisible"></change-password>

      <div  style="margin-right: 20px;display: flex;align-items: center" v-if="userInfo.userRole != -1">
        <el-badge :hidden="msgNum <= 0" :value="msgNum" :max="99" style="display: flex;align-items: center">
          <i @click="goChat" class="el-icon-chat-line-square iconFrontStyle"></i>
        </el-badge>
      </div>

      <el-dropdown trigger="click"
                   placement="bottom" @command="handleCommand"
                   @visible-change="dropdownClick">

          <span class="avatar-dropdown">
            <el-avatar size="medium" :src="getPicUrlByJson(userInfo.headPicUrl,0)"></el-avatar>
            <span class="userName" style="font-size: 1.0rem;margin-left: 5px">{{ userInfo.userName }}</span>
          </span>
        <el-dropdown-menu slot="dropdown" style="width: 170px;z-index: 9999;transform: translateX(-10px)">
          <el-dropdown-item command="personalInfo">
            <div class="icon-item">
              <i class="el-icon-user-solid"></i>
              <span>个人信息</span>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="changePassword">
            <div class="icon-item">
              <i class="el-icon-edit"></i>
              <span>修改密码</span>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="logout" divided>
            <div style="color: red">
              <i class="el-icon-s-unfold"></i>
              <span>退出登录</span>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <el-divider direction="vertical"></el-divider>

      <!--<span style="margin-left: 10px;"><i style="font-size: 20px;cursor: pointer" title="前台"
                                          class="el-icon-s-home text"
                                          @click="$router.push('/front')"></i></span>-->
      <span style="margin-left: 10px;color: red"><i style="font-size: 20px;cursor: pointer" title="退出"
                                                    @click="logout"
                                                    class="el-icon-s-unfold"></i></span>
    </div>
  </div>
</template>

<script>

import OwnTabsBar from "@/views/manage/components/OwnTabsBar.vue";
import request from "@/utils/request";
import config from "@/config/config";
import ChangePassword from "@/views/manage/userInfo/ChangePassword.vue";
import Common from "@/utils/common";

export default {
  name: "OwnNavBar",
  mixins: [Common],
  components: { ChangePassword, OwnTabsBar},
  data() {
    return {
      passwordVisible: false,
      qrCodeDialog: false,
      msgNum:0,
      timer: null,
    }
  },
  computed: {
    userInfo() {
      return this.$store.getters.getUser;
    }
  },
  mounted() {
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
        url: config.backHost + "/chatRoom/manageWaitCount"
      }).then(res => {
        this.msgNum = res.data
      })
    },
    goChat(){
      request({
        url: config.backHost + "/chatRoom/myRoomManage"
      }).then(res => {
        if (res.code == 200) {
          if(res.data.length>0){
            this.$router.push({name:'manageChatRoom',query:{roomId:res.data[0].id}})
          } else {
            this.$router.push({name:'manageChatRoom'})
          }
        }
      })
    },
    handleCommand(item) {
      switch (item) {
        case 'personalInfo':
          this.$router.push({name:"userInfos"})
          break
        case 'logout':
          this.logout();
          break
        case 'changePassword':
          this.passwordVisible = true;
          break
      }
    },
    dropdownClick() {

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
            this.$store.dispatch('logout');
            this.$router.replace({
              path: '/login',
            });
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
  $right-box-width: 500px;
  $left-box-width: calc(100% - 300px);

  div {
    box-sizing: border-box;
  }

  .own-nav-bar-box {
    width: 100%;
    height: 100%;
    background: $nav-bar-background;
    padding: 0 20px;
    box-shadow: $base-box-shadow;
    display: flex;
    justify-content: space-between;

    .own-nav-bar-left-box {
      width: $left-box-width;
      height: 100%;
      line-height: 100%;
      text-align: left;
      display: flex;
      align-items: center;

      i {
        color: $nav-bar-color;
      }
    }

    .own-nav-bar-right-box {
      width: $right-box-width;
      height: 100%;
      display: flex;
      justify-content: right;
      align-items: center;
      margin-right: 70px;
      .avatar-dropdown {
        display: flex;
        align-items: center;
        cursor: pointer;
      }

      .el-dropdown {
        height: 100%;
        display: flex;
        align-items: center;
        padding: 10px;
      }

      .userName {
        font-size: 1.0rem;
        margin-left: 10px;
        color: $nav-bar-color;
      }

      .text {
        color: $nav-bar-color;
      }

      .el-dropdown:hover {
        background: $nav-user-hover-background-color;

        .userName {
          color: $nav-user-hover-color !important;
        }
      }
    }
  }
  .iconFrontStyle{
    font-size: 30px;
    margin-left: 12px;
    color: #8f6537;
    cursor: pointer;
  }
</style>

