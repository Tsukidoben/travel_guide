<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
    <div class="newTableStyle">
      <!--列表操作区域-->
      <div v-if="userRole == '1'" class="own-list-action-box" style="display:block;">
        <el-button plain v-if="userRole == '1'" type="primary" size="small" @click="openForm('add','新增景点',null)">新增</el-button>
        <el-button plain v-if="userRole == '1'" type="danger" size="small" @click="delBatch($refs.table.selection, delBatchUrl)">批量删除</el-button>
      </div>
      <!--列表数据区域-->
      <div class="own-list-table-box">
        <!-- 表格内容 -->
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" width="55" label="序号" />
            <el-table-column prop="attractionName" label="景点名称"/>
            <el-table-column prop="attractionPic" width="100" label="景点图片">
              <template #default="{row,$index}">
                <el-image style="width: 60px;height: 60px" :src="getPicUrlByJson(row.attractionPic,0)" :preview-src-list="[getPicUrlByJson(row.attractionPic,0)]"></el-image>
              </template>
            </el-table-column>
            <el-table-column prop="typeId" min-width="150" label="景点分类">
              <template #default="{row,$index}">
                <span>{{ typeIdOptions?.find(i=>i.id == row.typeId)?.typeName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="attractionDesc" min-width="150" label="景点简介"/>
            <el-table-column prop="attractionDetail" min-width="150" label="景点描述">
              <template #default="{row,$index}">
                <span class="two-line">{{ getHtmlPlainText(row.attractionDetail) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="attractionPlace" min-width="150" label="景点位置"/>
            <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="150">
              <template #default="{ row }">
                <span v-if="userRole == '1'" class="span-button" @click="openForm('edit', '编辑景点', row)">编辑</span>
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
    <AttractionInfoForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import AttractionInfoForm from "@/views/manage/attractionInfo/AttractionInfoForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "AttractionInfoList",
  components: { AttractionInfoForm ,SearchTop},
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/attractionInfo/delById/",
      delBatchUrl: "/attractionInfo/delBatch",
      typeIdOptions: [],
      searchList: [
        {type:'select',name:'景点分类',placeholder:'景点分类',value:'typeId', isApiData: true,apiConfig:{url: '/attractionType/listPage',data: {}}, key: 'id' ,label: 'typeName'},
        {type:'input',name:'关键字',placeholder:'景点名称、景点简介',value:'keyword'},
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
      request({
        url: config.backHost + "/attractionType/listPage",
        data: {}
      }).then(res => {
        this.typeIdOptions = res.data;
      });

    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/attractionInfo/listPage",
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
