<template>
  <div class="form-box">
    <own-sidebar-dialog width="1000px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <div style="display: flex;gap: 20px">
            <div style="flex-shrink: 0;width: 40%">
              <el-form-item prop="images" label="小吃图片">
                <upload-image-more :key="newUploadImgKey" v-model="formData.images" :max="5"/>
                <p class="upload-tip">最多可上传 5 张图片，支持 jpg、png 格式</p>
              </el-form-item>
              <el-form-item prop="name" label="小吃名称">
                <el-input type="text" v-model="formData.name" style="width: 100%"  placeholder="请输入小吃名称"/>
              </el-form-item>
              <el-form-item prop="categoryId" label="小吃分类">
                <el-select style="width: 100%;" clearable v-model="formData.categoryId"  placeholder="请选择小吃分类">
                  <el-option v-for="item in categoryOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item prop="shopId" label="所属店铺">
                <el-select style="width: 100%;" clearable v-model="formData.shopId"  placeholder="请选择店铺">
                  <el-option v-for="item in shopOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item prop="avgPrice" label="人均价格">
                <el-input-number v-model="formData.avgPrice" :min="0" :max="9999" style="width: 100%"></el-input-number>
              </el-form-item>
              <el-form-item prop="score" label="评分">
                <el-rate v-model="formData.score" :max="5" show-score></el-rate>
              </el-form-item>
              <el-form-item prop="tags" label="推荐标签">
                <el-input type="text" v-model="formData.tags" style="width: 100%"  placeholder="多个标签用逗号分隔，如：必吃,特色,网红"/>
              </el-form-item>
              <el-form-item prop="status" label="状态">
                <el-radio-group v-model="formData.status">
                  <el-radio :label="1">上架</el-radio>
                  <el-radio :label="0">下架</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item prop="isRecommend" label="首页推荐">
                <el-radio-group v-model="formData.isRecommend">
                  <el-radio :label="1">是</el-radio>
                  <el-radio :label="0">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
            <div style="flex-shrink: 0;width: 60%">
              <el-form-item prop="description" label="小吃简介">
                <el-input type="textarea" :rows="4" v-model="formData.description" style="width: 100%"  placeholder="请输入小吃简介"/>
              </el-form-item>
              <el-form-item prop="recommendReason" label="推荐理由">
                <el-input type="textarea" :rows="4" v-model="formData.recommendReason" style="width: 100%"  placeholder="请输入推荐理由"/>
              </el-form-item>
            </div>
          </div>
        </el-form>
      </template>
      <template #footer>
        <el-button type="primary" size="small" @click="confirmSubmit(action,formData)">确认</el-button>
        <el-button type="warning" size="small" @click="closeDialog">关闭</el-button>
      </template>
    </own-sidebar-dialog>
  </div>
</template>

<script>
import FormUtils from '@/utils/formUtils'
import request from "@/utils/request";
import config from "@/config/config";
import OwnSidebarDialog from "@/components/OwnSidebarDialog.vue";
import uploadImageMore from "@/components/UploadImageMore.vue";

export default {
  name: "FoodInfoForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog, uploadImageMore},
  data() {
    return {
      formData: {
        id: null,
        name: '',
        images: '',
        categoryId: '',
        shopId: '',
        avgPrice: 0,
        score: 5,
        tags: '',
        status: 1,
        isRecommend: 0,
        description: '',
        recommendReason: ''
      },
      addUrl: '/api/food/info/saveOrUpdate',
      editUrl: '/api/food/info/saveOrUpdate',
      categoryOptions: [],
      shopOptions: [],
      newUploadImgKey: new Date().getTime(),
    };
  },
  methods: {
    initForm(){
      this.newUploadImgKey = new Date().getTime();
      
      // 获取分类列表
      request({
        url: config.backHost + "/api/food/category/list",
        method: 'POST'
      }).then(res => {
        if (res.code === 200) {
          this.categoryOptions = res.data;
        }
      });
      
      // 获取店铺列表
      request({
        url: config.backHost + "/api/food/shop/list",
        method: 'POST'
      }).then(res => {
        if (res.code === 200) {
          this.shopOptions = res.data;
        }
      });
    },
    clearData(){
      this.formData = {
        id: null,
        name: '',
        images: '',
        categoryId: '',
        shopId: '',
        avgPrice: 0,
        score: 5,
        tags: '',
        status: 1,
        isRecommend: 0,
        description: '',
        recommendReason: ''
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.images) {
        message += "小吃图片不能为空<br>";
      }
      if (!this.formData.name) {
        message += "小吃名称不能为空<br>";
      }
      if (!this.formData.categoryId) {
        message += "小吃分类不能为空<br>";
      }
      if (!this.formData.shopId) {
        message += "所属店铺不能为空<br>";
      }
      if (!this.formData.description) {
        message += "小吃简介不能为空<br>";
      }
      return message;
    },
  }
};
</script>

<style scoped lang="scss">
::v-deep .el-dialog__body{
  max-height: 70vh;
  overflow-x: hidden;
  overflow-y: auto;
}
.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
