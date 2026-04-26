<template>
  <div class="own-mvue-list-box">
    <!--搜索区域-->
    <div class="own-list-search-box">
      <search-top ref="searchTop" :searchForm.sync="searchForm" :list.sync="searchList" @searchData="searchData" @clearSearch="clearSearch"/>
    </div>
    <div class="newTableStyle">
      <!--列表数据区域-->
      <div class="own-list-table-box">
        <!-- 表格内容 -->
        <div class="own-list-table-data-box">
          <el-table ref="table" height="100%" :data="tableData" style="width: 100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
            <el-table-column type="index" width="55" label="序号" />
            <el-table-column prop="foodName" label="小吃名称" width="150"/>
            <el-table-column prop="userName" label="用户" width="120"/>
            <el-table-column prop="userHeadPic" label="用户头像" width="80">
              <template #default="{row}">
                <el-avatar :size="40" :src="getPicUrlByJson(row.userHeadPic, 0)"></el-avatar>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="评分" width="150">
              <template #default="{row}">
                <el-rate v-model="row.score" disabled text-color="#ff9900"></el-rate>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="评论内容" show-overflow-tooltip/>
            <el-table-column prop="images" label="配图" width="150">
              <template #default="{row}">
                <div v-if="row.images" style="display: flex; gap: 5px; justify-content: center;">
                  <el-image 
                    v-for="(img, idx) in getPicArray(row.images).slice(0, 3)" 
                    :key="idx"
                    :src="getPicUrl(img)"
                    :preview-src-list="getPicArray(row.images).map(i => getPicUrl(i))"
                    style="width: 40px; height: 40px"
                    fit="cover">
                  </el-image>
                  <span v-if="getPicArray(row.images).length > 3" style="font-size: 12px; color: #999;">
                    +{{ getPicArray(row.images).length - 3 }}
                  </span>
                </div>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" width="160" label="评论时间">
              <template #default="{row}">
                {{ timestampToYMDHMS(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column fixed="right" v-if="userRole == '1'" label="操作" width="150">
              <template #default="{ row }">
                <span v-if="userRole == '1'" class="span-button" @click="viewDetail(row)">查看详情</span>
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

    <!-- 查看详情弹窗 -->
    <el-dialog title="评论详情" :visible.sync="detailDialog" width="700px">
      <div v-if="currentComment" class="comment-detail">
        <div class="detail-item">
          <label>小吃名称：</label>
          <span>{{ currentComment.foodName }}</span>
        </div>
        <div class="detail-item">
          <label>用户：</label>
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-avatar :size="40" :src="getPicUrlByJson(currentComment.userHeadPic, 0)"></el-avatar>
            <span>{{ currentComment.userName }}</span>
          </div>
        </div>
        <div class="detail-item">
          <label>评分：</label>
          <el-rate v-model="currentComment.score" disabled text-color="#ff9900"></el-rate>
        </div>
        <div class="detail-item">
          <label>评论内容：</label>
          <div class="comment-text">{{ currentComment.content }}</div>
        </div>
        <div class="detail-item" v-if="currentComment.images">
          <label>配图：</label>
          <div style="display: flex; gap: 10px; flex-wrap: wrap;">
            <el-image 
              v-for="(img, idx) in getPicArray(currentComment.images)" 
              :key="idx"
              :src="getPicUrl(img)"
              :preview-src-list="getPicArray(currentComment.images).map(i => getPicUrl(i))"
              style="width: 100px; height: 100px"
              fit="cover">
            </el-image>
          </div>
        </div>
        <div class="detail-item">
          <label>评论时间：</label>
          <span>{{ timestampToYMDHMS(currentComment.createTime) }}</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialog = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import TableUtils from "@/utils/tableUtils";
import request from "@/utils/request";
import config from "@/config/config";
import SearchTop from "@/components/searchTop.vue";
import common from "@/utils/common";

export default {
  name: "FoodCommentList",
  components: { SearchTop},
  mixins: [TableUtils, common],
  data() {
    return {
      delUrl: "/api/food/comment/delete/",
      searchList: [
        {type:'input',name:'关键字',placeholder:'小吃名称、用户名、评论内容',value:'keyword'},
      ],
      detailDialog: false,
      currentComment: null
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
      request({
        url: config.backHost + "/api/food/comment/listPage",
        method: 'POST',
        data: { 
          params: {
            ...searchForm,
            // 告诉后端需要关联查询小吃和用户信息
            needFoodInfo: true,
            needUserInfo: true
          }, 
          pageBean: pageBean 
        }
      }).then(res => {
        if (res.code === 200) {
          // 处理不同的返回数据格式
          let dataList = [];
          if (Array.isArray(res.data)) {
            dataList = res.data;
          } else if (res.data && res.data.records) {
            dataList = res.data.records;
          } else if (res.data && res.data.list) {
            dataList = res.data.list;
          }
          
          // 处理数据，确保头像字段正确
          this.tableData = dataList.map(item => ({
            ...item,
            userHeadPic: item.userHeadPicUrl || item.userHeadPic
          }));
          
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
    viewDetail(row) {
      this.currentComment = row;
      this.detailDialog = true;
    },
    getPicArray(picJson) {
      try {
        return JSON.parse(picJson || '[]');
      } catch (e) {
        return [];
      }
    },
    getPicUrl(pic) {
      if (!pic) return '';
      // 如果 pic 是对象(已解析的 JSON),直接取 id 拼接下载 URL
      if (typeof pic === 'object' && pic.id) {
        return config.downloadUrl + pic.id;
      }
      // 如果 pic 是字符串,直接返回
      return pic;
    }
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/css/table.scss";

.comment-detail {
  .detail-item {
    margin-bottom: 20px;
    display: flex;
    align-items: flex-start;
    
    label {
      font-weight: bold;
      color: #606266;
      min-width: 100px;
      flex-shrink: 0;
    }
    
    span {
      color: #303133;
    }
    
    .comment-text {
      flex: 1;
      line-height: 1.8;
      color: #303133;
      white-space: pre-wrap;
    }
  }
}
</style>
