<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div style="height: 100%;display: flex;gap: 12px">
      <div style="width: 220px;background: #FFFFFF;border: 1px solid #EBEEF5;border-radius: 8px;height: 100%;overflow-y: auto">
        <el-tree ref="tree" node-key="id" highlight-current :data="attractionIdOptions" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
      </div>
      <div style="width: calc(100% - 232px);height: 100%;display: flex;flex-direction: column">
        <div class="own-list-search-box">
          <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
        </div>
        <div class="newTableStyle">
          <!--列表操作区域-->
          <div v-if="userRole == '1'" class="own-list-action-box" style="display:block;">
            <el-button plain v-if="userRole == '1'" type="primary" size="small" @click="openForm('add','新增门票',null)">新增</el-button>
            <el-button plain v-if="userRole == '1'" type="danger" size="small" @click="delBatch($refs.table.selection, delBatchUrl)">批量删除</el-button>
          </div>
          <!--列表数据区域-->
          <div class="own-list-table-box">
            <!-- 表格内容 -->
            <div class="own-list-table-data-box">
              <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
                <el-table-column type="selection" width="55" />
                <el-table-column type="index" width="55" label="序号" />
                <el-table-column prop="attractionId" label="所属景点">
                  <template #default="{row,$index}">
                    <span>{{ attractionIdOptions?.find(i=>i.id == row.attractionId)?.attractionName }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="ticketName" label="门票名称"/>
                <el-table-column prop="ticketPrice" label="门票价格"/>
                <el-table-column prop="useScope" label="使用范围"/>
                <el-table-column prop="status" label="门票状态">
                  <template #default="{row,$index}">
                    <el-tag type="success" v-if="row.status == 1">{{ statusOptions?.find(i=>i.value == row.status)?.label }}</el-tag>
                    <el-tag type="danger" v-if="row.status == 2">{{ statusOptions?.find(i=>i.value == row.status)?.label }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="150">
                  <template #default="{ row }">
                    <span v-if="userRole == '1'" class="span-button" @click="openForm('edit', '编辑门票', row)">编辑</span>
                    <span v-if="userRole == '1'" class="span-button" style="color:red;" @click="delById(row, delUrl)">删除</span>
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
      </div>
    </div>
    <!--表单-->
    <TicketInfoForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import TicketInfoForm from "@/views/manage/ticketInfo/TicketInfoForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "TicketInfoList",
  components: { TicketInfoForm ,SearchTop},
  mixins: [TableUtils],
  data() {
    return {
      treeData:[],
      defaultProps: {
        children: 'children',
        label: 'attractionName'
      },
      delUrl: "/ticketInfo/delById/",
      delBatchUrl: "/ticketInfo/delBatch",
      attractionIdOptions: [],
      statusOptions: [{"value":"1","label":"正常"},{"value":"2","label":"下架"}],
      searchList: [
        {type:'input',name:'门票名称',placeholder:'门票名称',value:'ticketName'}
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
  },
  methods: {
    handleNodeClick(val){
      this.searchForm.attractionId = val.id
      this.query(this.searchForm, this.pageBean);
    },
    initData(){
      request({
        url: config.backHost + "/attractionInfo/listPage",
        data: {}
      }).then(res => {
        this.attractionIdOptions = res.data;
        this.attractionIdOptions.unshift({id:'',attractionName:'全部'})
        this.$nextTick(()=>{
          this.$refs.tree.setCurrentKey('');
          this.query(this.searchForm, this.pageBean);
        })
      });
    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/ticketInfo/listPage",
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
.el-tree {
  margin: 10px 0;
  ::v-deep .el-tree-node__content:hover {
    background-color: #FDE6D8 !important;
    color: #FF8A45;
  }

  ::v-deep .el-tree-node.is-current > .el-tree-node__content {
    background-color: #FF8A45 !important;
    color: #FFFFFF !important;
  }

  ::v-deep .el-tree-node.is-current > .el-tree-node__content:hover {
    background-color: #FF8A45 !important;
    color: #FFFFFF !important;
  }
  ::v-deep .el-tree-node__content{
    height: 40px;
  }
}
</style>
