<template>
  <div style="text-align: left">
    <el-upload
        list-type="picture-card"
        :on-preview="handlePictureCardPreview"
        :on-remove="handleRemove"
        :multiple="false"
        :file-list="fileList"
        :headers="headers"
        :on-success="onSuccess"
        :accept="accept"
        :limit="9"
        :action="config.uploadUrl"
        :disabled="disabled"
        :before-upload="beforeUpload"
    >
      <i class="el-icon-plus" />
    </el-upload>
    <span class="msg">{{msg}} 支持格式：.jpg .png</span>
    <el-dialog :visible.sync="dialogVisible" :append-to-body="true">
      <img style="width:100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>
<script>
import config from "@/config/config";
import common from "@/utils/common";
export default {
  name: 'UploadImageMore',
  mixins:[common],
  computed: {
    config() {
      return config
    }
  },
  props: {
    msg:{
      type:String,
      default: ''
    },
    disabled:{
      type: Boolean,
      default: false
    },
    accept: {
      type: String,
      default: '.jpg,.jpeg,.png,gif,JPG,JPEG,PNG,GIF'
    },
    action: {
      type: String,
      default: ''
    },
    value: {
      type: String,
      default: ''
    },
    max: {
      type: Number,
      default: 9
    }
  },
  data() {
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      fileList: [],
      headers: {
        'Authorization': '' + localStorage.getItem("token")
      },
      isWatch: true
    }
  },
  watch: {
    value: {
      handler(newValue, oldValue) {
        if(newValue && newValue.length>0){
          let data = JSON.parse(newValue)
          this.fileList = data.map(item => ({
            name: item.id,
            url: `${this.config.downloadUrl}${item.id}`,
            status: 'success',
            response:{
              data:[item]
            },
          }));
        }
      },
      deep: true,
      immediate: true
    },
  },
  methods: {
    beforeUpload(file) {
      if (this.fileList.length >= this.max) {
        this.$message.warning(`最多只能上传 ${this.max} 张图片`)
        return false
      }
      return true
    },
    syncToParent(fileList) {
      const list = fileList.map(f => {
        const item = f.response?.data?.[0] || {}
        return item
      })
      this.$emit('input', JSON.stringify(list))
    },
    onSuccess(response, file, fileList) {
      if (response.code === 200) {
        fileList[fileList.length - 1].url = this.getPicUrlByJson(JSON.stringify(response.data),0)
        this.syncToParent(fileList)
      } else {
        this.$message.error(response.msg)
      }
    },
    handleRemove(file, fileList) {
      this.syncToParent(fileList)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    }
  }
}
</script>
<style scoped lang="scss">
.msg{
  font-size: 13px;
  color: #555;
}
::v-deep(.el-upload-list__item) {
  transition: none !important;
}
</style>

