<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
    <div class="newTableStyle">
      <!--列表操作区域-->
      <div v-if="userRole == '1'" class="own-list-action-box" style="display:block;">
        <el-button plain v-if="userRole == '1'" type="primary" size="small" @click="openForm('add','新增用户',null)">新增用户</el-button>
        <el-button plain v-if="userRole == '1'" type="danger" size="small" @click="delBatch($refs.table.selection, delBatchUrl)">批量删除</el-button>
      </div>
      <!--列表数据区域-->
      <div class="own-list-table-box">
        <!-- 表格内容 -->
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" width="55" label="序号" />
            <el-table-column prop="headPicUrl" width="100" label="头像">
              <template #default="{row}">
                <el-image style="width: 60px;height: 60px" :src="getPicUrlByJson(row.headPicUrl,0)" :preview-src-list="[getPicUrlByJson(row.headPicUrl,0)]"></el-image>
              </template>
            </el-table-column>
            <el-table-column prop="userName" min-width="150" label="用户名"/>
            <el-table-column prop="userAccount" label="用户账号"/>
            <el-table-column prop="userSex" label="性别">
              <template #default="{row}">
                <span>{{ userSexOptions?.find(i=>i.value == row.userSex)?.label }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="userPhone"  min-width="150" label="联系电话"/>
            <el-table-column prop="userRole" label="角色">
              <template #default="{row}">
                <span>{{ userRoleOptions?.find(i=>i.value == row.userRole)?.label }}</span>
              </template>
            </el-table-column>
            <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="270">
              <template #default="{ row }">
                <span class="span-button" style="color: #FFA200;border-color: #FFA200" @click="resetPwd(row)">重置密码</span>
                <span class="span-button" style="color: #FFA200;border-color: #FFA200" @click="resetLoginStatus(row)">重置登录</span>
                <span v-if="userRole == '1'" class="span-button" @click="openForm('edit', '编辑用户信息', row)">编辑</span>
                <span v-if="userRole == '1'" class="span-button" style="color:red;border-color: red;" @click="delById(row, delUrl)">删除</span>
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
    <UserManageForm ref="form" @reload="query(searchForm,pageBean)" :dialog-visible.sync="formDialog" :title.sync="formTitle" :action.sync="formAction" />
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import UserManageForm from "@/views/manage/userManage/UserManageForm.vue";
import SearchTop from "@/components/searchTop.vue";

export default {
  name: "UserManageList",
  components: { UserManageForm ,SearchTop},
  mixins: [TableUtils],
  data() {
    return {
      delUrl: "/user/delById/",
      delBatchUrl: "/user/delBatch",
      userSexOptions: [{"value":"1","label":"男"},{"value":"0","label":"女"}],
      userRoleOptions: [{"value":"1","label":"管理员"},{"value":"3","label":"商家"},{"value":"2","label":"用户"}],
      searchList: [
        {type:'input',name:'用户名',placeholder:'用户名',value:'userName'},
        {type:'input',name:'用户账号',placeholder:'用户账号',value:'userAccount'},
        {type:'select',name:'角色',placeholder:'角色',value:'userRole',data: [{"value":"1","label":"管理员"},{"value":"3","label":"商家"},{"value":"2","label":"用户"}] , key: 'value' ,label: 'label'},
        {type:'input',name:'联系电话',placeholder:'联系电话',value:'userPhone'},
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
    resetPwd(row){
      this.$confirm('确定重置？', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: config.backHost + "/user/resetPwd",
          data: {userId: row.id}
        }).then(res => {
          if (res.code == 200) {
            this.$message({
              type: 'success',
              message: '重置密码成功!'
            });
          }
        })
      }).catch(err=>{
        this.$message({
          type: 'info',
          message: '已取消重置密码'
        });
      })
    },
    resetLoginStatus(row){
      this.$confirm(`确定重置【${row.userName}】登录次数？`, '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: config.backHost + "/user/resetLoginStatus/" + row.id,
        }).then(res => {
          if (res.code == 200) {
            this.$message({
              type: 'success',
              message: '重置登录次数成功!'
            });
          }
        })
      }).catch(err=>{
        this.$message({
          type: 'info',
          message: '已取消重置登录次数'
        });
      })
    },
    initData(){
    },
    query(searchForm, pageBean) {
      request({
        url: config.backHost + "/user/listPage",
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
