<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
    <div class="newTableStyle">
      <!--列表操作区域-->
      <div v-if="userRole == '1'" class="own-list-action-box" style="display:block;">
        <el-button plain v-if="userRole == '1'" type="primary" size="small" @click="openForm('add','新增分类',null)">新增</el-button>
        <el-button plain v-if="userRole == '1'" type="danger" size="small" @click="delBatch($refs.table.selection, delBatchUrl)">批量删除</el-button>
      </div>
      <!--列表数据区域-->
      <div class="own-list-table-box">
        <!-- 表格内容 -->
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" width="55" label="序号" />
            <el-table-column prop="typeName" label="分类名称"/>
            <el-table-column prop="typeDesc" label="分类简介"/>
            <el-table-column prop="createTime" width="160" label="创建时间">
              <template #default="{row,$index}">
                {{ timestampToYMDHMS(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="150">
              <template #default="{ row }">
                <span v-if="userRole == '1'" class="span-button" @click="openForm('edit', '编辑分类', row)">编辑</span>
                <span v-if="userRole == '1'" class="span-button" style="color:red;border-color: red" @click="delById(row, delUrl)">删除</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!--分页区域-->
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
    <!--表单-->
    <AttractionTypeForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import AttractionTypeForm from "@/views/manage/attractionType/AttractionTypeForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "AttractionTypeList",
  components: { AttractionTypeForm ,SearchTop},
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/attractionType/delById/",
      delBatchUrl: "/attractionType/delBatch",
      searchList: [
        {type:'input',name:'关键字',placeholder:'分类名称、分类简介',value:'keyword'},
      ]
    }
  },
  computed: {
    userRole() {
      return this.$store.getters.getUser.userRole;
    },
  },
  mounted() {
    this.initData();
    this.query(this.searchForm, this.pageBean);
  },
  methods: {
    initData(){
    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/attractionType/listPage",
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
