<template>
  <div class="file-uploader-box">
    <el-upload
        class="file-uploader"
        :action="config.uploadUrl"
        :data="{ isPrivate: 1 }"
        :before-upload="beforeFileUpload"
        :show-file-list="false"
        :headers="headers"
        :on-success="onSuccess"
    >
      <div class="upload-content">
        <i v-loading="loading" class="el-icon-upload file-uploader-icon" />
        <div class="upload-text" v-if="loading">文件上传中，请稍等...</div>
        <div class="upload-text" v-else-if="fileName">
          <span>已上传文件：</span>
          <a :href="downloadLink" @click.stop target="_blank" class="download-link">
            {{ fileName }}
          </a>
          <i class="el-icon-close remove-icon" @click.stop="removeFile" title="删除文件"></i>
        </div>
        <div class="upload-text" v-else>点击上传文件</div>
      </div>
    </el-upload>
    <span v-if="isShowTip" class="msg">文件大小不能超过 {{ fileMaxSize }}Mb</span>
  </div>
</template>

<script>
import config from "@/config/config";

export default {
  name: "UploadFile",
  props: {
    isShowTip: {
      type: Boolean,
      default: true
    },
    fileMaxSize: {
      type: Number,
      default: 1000 // 默认 1GB
    },
    value: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      fileName: "",
      downloadLink: "",
      headers: {
        Authorization: "" + localStorage.getItem("token")
      },
      loading: false
    };
  },
  computed: {
    config() {
      return config;
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (newVal) {
          this.downloadLink = this.getDownloadLink(JSON.parse(newVal));
          this.fileName = this.extractFileName(JSON.parse(newVal));
        } else {
          this.downloadLink = "";
          this.fileName = "";
        }
      },
      immediate: true
    }
  },
  methods: {
    removeFile() {
      this.fileName = "";
      this.downloadLink = "";
      this.$emit("input", "");
    },
    beforeFileUpload(file) {
      const isLtSize = file.size / 1024 / 1024 < this.fileMaxSize;

      if (!isLtSize) {
        this.$message.error("上传文件大小不能超过 " + this.fileMaxSize + "Mb！");
        return false;
      }

      this.loading = true;
      return true;
    },
    onSuccess(response, file) {
      this.loading = false;
      if (response.code === 200) {
        const filePath = response.data;
        this.fileName = file.name;
        this.downloadLink = this.getDownloadLink(filePath);

        this.$emit("input", JSON.stringify(filePath));
      } else {
        this.$message.error(response.msg || "上传失败");
      }
    },
    getDownloadLink(path) {
      return config.downloadUrl + path[0].id;
    },
    extractFileName(path) {
      try {
        return path[0].fileName;
      } catch (e) {
        return "文件";
      }
    }
  }
};
</script>

<style scoped lang="scss">
.file-uploader-box .file-uploader .el-upload {
  border: 1px dashed #c0ccda;
  border-radius: 6px;
  cursor: pointer;
  background-color: #fbfdff;
  padding: 16px;
  text-align: center;
}

.upload-content {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.file-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #0077aa;
  word-break: break-word;
  text-align: center;
}

.download-link {
  color: #0077aa;
  text-decoration: underline;
  cursor: pointer;
}

.msg {
  font-size: 13px;
  color: #555;
  margin-top: 8px;
  display: block;
}
.remove-icon {
  color: #f56c6c;
  cursor: pointer;
  margin-left: 10px;
  font-size: 16px;

  &:hover {
    color: #ff0000;
  }
}
</style>

