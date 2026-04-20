<template>
  <div class="post-strategy-container">
    <div class="form-header">
      <el-page-header @back="$router.go(-1)" content="发布旅游攻略"></el-page-header>
    </div>

    <div class="post-card">
      <el-form :model="form" ref="postForm" label-position="top">
        <el-form-item label="添加封面照片" required>
          <upload-image-more :msg="'最多可上传 3 张图片'" v-model="form.strategyPic" :max="3"/>
<!--          <div class="upload-section">-->
<!--            <el-upload-->
<!--                class="cover-uploader"-->
<!--                :action="uploadUrl"-->
<!--                :show-file-list="false"-->
<!--                :on-success="handleUploadSuccess"-->
<!--                :before-upload="beforeUpload"-->
<!--            >-->
<!--              <div v-if="form.strategyPic" class="preview-container">-->
<!--                <img :src="getPicUrlByJson(form.strategyPic, 0)" class="cover-image" />-->
<!--                <div class="edit-overlay"><i class="el-icon-edit"></i> 更换封面</div>-->
<!--              </div>-->
<!--              <div v-else class="upload-placeholder">-->
<!--                <i class="el-icon-camera-solid"></i>-->
<!--                <p>上传吸引人的封面图</p>-->
<!--              </div>-->
<!--            </el-upload>-->
<!--          </div>-->
        </el-form-item>
        <el-form-item label="关联景点" required>
          <el-select
              v-model="form.attractionId"
              filterable
              placeholder="请搜索并选择攻略关联的景点"
              style="width: 100%"
              @change="handleAttractionChange"
          >
            <el-option
                v-for="item in attractionOptions"
                :key="item.id"
                :label="item.attractionName"
                :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分享您的旅行故事" required>
          <el-input
              type="textarea"
              :rows="12"
              placeholder="在这里记录您的旅行心得、避坑指南或路线推荐..."
              v-model="form.strategyContent"
              maxlength="2000"
              show-word-limit
          >
          </el-input>
        </el-form-item>

        <div class="form-footer">
          <el-button type="primary" class="btn-primary-theme" round @click="submitStrategy" :loading="submitting">
            立即发布
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";
import UploadImageMore from "@/components/UploadImageMore.vue";
export default {
  mixins: [common],
  components:{UploadImageMore},
  data() {
    return {
      uploadUrl: config.backHost + "/file/upload",
      submitting: false,
      attractionOptions: [],
      form: {
        id: '',
        strategyPic: "",
        strategyContent: "",
        attractionId: "",
        attractionName: "",
      }
    };
  },
  mounted() {
    this.loadAttractions()
    const id = this.$route.query.id;
    if(id){
      request({
        url: `${config.backHost}/tripStrategy/getById/${id}`,
      }).then(res => {
        if (res.code === 200) {
          this.form = res.data;
        }
      });
    }
  },
  methods: {
    handleAttractionChange(val) {
      const selected = this.attractionOptions.find(item => item.id === val);
      if (selected) {
        this.form.attractionName = selected.attractionName;
      }
    },
    loadAttractions() {
      request({
        url: config.backHost + "/attractionInfo/listPage",
        data: { pageBean: { page: 1, pageSize: -1 }, params: {} }
      }).then(res => {
        if (res.code === 200) {
          this.attractionOptions = res.data;
        }
      });
    },
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isJPG) this.$message.error('只能上传 JPG/PNG 格式图片!');
      if (!isLt2M) this.$message.error('图片大小不能超过 2MB!');
      return isJPG && isLt2M;
    },

    handleUploadSuccess(res) {
      if (res.code === 200) {
        this.form.strategyPic = JSON.stringify([res.data[0]]);
        this.$message.success("封面上传成功");
      }
    },

    submitStrategy() {
      if (!this.form.strategyPic) return this.$message.warning("请上传封面图");
      if (!this.form.attractionId) return this.$message.warning("请选择关联的景点");
      if (!this.form.strategyContent) return this.$message.warning("请输入攻略内容");
      this.handlePost();
    },

    handlePost() {
      this.submitting = true;
      request({
        url: config.backHost + "/tripStrategy/" + (this.form.id?'update':'addNew'),
        data: this.form
      }).then(res => {
        if (res.code === 200) {
          this.$message.success(res.msg)
          this.$router.push({name:'myStrategyGuide'});
        }
      }).finally(() => {
        this.submitting = false;
      });
    }
  }
};
</script>

<style scoped lang="scss">
.post-strategy-container {
  padding: 20px 10%;
  background-color: #f4f7fb;
  min-height: calc(100vh - 80px);

  .form-header {
    max-width: 900px;
    margin: 0 auto 20px;
  }

  .post-card {
    max-width: 900px;
    margin: 0 auto;
    background: white;
    padding: 40px;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  }

  .upload-section {
    width: 100%;

    .cover-uploader {
      width: 100%;
      ::v-deep .el-upload {
        width: 100%;
        border: 2px dashed #dcdfe6;
        border-radius: 12px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: 0.3s;
        &:hover { border-color: $theme-colorFront; }
      }
    }

    .upload-placeholder {
      padding: 60px 0;
      text-align: center;
      i { font-size: 48px; color: #909399; margin-bottom: 10px; }
      p { font-size: 16px; color: #303133; font-weight: bold; }
      span { font-size: 13px; color: #909399; }
    }

    .preview-container {
      position: relative;
      height: 300px;
      .cover-image { width: 100%; height: 100%; object-fit: cover; }
      .edit-overlay {
        position: absolute; top: 0; left: 0; width: 100%; height: 100%;
        background: rgba(0,0,0,0.4); color: white;
        display: flex; align-items: center; justify-content: center;
        opacity: 0; transition: 0.3s;
      }
      &:hover .edit-overlay { opacity: 1; }
    }
  }

  ::v-deep .el-textarea__inner {
    font-size: 16px;
    padding: 20px;
    border-radius: 12px;
    background-color: #f9fafc;
    border: 1px solid #eee;
    &:focus { border-color: $theme-colorFront; }
  }

  .form-footer {
    margin-top: 40px;
    display: flex;
    justify-content: flex-end;
    gap: 15px;

    .el-button {
      padding: 12px 35px;
      font-size: 16px;
      font-weight: bold;
    }
  }
}
.btn-primary-theme {
  background-color: $theme-color !important;
  border-color: $theme-color !important;
  color: #fff !important;
  &:hover {
    background-color: #ff9d66 !important;
    opacity: 0.9;
  }
}
</style>
