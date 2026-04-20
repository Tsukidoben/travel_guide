<template>
  <div class="form-box">
    <own-sidebar-dialog width="500px" :title="title" :visible.sync="visible" :before-close="closeDialog" :modal="true" :close-on-click-modal="false">
      <template #default>
        <el-form :model="formData" ref="form" label-width="auto" style="padding: 0 20px">
          <el-form-item prop="attractionId" label="所属景点">
            <el-select style="width: 100%;" clearable v-model="formData.attractionId"  placeholder="请选择所属景点">
              <el-option v-for="item in attractionIdOptions" :key="item.id" :label="item.attractionName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="ticketName" label="门票名称">
            <el-input type="text" v-model="formData.ticketName" style="width: 100%"  placeholder="请输入门票名称"/>
          </el-form-item>
          <el-form-item prop="ticketPrice" label="门票价格">
            <el-input-number v-model="formData.ticketPrice" style="width: 100%;" @input="(e)=>onInput(e,'ticketPrice')" controls-position="right" :min="1"></el-input-number>
          </el-form-item>
          <el-form-item prop="useScope" label="使用范围">
            <el-input type="textarea" :row="3" v-model="formData.useScope" style="width: 100%"  placeholder="请输入使用范围"/>
          </el-form-item>
          <el-form-item prop="status" label="门票状态">
            <el-switch
                v-model="formData.status"
                active-text="正常"
                active-value="1"
                inactive-value="2"
                inactive-text="下架">
            </el-switch>
<!--            <el-select style="width: 100%;" clearable v-model="formData.status"  placeholder="请选择门票状态">-->
<!--              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>-->
<!--            </el-select>-->
          </el-form-item>
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

export default {
  name: "TicketInfoForm",
  mixins: [FormUtils],
  components: {OwnSidebarDialog },
  data() {
    return {
      formData: {
          attractionId: '',
          ticketName: '',
          ticketPrice: '',
          useScope: '',
          status: "1",
      },
      addUrl: '/ticketInfo/saveOrUpdate',
      editUrl: '/ticketInfo/saveOrUpdate',
      attractionIdOptions: [],
      statusOptions: [{"value":"1","label":"正常"},{"value":"2","label":"下架"}],

    };
  },
  methods: {
    onInput(value,type) {
      if (!value) {
        this.$nextTick(()=>{
          this.$set(this.formData, type, undefined);
          setTimeout(()=>{
            this.$set(this.formData, type, 1);
          },10)
        })
      }
    },
    initForm(){
        request({
          url: config.backHost + "/attractionInfo/listPage",
          data: {}
        }).then(res => {
          this.attractionIdOptions = res.data;
        });

    },
    clearData(){
      this.formData = {
        attractionId: '',
        ticketName: '',
        ticketPrice: '',
        useScope: '',
        status: "1",
      };
    },
    beforeSave() {
      let message = '';
      if (!this.formData.attractionId) {
        message += "所属景点不能为空<br>";
      }
      if (!this.formData.ticketName) {
        message += "门票名称不能为空<br>";
      }
      if (!this.formData.ticketPrice) {
        message += "门票价格不能为空<br>";
      }
      if (!this.formData.useScope) {
        message += "使用范围不能为空<br>";
      }
      if (!this.formData.status) {
        message += "门票状态不能为空<br>";
      }
      return message;
    },
  }
};
</script>
<style scoped lang="scss">
</style>
