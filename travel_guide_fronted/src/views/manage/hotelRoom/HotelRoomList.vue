<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div style="height: 100%;display: flex;gap: 12px">
      <div style="width: 220px;background: #FFFFFF;border: 1px solid #EBEEF5;border-radius: 8px;height: 100%;overflow-y: auto">
        <el-tree ref="tree" node-key="id" highlight-current :data="hotelIdOptions" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
      </div>
      <div style="width: calc(100% - 232px);height: 100%;display: flex;flex-direction: column">
    <!--搜索区域-->
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
        <div class="newTableStyle">
          <!--列表操作区域-->
          <div v-if="userRole == '3'" class="own-list-action-box" style="display:block;">
            <el-button plain v-if="userRole == '3'" type="primary" size="small" @click="openForm('add','新增房间',null)">新增</el-button>
            <el-button plain v-if="userRole == '3'" type="danger" size="small" @click="delBatch($refs.table.selection, delBatchUrl)">批量删除</el-button>
          </div>
          <!--列表数据区域-->
          <div class="own-list-table-box">
            <!-- 表格内容 -->
            <div class="own-list-table-data-box">
              <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
                <el-table-column type="selection" width="55" />
                <el-table-column type="index" width="55" label="序号" />
                <el-table-column prop="roomName" label="房间名称" min-width="120"/>
                <el-table-column prop="htoelPic" width="100" label="房间图片">
                  <template #default="{row,$index}">
                    <el-image style="width: 60px;height: 60px" :src="getPicUrlByJson(row.roomPic,0)" :preview-src-list="[getPicUrlByJson(row.roomPic,0)]"></el-image>
                  </template>
                </el-table-column>
                <el-table-column prop="hotelId" label="所属酒店" min-width="120">
                  <template #default="{row,$index}">
                    <span>{{ hotelIdOptions?.find(i=>i.id == row.hotelId)?.htoelName }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="roomDesc" label="房间介绍" min-width="150"/>
                <el-table-column prop="price" label="房间价格"/>
                <el-table-column prop="roomCount" label="房间数量"/>
                <el-table-column prop="peopleCount" label="可住人数"/>
                <el-table-column prop="roomFloor" label="房间楼层"/>
                <el-table-column fixed="right" v-if="userRole == '3'" label="操作" width="150">
                  <template #default="{ row }">
                    <span v-if="userRole == '3'" class="span-button" @click="openForm('edit', '编辑房间', row)">编辑</span>
                    <span v-if="userRole == '3'" class="span-button" style="color:red;" @click="delById(row, delUrl)">删除</span>
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
    <HotelRoomForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import HotelRoomForm from "@/views/manage/hotelRoom/HotelRoomForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "HotelRoomList",
  components: { HotelRoomForm ,SearchTop},
  mixins: [TableUtils],
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'htoelName'
      },
      delUrl: "/hotelRoom/delById/",
      delBatchUrl: "/hotelRoom/delBatch",
      hotelIdOptions: [],
      searchList: [
        {type:'input',name:'关键字',placeholder:'房间名称、房间介绍',value:'keyword'},
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
    handleNodeClick(val){
      this.searchForm.hotelId = val.id
      this.query(this.searchForm, this.pageBean);
    },
    initData(){
      request({
        url: config.backHost + "/hotelInfo/listPage",
        data: {}
      }).then(res => {
        this.hotelIdOptions = res.data;
        this.hotelIdOptions.unshift({id:'',htoelName:'全部'})
        this.$nextTick(()=>{
          this.$refs.tree.setCurrentKey('');
          this.query(this.searchForm, this.pageBean);
        })
      });
    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/hotelRoom/listPage",
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
