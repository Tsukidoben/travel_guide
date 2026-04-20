<template>
  <div class="profile-container" v-loading="loading">
    <div class="profile-card">
      <aside class="sidebar-identity">
        <div class="avatar-uploader" @click="triggerUpload">
          <div class="img-wrapper">
            <img :src="getPicUrlByJson(userInfo.headPicUrl,0)" alt="avatar">
            <div class="hover-mask">
              <i class="el-icon-camera"></i>
              <span>更换头像</span>
            </div>
          </div>
        </div>

        <div class="account-brief">
          <div class="info-row">
            <label>学号</label>
            <p>{{userInfo.userAccount}}</p>
          </div>
          <div class="info-row">
            <label>注册日期</label>
            <p>{{timestampToYMDHMS(userInfo.createTime).split(' ')[0]}}</p>
          </div>
        </div>

        <input type="file" ref="fileInput" accept="image/*" style="display: none" @change="handleFileChange" />
      </aside>

      <main class="main-form">
        <header class="form-header">
          <div class="title-meta">
            <h1>个人资料详情</h1>
            <span>PROFILE DETAILS</span>
          </div>
        </header>

        <div class="inputs-container">
          <div class="input-group">
            <label><i class="el-icon-user"></i> 姓名</label>
            <el-input v-model="userInfo.userName" placeholder="请输入真实姓名" class="custom-input"></el-input>
          </div>

          <div class="input-group">
            <label><i class="el-icon-male"></i> 性别</label>
            <el-select v-model="userInfo.userSex" placeholder="请选择性别" class="custom-input">
              <el-option v-for="item in userSexOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </div>

          <div class="input-group">
            <label><i class="el-icon-phone-outline"></i> 联系电话</label>
            <el-input v-model="userInfo.userPhone" placeholder="请输入手机号码" maxlength="11" class="custom-input"></el-input>
          </div>
        </div>

        <footer class="form-footer">
          <button class="submit-btn" @click="editInfo">
            保存修改并同步
          </button>
        </footer>
      </main>
    </div>
  </div>
</template>

<script>
import common from "@/utils/common";
import request from "@/utils/request";
import config from "@/config/config";

export default {
  name: "UserProfile",
  mixins: [common],
  data() {
    return {
      userInfo: {},
      editUrl: "/user/saveOrUpdate",
      loading: false,
      userSexOptions: [{"value": "1", "label": "男"}, {"value": "0", "label": "女"}],
    }
  },
  mounted() {
    this.getUserInof()
  },
  methods: {
    triggerUpload() { this.$refs.fileInput.click(); },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (!file) return;
      const formData = new FormData();
      formData.append('file', file);
      formData.append('sysName', 'contest');
      formData.append('busType', 'user');
      this.uploadImage(formData);
    },
    uploadImage(file) {
      this.loading = true;
      request({ url: config.uploadUrl, data: file }).then(res => {
        this.$message.success('头像上传成功');
        this.userInfo.headPicUrl = JSON.stringify(res.data);
        this.userInfo = JSON.parse(JSON.stringify(this.userInfo));
      }).finally(() => { this.loading = false });
    },
    editInfo() {
      if (!this.userInfo.userName) { this.$message.warning('请填写姓名'); return }
      if (!this.userInfo.userSex) { this.$message.warning('请选择性别'); return }
      if (!this.userInfo.userPhone) { this.$message.warning('请填写电话'); return }
      if (!/^1[3-9]\d{9}$/.test(this.userInfo.userPhone)) {
        this.$message.warning('请输入正确的11位手机号码');
        return;
      }
      request({
        url: config.backHost + this.editUrl,
        data: {
          "id": this.userInfo.id,
          "headPicUrl": this.userInfo.headPicUrl,
          "userName": this.userInfo.userName,
          "userSex": this.userInfo.userSex,
          "userPhone": this.userInfo.userPhone,
          "userRole": 2
        }
      }).then((res) => {
        this.$message.success('修改成功');
        this.getUserInof();
      })
    },
    getUserInof() {
      request({ url: config.backHost + "/user/getCurrentUser" }).then(res => {
        this.userInfo = res.data;
      })
    }
  }
}
</script>

<style scoped lang="scss">
$theme-dark: $theme-color;
$text-light: #909399;

.profile-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
}

.profile-card {
  display: flex;
  width: 100%;
  max-width: 900px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #edf2f7;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.sidebar-identity {
  width: 280px;
  background: #f8fafc;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-right: 1px solid #edf2f7;

  .status-tag {
    font-size: 11px;
    font-weight: bold;
    color: #fff;
    background: $theme-dark;
    padding: 3px 12px;
    border-radius: 4px;
    margin-bottom: 30px;
    letter-spacing: 1px;
  }

  .avatar-uploader {
    cursor: pointer;
    margin-bottom: 30px;
    .img-wrapper {
      width: 150px;
      height: 150px;
      border-radius: 50%;
      border: 4px solid #fff;
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
      overflow: hidden;
      position: relative;
      img { width: 100%; height: 100%; object-fit: cover; }
      .hover-mask {
        position: absolute; top: 0; left: 0; width: 100%; height: 100%;
        background: rgba(31, 45, 61, 0.7);
        display: flex; flex-direction: column; align-items: center; justify-content: center;
        color: #fff; opacity: 0; transition: 0.3s;
        i { font-size: 20px; margin-bottom: 5px; }
        span { font-size: 12px; }
      }
      &:hover .hover-mask { opacity: 1; }
    }
  }

  .account-brief {
    width: 100%;
    .info-row {
      margin-bottom: 15px;
      label { font-size: 11px; color: $text-light; display: block; margin-bottom: 3px; }
      p { font-size: 14px; color: $theme-dark; font-weight: 500; word-break: break-all; }
    }
  }

  .official-stamp {
    margin-top: auto;
    border: 2px dashed #cbd5e1;
    color: #cbd5e1;
    padding: 8px 15px;
    font-weight: 900;
    font-size: 12px;
    border-radius: 4px;
    transform: rotate(-5deg);
  }
}
.main-form {
  flex: 1;
  padding: 40px 50px;
  display: flex;
  flex-direction: column;

  .form-header {
    margin-bottom: 20px;
    h1 { font-size: 22px; color: $theme-dark; margin: 0; font-weight: 700; }
    span { font-size: 12px; color: $text-light; font-family: monospace; }
  }

  .inputs-container {
    .input-group {
      margin-bottom: 25px;
      label {
        display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 10px;
        i { color: $theme-dark; margin-right: 5px; }
      }
    }
  }
}

::v-deep .custom-input {
  .el-input__inner {
    height: 48px;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    background-color: #fcfcfc;
    &:focus { border-color: $theme-dark; }
  }
}

.form-footer {
  margin-top: auto;
  .submit-btn {
    width: 100%;
    height: 54px;
    background: $theme-dark;
    color: #fff;
    border: none;
    border-radius: 8px;
    font-weight: bold;
    font-size: 15px;
    cursor: pointer;
    transition: 0.3s;
    &:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(31, 45, 61, 0.2); }
    &:active { transform: translateY(0); }
  }
}

::v-deep .el-select { width: 100%; }
</style>
