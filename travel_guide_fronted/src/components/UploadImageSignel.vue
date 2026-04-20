<template>
  <div class="avatar-uploader-box">
    <el-upload
        class="avatar-uploader"
        :action="config.uploadUrl"
        :before-upload="beforeAvatarUpload"
        :show-file-list="false"
        :accept="accept"
        :headers="headers"
        :on-success="onSuccess"
    >

      <div style="display: flex;justify-content: space-between">
        <img v-loading="loading" v-if="imageUrl" :src="imageUrl" class="avatar" :class="{'circular': isCircular,'isSmall': isSmall}">
        <i v-loading="loading" :class="{'isSmall': isSmall}" v-else class="el-icon-plus avatar-uploader-icon"/>
        <div
            style="height: 100%;display: flex;margin-left: 20px;align-items: center;justify-content: right;font-size: 16px;color: #0077aa;"
            v-if="loading">图片上传中，请稍等...
        </div>
      </div>
    </el-upload>
    <span v-if="isShowTip" class="msg">支持格式：.jpg .png ，不能超过{{ fileMaxSize }}Mb</span>
  </div>
</template>
<script>
import config from "@/config/config";

export default {
  name: 'UploadImageSignel',
  computed: {
    config() {
      return config
    }
  },
  props: {
    isShowTip: {
      type: Boolean,
      default: true
    },
    isCircular: {
      type: Boolean,
      default: false
    },
    isSmall: {
      type: Boolean,
      default: false
    },
    accept: {
      type: String,
      default: '.jpg,.jpeg,.png,gif,JPG,JPEG,PNG,GIF'
    },
    fileMaxSize: {
      type: Number,
      default: 3
    },
    action: {
      type: String,
      default: ''
    },
    value: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      imageUrl: '',
      fileObj: null,
      headers: {
        'Authorization': '' + localStorage.getItem("token")
      },
      loading: false,
    }
  },
  // 监听
  watch: {
    value: {
      handler(newValue, oldValue) {
        if(newValue){
          console.log(newValue)
          if(newValue.indexOf("[{") != -1 && newValue.indexOf("}]")!=-1){
            // 转json
            this.imageUrl = config.downloadUrl + JSON.parse(newValue)[0].id
          }else{
            this.imageUrl = newValue
          }
        }else{
          this.imageUrl = ""
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    // 上传前检查
    beforeAvatarUpload(file) {
      const isLt2M = file.size / 1024 / 1024 / 1024 < this.fileMaxSize
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 ' + this.fileMaxSize + 'Mb')
      }
      this.loading = isLt2M;
      return isLt2M
    },
    // 上传成功
    onSuccess(response, file, fileList) {
      this.loading = false;
      if (response.code === 200) {
        if(response.data){
          let picArrays = [response.data[0]]
          this.$emit('input', JSON.stringify(picArrays))
        }else{
          this.$emit('input', '')
        }
      } else {
        this.$message.error(response.msg)
      }
    },
    // 预览图片
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    }
  }
}
</script>
<style scoped lang="scss">
.avatar-uploader-box .avatar-uploader .el-upload {
  border: 1px dashed #c0ccda;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  background-color: #fbfdff;
}

.avatar-uploader-box .avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-box .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 148px;
  height: 148px;
  line-height: 148px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: $base-border;


  &.isSmall {
    width: 100px;
    max-height: 80px;
    line-height: 100px;
  }
}

.avatar-uploader-box .avatar {
  width: 148px;
  height: 148px;
  display: block;
  border: $base-border;

  &.isSmall {
    width: 100px;
    max-height: 80px;
  }
}

.avatar-uploader-box .circular {
  width: 148px;
  height: 148px;
  display: block;
  border-radius: 74px;
  border: $base-border;

  &.isSmall {
    width: 100px;
    max-height: 80px;
    border-radius: 40px;
  }
}

.avatar-uploader-box .msg {
  font-size: 13px;
  color: #555;
}
</style>

