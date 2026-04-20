<template>
  <div class="personal-container">
    <div class="personal-card">
      <div class="profile-sidebar">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <upload-image-signel v-model="userInfo.headPicUrl"/>
          </div>
        </div>
        <div class="profile-footer-info" v-if="userInfo.createTime">
          <p><i class="el-icon-date"></i> 注册于 {{ timestampToYMDHMS(userInfo.createTime) }}</p>
        </div>
        <div class="profile-footer-info" v-if="userInfo.lastLoginTime">
          <p><i class="el-icon-date"></i> 最后登录于 {{ timestampToYMDHMS(userInfo.lastLoginTime) }}</p>
        </div>
      </div>

      <div class="profile-content">
        <div class="content-header">
          <span class="title">个人信息设置</span>
          <span class="subtitle">管理您的个人资料及账号安全</span>
        </div>

        <div class="form-body">
          <div class="form-item">
            <label v-if="userInfo.userRole == 1">账号</label>
            <label v-if="userInfo.userRole == 3">工号</label>
            <div class="readonly-value">{{ userInfo.userAccount }}</div>
          </div>

          <div class="form-item">
            <label>姓名</label>
            <el-input
                v-model="userInfo.userName"
                placeholder="请输入您的姓名"
                prefix-icon="el-icon-user"
                clearable
            ></el-input>
          </div>
          <div class="form-item">
            <label>性别</label>
            <el-select
                style="width: 100%;"
                class="custom-select"
                v-model="userInfo.userSex"
                placeholder="性别">
              <i slot="prefix"  style="margin-top: 13px;margin-left: 6px" class="el-icon-female"></i>
              <el-option v-for="item in userSexOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </div>
          <div class="form-item">
            <label>联系电话</label>
            <el-input type="text" v-model="userInfo.userPhone" show-word-limit maxlength="11" style="width: 100%"  placeholder="请输入联系电话"/>
          </div>
        </div>

        <div class="form-actions">
          <el-button
              type="primary"
              icon="el-icon-check"
              class="save-btn"
              :loading="loading"
              @click="editInfo"
          >保存修改</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import common from "@/utils/common";
import request from "@/utils/request";
import config from "@/config/config";
import UploadImageSignel from "@/components/UploadImageSignel.vue";
export default {
  name: "personalInfo",
  components: {UploadImageSignel},
  mixins: [common],
  data() {
    return {
      editUrl: "/user/saveOrUpdate",
      loading: false,
      userInfo: {},
      userSexOptions: [{"value":"1","label":"男"},{"value":"0","label":"女"}],
    }
  },
  mounted() {
    this.getUser()
  },
  methods: {
    getUser(){
      request({
        url: config.backHost + '/user/getCurrentUser',
      }).then((res) => {
        if (res.code == 200) {
          this.userInfo = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    editInfo() {
      if (!this.userInfo.userName) {
        this.$message.warning('请填写姓名')
        return
      }
      if (!this.userInfo.userSex) {
        this.$message.warning('请选择性别')
        return
      }
      if (!this.userInfo.userPhone) {
        this.$message.warning('联系电话不能为空')
        return
      } else {
        const phoneReg = /^1[3-9]\d{9}$/;
        if (!phoneReg.test(this.userInfo.userPhone)) {
          this.$message.warning('联系电话格式不正确')
          return
        }
      }
      request({
        url: config.backHost + this.editUrl,
        data: {
          "headPicUrl": this.userInfo.headPicUrl,
          "id": this.userInfo.id,
          "updateTime": new Date().getTime(),
          "userPhone":this.userInfo.userPhone,
          "userName": this.userInfo.userName,
          "userRole": this.userInfo.userRole,
          "userSex": this.userInfo.userSex
        }
      }).then((res) => {
        if (res.code == 200) {
          this.$message.success('修改成功')
          this.$store.dispatch('fetchUser');
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    validatePhone(phone) {
      const phoneRegex = /^1[3-9]\d{9}$/;
      return phoneRegex.test(phone);
    },
    isIdCard(value) {
      const idCardReg = /^[1-9]\d{5}(18|19|20)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}(\d|X|x)$/;
      if (!idCardReg.test(value)) {
        return true
      }
      return false
    },
  }
}
</script>

<style scoped lang="scss">
.personal-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;
  height: 100%;
}

.personal-card {
  display: flex;
  width:100%;
  height: auto;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #ebeef5;
}
.profile-sidebar {
  width: 300px;
  background: linear-gradient(180deg, #f8faff 0%, #ffffff 100%);
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 20px;

  .avatar-section {
    text-align: center;
    .avatar-wrapper {
      margin-bottom: 20px;
      padding: 5px;
      //background: #fff;
      //border-radius: 50%;
      //box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      transition: transform 0.3s ease;
      &:hover {
        transform: scale(1.02);
      }
    }
    .user-display-name {
      margin: 15px 0 5px;
      font-size: 20px;
      color: #303133;
    }
  }

  .profile-footer-info {
    color: #909399;
    font-size: 13px;
    i { margin-right: 5px; }
  }
}
.profile-content {
  flex: 1;
  padding: 40px 50px;

  .content-header {
    margin-bottom: 35px;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 15px;
    .title {
      display: block;
      font-size: 22px;
      font-weight: 600;
      color: #303133;
    }
    .subtitle {
      font-size: 13px;
      color: #999;
    }
  }

  .form-body {
    .form-item {
      margin-bottom: 25px;
      label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #606266;
        font-size: 14px;
      }
      .readonly-value {
        padding: 8px 15px;
        background-color: #f5f7fa;
        border-radius: 4px;
        color: #606266;
        font-size: 14px;
        border: 1px solid #e4e7ed;
      }
      .disabled-text {
        color: #c0c4cc;
      }
    }
  }

  .form-actions {
    margin-top: 40px;
    .save-btn {
      width: 140px;
      height: 40px;
      font-size: 15px;
      border-radius: 20px;
      box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
    }
  }
}
@media (max-width: 850px) {
  .personal-card {
    flex-direction: column;
    width: 100%;
  }
  .profile-sidebar {
    width: 100%;
    padding: 30px 20px;
  }
}
</style>

