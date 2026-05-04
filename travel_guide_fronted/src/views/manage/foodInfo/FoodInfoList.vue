<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
    <div class="newTableStyle">
      <!--列表操作区域-->
      <div v-if="userRole == '1'" class="own-list-action-box" style="display:block;">
        <el-button plain v-if="userRole == '1'" type="primary" size="small" @click="openForm('add','新增小吃',null)">新增</el-button>
        <el-button plain v-if="userRole == '1'" type="danger" size="small" @click="delBatch($refs.table.selection, delBatchUrl)">批量删除</el-button>
      </div>
      <!--列表数据区域-->
      <div class="own-list-table-box">
        <!-- 表格内容 -->
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" width="55" label="序号" />
            <el-table-column prop="images" label="小吃图片" width="120">
              <template #default="{row}">
                <el-image :src="getPicUrlByJson(row.images, 0)" style="width: 80px; height: 60px" fit="cover"></el-image>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="小吃名称" width="150"/>
            <el-table-column prop="categoryName" label="分类" width="100"/>
            <el-table-column prop="shopName" label="店铺" width="150"/>
            <el-table-column prop="score" label="评分" width="160">
              <template #default="{row}">
                <el-rate :value="parseFloat(row.score)" disabled text-color="#ff9900" :max="5"></el-rate>
              </template>
            </el-table-column>
            <el-table-column prop="avgPrice" label="人均" width="80">
              <template #default="{row}">
                ¥{{ row.avgPrice }}
              </template>
            </el-table-column>
            <el-table-column prop="isRecommend" label="推荐" width="80">
              <template #default="{row}">
                <el-tag :type="row.isRecommend ? 'success' : 'info'" size="mini">
                  {{ row.isRecommend ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{row}">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="mini">
                  {{ row.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="250">
              <template #default="{ row }">
                <span v-if="userRole == '1'" class="span-button" @click="openForm('edit', '编辑小吃', row)">编辑</span>
                <span v-if="userRole == '1'" class="span-button" @click="toggleStatus(row)">
                  {{ row.status === 1 ? '下架' : '上架' }}
                </span>
                <span v-if="userRole == '1'" class="span-button" @click="toggleRecommend(row)">
                  {{ row.isRecommend ? '取消推荐' : '设为首页推荐' }}
                </span>
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
    <FoodInfoForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import FoodInfoForm from "@/views/manage/foodInfo/FoodInfoForm.vue";
import SearchTop from "@/components/searchTop.vue";
import common from "@/utils/common";

export default {
  name: "FoodInfoList",
  components: { FoodInfoForm, SearchTop},
  mixins: [TableUtils, common],
  data() {
    return {
      delUrl: "/api/food/info/delete/",
      delBatchUrl: "/api/food/info/delBatch",
      searchList: [
        {type:'input',name:'关键字',placeholder:'小吃名称、店铺名',value:'keyword'},
        {type:'select',name:'状态',placeholder:'全部',value:'status',data:[
          {label:'全部',value:''},
          {label:'上架',value:1},
          {label:'下架',value:0}
        ],key:'value',label:'label'}
      ]
    }
  },
  computed: {
    userRole() {
      const user = this.$store.getters.getUser;
      return user ? user.userRole : null;
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
      // 构造查询参数
      const params = {...searchForm};
      
      // 如果状态为空字符串或未定义，显式设置为 null，告诉后端查询所有状态
      if (params.status === '' || params.status === undefined) {
        params.status = null;
      }
      
      request({
        url: config.backHost + "/api/food/info/listPage",
        method: 'POST',
        data: { 
          params: params,
          pageBean: pageBean
        }
      }).then(res => {
        if (res.code === 200) {
          // 处理不同的返回数据格式
          if (Array.isArray(res.data)) {
            this.tableData = res.data;
          } else if (res.data && res.data.records) {
            this.tableData = res.data.records;
          } else {
            this.tableData = [];
          }
          
          // 处理分页信息
          if (res.pageBean) {
            this.pageBean = res.pageBean;
          } else if (res.data && res.data.total !== undefined) {
            this.pageBean = {
              page: pageBean.page,
              pageSize: pageBean.pageSize,
              total: res.data.total
            };
          }
        }
      });
    },
    toggleStatus(row) {
      const newStatus = row.status === 1 ? 0 : 1;
      const actionText = newStatus === 1 ? '上架' : '下架';
      
      this.$confirm(`确认${actionText}该小吃吗？`, '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: config.backHost + "/api/food/info/updateStatus",
          method: 'POST',
          data: {
            id: row.id,
            status: newStatus
          }
        }).then(res => {
          if (res.code === 200) {
            this.$message.success(actionText + '成功');
            // 清除状态筛选条件，避免下架/上架后的数据被过滤掉
            this.$set(this.searchForm, 'status', '');
            // 重置到第一页
            this.pageBean.page = 1;
            this.query(this.searchForm, this.pageBean);
          } else {
            this.$message.error(res.msg || actionText + '失败');
          }
        }).catch(err => {
          this.$message.error(actionText + '失败，请检查后端服务');
        });
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },
    toggleRecommend(row) {
      const newRecommend = !row.isRecommend;
      
      request({
        url: config.backHost + "/api/food/info/updateRecommend",
        method: 'POST',
        data: {
          id: row.id,
          isRecommend: newRecommend ? 1 : 0  // 将 Boolean 转换为 Integer
        }
      }).then(res => {
        if (res.code === 200) {
          this.$message.success(newRecommend ? '已设为首页推荐' : '已取消推荐');
          this.query(this.searchForm, this.pageBean);
        } else {
          this.$message.error(res.msg || '操作失败');
        }
      }).catch(err => {
        this.$message.error('更新推荐状态失败，请检查后端服务');
      });
    }
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/css/table.scss";
</style>
