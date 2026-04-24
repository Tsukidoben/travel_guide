<template>
  <div class="own-mvue-list-box">
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
    <div class="newTableStyle">
      <div v-if="userRole == '1'" class="own-list-action-box" style="display:block;">
        <el-button plain v-if="userRole == '1'" type="danger" size="small" @click="delBatch($refs.table.selection, delBatchUrl)">批量删除</el-button>
      </div>
      <div class="own-list-table-box">
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" width="55" label="序号" />
            <el-table-column prop="strategyPic" width="100" label="攻略图片">
              <template #default="{row}">
                <el-image style="width: 60px;height: 60px; border-radius: 4px" :src="getPicUrlByJson(row.strategyPic,0)" :preview-src-list="[getPicUrlByJson(row.strategyPic,0)]"></el-image>
              </template>
            </el-table-column>
            <el-table-column prop="strategyContent" label="攻略内容" show-overflow-tooltip />
            <el-table-column prop="status" label="攻略状态">
              <template #default="{row}">
                <el-tag :type="getStatusTag(row.status)">{{ statusOptions?.find(i=>i.value == row.status)?.label }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reviewReason" label="审核意见" />
            <el-table-column prop="createName" label="发布人"/>
            <el-table-column prop="createTime" width="160" label="发布时间">
              <template #default="{row}">
                {{ timestampToYMDHMS(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="240">
              <template #default="{ row }">
                <template v-if="row.status == '10'">
                  <span class="span-button" style="color:#67C23A;border-color: #67C23A;" @click="handleAgree(row.id)">通过</span>
                  <span class="span-button" style="color:#F56C6C;border-color: #F56C6C;" @click="handleNoAgreeOpen(row)">驳回</span>
                </template>
                <span class="span-button" @click="openForm('view', '查看详情', row)">查看详情</span>
                <span class="span-button" style="color:red;border-color: red;" @click="delById(row, delUrl)">删除</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-pagination
            :current-page="pageBean.page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageBean.pageSize"
            layout="total, sizes, prev, pager, next"
            :total="pageBean.total"
            @current-change="currPageChange"
            @size-change="handleSizeChange" />
      </div>
    </div>

    <el-dialog title="驳回攻略" :visible.sync="noAgreeVisible" width="400px" append-to-body>
      <el-form :model="noAgreeForm" label-width="80px">
        <el-form-item label="驳回原因" required>
          <el-input type="textarea" :rows="3" v-model="noAgreeForm.reviewReason" placeholder="请输入驳回理由"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="noAgreeVisible = false" size="small">取消</el-button>
        <el-button type="danger" @click="submitNoAgree" size="small">确定驳回</el-button>
      </div>
    </el-dialog>

    <TripStrategyForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import TripStrategyForm from "@/views/manage/tripStrategy/TripStrategyForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "TripStrategyList",
  components: { TripStrategyForm ,SearchTop},
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/tripStrategy/delById/",
      delBatchUrl: "/tripStrategy/delBatch",
      statusOptions: [{"value":"10","label":"待审核"},{"value":"80","label":"审核通过"},{"value":"-2","label":"审核不通过"}],
      searchList: [
        {type:'input',name:'攻略内容',placeholder:'攻略内容',value:'strategyContent'},
        {type:'select',name:'攻略状态',placeholder:'攻略状态',value:'status',data: [{"value":"10","label":"待审核"},{"value":"80","label":"审核通过"},{"value":"-2","label":"审核不通过"}] , key: 'value' ,label: 'label'},
      ],
      noAgreeVisible: false,
      noAgreeForm: {
        id: '',
        reviewReason: ''
      }
    }
  },
  computed: {
    userRole() {
      const user = this.$store.getters.getUser;
      return user ? user.userRole : null;
    },
  },
  mounted() {
    this.query(this.searchForm, this.pageBean);
  },
  methods: {
    getStatusTag(status) {
      const map = { "10": "warning", "80": "success", "-2": "danger" };
      return map[status] || "info";
    },
    handleAgree(id) {
      this.$confirm('确定通过该攻略审核吗？', '提示', {
        type: 'success',
        confirmButtonText: '通过'
      }).then(() => {
        request({
          url: config.backHost + `/tripStrategy/agree/${id}`,
        }).then(res => {
          if (res.code === 200) {
            this.$message.success("已通过审核");
            this.query(this.searchForm, this.pageBean);
          }
        });
      }).catch(() => {});
    },
    handleNoAgreeOpen(row) {
      this.noAgreeForm = {
        id: row.id,
        reviewReason: ''
      };
      this.noAgreeVisible = true;
    },
    submitNoAgree() {
      if (!this.noAgreeForm.reviewReason) {
        return this.$message.warning("请填写驳回理由");
      }
      request({
        url: config.backHost + "/tripStrategy/noAgree",
        data: this.noAgreeForm
      }).then(res => {
        if (res.code === 200) {
          this.$message.error("攻略已驳回");
          this.noAgreeVisible = false;
          this.query(this.searchForm, this.pageBean);
        }
      });
    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/tripStrategy/listPage",
        data: { params: searchForm, pageBean: pageBean }
      }).then(res => {
        if (res.code === 200) {
          this.tableData = res.data;
          this.pageBean = res.pageBean;
        }
      });
    }
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/css/table.scss";
</style>
