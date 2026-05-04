<template>
  <div class="form-box">
    <own-sidebar-dialog width="1200px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <div style="display: flex;gap: 20px">
            <div style="flex-shrink: 0;width: 40%">
              <el-form-item prop="attractionPic" label="景点图片">
                <upload-image-more :key="newUploadImgKey" v-model="formData.attractionPic" :max="3"/>
                <p class="upload-tip">最多可上传 3 张图片，支持 jpg、png 格式</p>
              </el-form-item>
              <el-form-item prop="attractionName" label="景点名称">
                <el-input type="text" v-model="formData.attractionName" style="width: 100%"  placeholder="请输入景点名称"/>
              </el-form-item>
              <el-form-item prop="typeId" label="景点分类">
                <el-select style="width: 100%;" clearable v-model="formData.typeId"  placeholder="请选择景点分类">
                  <el-option v-for="item in typeIdOptions" :key="item.id" :label="item.typeName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item prop="playHour" label="游玩时长">
                <div style="display: flex; gap: 10px; align-items: center;">
                  <el-input-number v-model="playHourInput" :min="0" :max="24" style="width: 120px" placeholder="小时" />
                  <span style="flex-shrink: 0; color: #606266;">小时</span>
                  <el-input-number v-model="playMinuteInput" :min="0" :max="59" style="width: 120px" placeholder="分钟" />
                  <span style="flex-shrink: 0; color: #606266;">分钟</span>
                </div>
                <p class="upload-tip">例如：2小时30分钟，请输入 小时:2 分钟:30</p>
              </el-form-item>
              <el-form-item prop="businessHours" label="开放时间">
                <el-input type="text" v-model="formData.businessHours" style="width: 100%" placeholder="请输入开放时间，如：08:00-18:00" />
                <p class="upload-tip">例如：08:00-18:00 或 周一至周日 08:00-18:00</p>
              </el-form-item>
              <el-form-item prop="attractionDesc" label="景点简介">
                <el-input type="textarea" :row="3" v-model="formData.attractionDesc" style="width: 100%"  placeholder="请输入景点简介"/>
              </el-form-item>
              <el-form-item prop="attractionPlace" label="景点位置">
                <div style="display: flex; gap: 10px; align-items: flex-start;">
                  <el-input type="textarea" :rows="3" v-model="formData.attractionPlace" style="flex: 1" placeholder="请输入景点位置" @blur="handleGetCoordinates('attraction')" />
                  <el-button type="primary" size="small" :loading="geoLoading" @click="handleGetCoordinates('attraction')" style="flex-shrink: 0;">获取经纬度</el-button>
                </div>
                <!-- 隐藏字段存储经纬度 -->
                <el-input type="hidden" v-model="formData.longitude" />
                <el-input type="hidden" v-model="formData.latitude" />
              </el-form-item>
            </div>
            <div style="flex-shrink: 0;width: 60%">
              <el-form-item prop="attractionDetail" label="景点描述">
                <wangEditorComponent :height="500" v-model="formData.attractionDetail" />
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
import wangEditorComponent from "@/components/WangEditorComponent.vue";

export default {
  name: "AttractionInfoForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog  , uploadImageMore  , wangEditorComponent },
  data() {
    return {
      formData: {
          attractionPic: '',
          attractionName: '',
          typeId: '',
          attractionDesc: '',
          attractionDetail: '',
          attractionPlace: '',
          playHour: null,
          businessHours: '',
          longitude: '',
          latitude: ''
      },
      playHourInput: null, // 游玩时长-小时部分
      playMinuteInput: null, // 游玩时长-分钟部分
      addUrl: '/attractionInfo/saveOrUpdate',
      editUrl: '/attractionInfo/saveOrUpdate',
      typeIdOptions: [],
      newUploadImgKey:new Date().getTime(),
      geoLoading: false // 经纬度获取loading状态
    };
  },
  methods: {
    initForm(){
      this.newUploadImgKey = new Date().getTime()
        request({
          url: config.backHost + "/attractionType/listPage",
          data: {}
        }).then(res => {
          this.typeIdOptions = res.data;
        });

    },
    clearData(){
      this.formData = {
        attractionPic: '',
        attractionName: '',
        typeId: '',
        attractionDesc: '',
        attractionDetail: '',
        attractionPlace: '',
        playHour: null,
        businessHours: '',
        longitude: '',
        latitude: ''
      };
      this.playHourInput = null;
      this.playMinuteInput = null;
    },
    beforeSave() {
      let message = '';
      if (!this.formData.attractionPic) {
        message += "景点图片不能为空<br>";
      }
      if (!this.formData.attractionName) {
        message += "景点名称不能为空<br>";
      }
      if (!this.formData.typeId) {
        message += "景点分类不能为空<br>";
      }
      if (!this.formData.attractionDesc) {
        message += "景点简介不能为空<br>";
      }
      if (!this.formData.attractionDetail) {
        message += "景点描述不能为空<br>";
      }
      if (!this.formData.attractionPlace) {
        message += "景点位置不能为空<br>";
      }
      return message;
    },
    // 获取经纬度
    async handleGetCoordinates(type) {
      const address = this.formData.attractionPlace;
      if (!address || !address.trim()) {
        this.$message.warning('请先输入地址');
        return;
      }
      
      this.geoLoading = true;
      try {
        const res = await request({
          url: config.backHost + '/api/common/geoCode',
          method: 'POST',
          data: { address: address }
        });
        
        if (res.code === 200 && res.data) {
          this.formData.longitude = res.data.longitude || '';
          this.formData.latitude = res.data.latitude || '';
          this.$message.success('经纬度获取成功');
        } else {
          this.$message.error('地址解析失败，请检查地址格式或手动输入');
        }
      } catch (error) {
        console.error('地址解析失败:', error);
        this.$message.error('地址解析失败，请检查地址格式或手动输入');
      } finally {
        this.geoLoading = false;
      }
    },
    // 更新游玩时长（转换为分钟）
    updatePlayHour() {
      const hours = this.playHourInput || 0;
      const minutes = this.playMinuteInput || 0;
      this.formData.playHour = hours * 60 + minutes;
    },
    // 从后端数据初始化游玩时长显示
    initPlayHourDisplay() {
      if (this.formData.playHour) {
        this.playHourInput = Math.floor(this.formData.playHour / 60);
        this.playMinuteInput = this.formData.playHour % 60;
      } else {
        this.playHourInput = null;
        this.playMinuteInput = null;
      }
    }
  },
  watch: {
    // 监听游玩时长输入变化，自动转换为分钟
    playHourInput() {
      this.updatePlayHour();
    },
    playMinuteInput() {
      this.updatePlayHour();
    },
    // 监听表单数据变化，同步游玩时长显示（编辑时）
    'formData.playHour'(newVal) {
      if (newVal !== null && newVal !== undefined) {
        this.playHourInput = Math.floor(newVal / 60);
        this.playMinuteInput = newVal % 60;
      }
    }
  },
  created() {
    // 组件创建时的初始化逻辑
  },
};
</script>
