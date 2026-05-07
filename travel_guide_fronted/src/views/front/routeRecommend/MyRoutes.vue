<template>
  <div class="my-routes-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的行程记录</h2>
      <p>查看和管理您的历史行程推荐</p>
      <div class="header-divider"></div>
    </div>

    <!-- 新建行程按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="$router.push({name: 'routeGenerate'})">
        <i class="el-icon-plus"></i> 新建行程
      </el-button>
    </div>

    <!-- 批量操作栏 -->
    <div v-if="selectedRoutes.length > 0" class="batch-action-bar">
      <span class="selected-info">已选中 <strong>{{ selectedRoutes.length }}</strong> 条行程</span>
      <el-button 
        type="danger" 
        icon="el-icon-delete"
        :disabled="selectedRoutes.length === 0"
        @click="batchDelete">
        批量删除
      </el-button>
    </div>

    <!-- 行程列表 -->
    <el-card v-loading="loading">
      <!-- 空状态 -->
      <div v-if="!loading && routeList.length === 0" class="empty-state">
        <i class="el-icon-notebook-2 empty-icon"></i>
        <p class="empty-text">您还没有保存过行程，快去生成一条专属路线吧~</p>
        <el-button type="primary" @click="$router.push({name: 'routeGenerate'})">
          <i class="el-icon-magic-stick"></i> 去生成行程
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table 
        v-else
        :data="routeList" 
        style="width: 100%"
        :row-class-name="tableRowClassName"
        @row-click="handleRowClick"
        @selection-change="handleSelectionChange">
        
        <!-- 复选框列 -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        
        <!-- 行程名称 -->
        <el-table-column prop="recommendationName" label=" 行程名称" width="180" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span class="route-name" @click.stop="viewDetail(scope.row.id)" :title="scope.row.recommendationName">{{ scope.row.recommendationName }}</span>
          </template>
        </el-table-column>

        <!-- 天数 -->
        <el-table-column prop="days" label=" 天数" width="70" align="center">
          <template slot-scope="scope">
            <el-tag 
              :type="getDaysTagType(scope.row.days)" 
              size="small"
              class="days-tag">
              {{ scope.row.days }}天
            </el-tag>
          </template>
        </el-table-column>

        <!-- 预算 -->
        <el-table-column prop="budget" label=" 预算" width="90" align="center">
          <template slot-scope="scope">
            <span class="budget">¥{{ scope.row.budget }}</span>
          </template>
        </el-table-column>

        <!-- 预估费用 -->
        <el-table-column prop="estimatedCost" label=" 预估费用" width="100" align="center">
          <template slot-scope="scope">
            <span :class="getCostClass(scope.row)">¥{{ scope.row.estimatedCost }}</span>
          </template>
        </el-table-column>

        <!-- 景点数 -->
        <el-table-column prop="totalAttractions" label="️ 景点数" width="80" align="center">
          <template slot-scope="scope">
            {{ scope.row.totalAttractions }}个
          </template>
        </el-table-column>

        <!-- 酒店数 -->
        <el-table-column prop="totalHotels" label=" 酒店数" width="80" align="center">
          <template slot-scope="scope">
            {{ scope.row.totalHotels }}家
          </template>
        </el-table-column>

        <!-- 总距离 -->
        <el-table-column prop="totalDistance" label=" 总距离" width="110" align="center">
          <template slot-scope="scope">
            {{ scope.row.totalDistance }}km
          </template>
        </el-table-column>

        <!-- 创建时间 -->
        <el-table-column prop="createTime" label=" 创建时间" width="170" align="center">
          <template slot-scope="scope">
            {{ scope.row.createTime }}
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="️ 操作" width="170" fixed="right" align="center">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button 
                size="small" 
                type="primary"
                plain
                icon="el-icon-view"
                @click.stop="viewDetail(scope.row.id)">
                详情
              </el-button>
              <el-button 
                size="small" 
                type="danger"
                plain
                icon="el-icon-delete"
                class="delete-btn"
                @click.stop="deleteRoute(scope.row.id)">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-container">
        <span class="pagination-info">共 {{ total }} 条，每页 {{ pageSize }} 条</span>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
import config from '@/config/config'

export default {
  name: 'MyRoutes',
  data() {
    return {
      routeList: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      selectedRoutes: []  // 选中的行程列表
    }
  },
  mounted() {
    this.loadRoutes()
  },
  methods: {
    // 加载路线列表
    async loadRoutes() {
      this.loading = true
      try {
        const res = await request({
          url: config.backHost + '/api/routeRecommendation/history',
          method: 'POST',
          data: {
            pageBean: {
              page: this.pageNum,
              pageSize: this.pageSize
            }
          }
        })
        
        if (res.code === 200) {
          if (res.data && Array.isArray(res.data)) {
            this.routeList = res.data
            this.total = res.pageBean ? res.pageBean.total : res.data.length
          } else if (res.pageBean) {
            this.routeList = res.pageBean.data || res.pageBean.list || []
            this.total = res.pageBean.total || 0
          }
        } else {
          this.$message.error(res.msg || '加载失败')
        }
      } catch (error) {
        console.error('加载路线列表失败:', error)
        this.$message.error('加载失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 表格行样式
    tableRowClassName({row, rowIndex}) {
      return 'table-row'
    },

    // 行点击事件
    handleRowClick(row) {
      this.viewDetail(row.id)
    },

    // 选中项变化
    handleSelectionChange(selection) {
      this.selectedRoutes = selection
    },

    // 批量删除
    batchDelete() {
      if (this.selectedRoutes.length === 0) {
        this.$message.warning('请先选择要删除的行程')
        return
      }

      this.$confirm(
        `您确定要删除选中的 ${this.selectedRoutes.length} 条行程吗？删除后将无法恢复。`,
        '提示',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          confirmButtonClass: 'el-button--danger'
        }
      ).then(async () => {
        this.loading = true
        try {
          const ids = this.selectedRoutes.map(item => item.id)
          
          const res = await request({
            url: config.backHost + '/api/routeRecommendation/batchDelete',
            method: 'POST',
            data: { ids }
          })
          
          if (res.code === 200) {
            this.$message.success(`已成功删除 ${this.selectedRoutes.length} 条行程`)
            this.selectedRoutes = []
            this.loadRoutes()
          } else {
            this.$message.error(res.msg || '批量删除失败')
          }
        } catch (error) {
          console.error('批量删除失败:', error)
          this.$message.error('批量删除失败')
        } finally {
          this.loading = false
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    // 获取天数标签类型
    getDaysTagType(days) {
      if (days <= 3) return 'success'  // 1-3天：绿色
      if (days <= 7) return 'warning'  // 4-7天：橙色
      return 'danger'                   // 7天以上：红色
    },

    // 获取费用样式类
    getCostClass(row) {
      if (row.estimatedCost <= row.budget) {
        return 'cost under-budget'  // 预算内：绿色
      } else {
        return 'cost over-budget'   // 超预算：红色
      }
    },

    // 查看详情
    viewDetail(recommendationId) {
      this.$router.push({
        name: 'routeDetail',
        params: { id: recommendationId }
      })
    },

    // 切换收藏状态
    // 已移除收藏功能，只保留查看详情和删除

    // 删除路线
    deleteRoute(recommendationId) {
      this.$confirm('确定要删除这条行程吗？删除后将无法恢复', '提示', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }).then(async () => {
        try {
          const res = await request({
            url: config.backHost + `/api/routeRecommendation/delete/${recommendationId}`,
            method: 'POST'
          })
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.loadRoutes()
          } else {
            this.$message.error(res.msg || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    // 分页大小改变
    handleSizeChange(val) {
      this.pageSize = val
      this.pageNum = 1
      this.loadRoutes()
    },

    // 页码改变
    handleCurrentChange(val) {
      this.pageNum = val
      this.loadRoutes()
    }
  }
}
</script>

<style scoped lang="scss">
.my-routes-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;

  .page-header {
    text-align: center;
    margin-bottom: 20px;
    
    h2 {
      color: #333;
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 10px;
    }
    
    p {
      color: #999;
      font-size: 14px;
      margin-bottom: 15px;
    }
    
    .header-divider {
      height: 1px;
      background-color: #e0e0e0;
      margin: 0 auto;
      width: 100%;
    }
  }

  .action-bar {
    text-align: right;
    margin-bottom: 20px;
    
    .el-button {
      background-color: #FF7A2F;
      border-color: #FF7A2F;
      
      &:hover {
        background-color: #e66a24;
        border-color: #e66a24;
      }
      
      i {
        margin-right: 5px;
      }
    }
  }

  // 批量操作栏
  .batch-action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    margin-bottom: 20px;
    background-color: #fff3e0;
    border: 1px solid #ffcc80;
    border-radius: 4px;
    
    .selected-info {
      font-size: 14px;
      color: #333;
      
      strong {
        color: #FF7A2F;
        font-size: 16px;
      }
    }
    
    .el-button {
      i {
        margin-right: 5px;
      }
    }
  }

  // 空状态
  .empty-state {
    text-align: center;
    padding: 60px 20px;
    
    .empty-icon {
      font-size: 64px;
      color: #ddd;
      margin-bottom: 20px;
    }
    
    .empty-text {
      font-size: 16px;
      color: #999;
      margin-bottom: 20px;
    }
    
    .el-button {
      background-color: #FF7A2F;
      border-color: #FF7A2F;
      
      &:hover {
        background-color: #e66a24;
        border-color: #e66a24;
      }
    }
  }

  // 表格样式
  ::v-deep .el-table {
    .table-row {
      cursor: pointer;
      
      &:hover {
        background-color: #f5f5f5;
      }
    }
    
    .el-table__row {
      height: 48px;
    }
  }

  .route-name {
    color: #409eff;
    font-weight: 500;
    cursor: pointer;
    display: block;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    
    &:hover {
      text-decoration: underline;
    }
  }

  .days-tag {
    &.el-tag--success {
      background-color: #e6f7ff;
      border-color: #91d5ff;
      color: #1890ff;
    }
    
    &.el-tag--warning {
      background-color: #fff7e6;
      border-color: #ffd591;
      color: #fa8c16;
    }
    
    &.el-tag--danger {
      background-color: #fff1f0;
      border-color: #ffa39e;
      color: #f5222d;
    }
  }

  .budget {
    color: #333;
    font-weight: normal;
  }

  .cost {
    font-weight: bold;
    
    &.under-budget {
      color: #27AE60;  // 预算内：绿色
    }
    
    &.over-budget {
      color: #E53E3E;  // 超预算：红色
    }
  }

  // 操作按钮
  ::v-deep .action-buttons {
    display: flex;
    gap: 8px;
    justify-content: center;
    align-items: center;
    
    .el-button {
      margin: 0;
      padding: 7px 12px;
      border-radius: 6px;
      font-size: 13px;
      transition: all 0.3s;
      
      i {
        margin-right: 4px;
      }
      
      // 查看详情按钮 - 幽灵按钮
      &.el-button--primary {
        background-color: transparent !important;
        border: 1px solid #d9ecff !important;
        color: #409eff !important;
        
        &:hover {
          background-color: #ecf5ff !important;
          border-color: #b3d8ff !important;
          color: #409eff !important;
        }
      }
      
      // 删除按钮 - 幽灵按钮
      &.el-button--danger {
        background-color: transparent !important;
        border: 1px solid #fbc4c4 !important;
        color: #f56c6c !important;
        
        &:hover {
          background-color: #fef0f0 !important;
          border-color: #fbc4c4 !important;
          color: #f56c6c !important;
        }
      }
    }
  }

  .delete-btn {
    &:hover {
      animation: shake 0.5s;
    }
  }

  @keyframes shake {
    0%, 100% { transform: translateX(0); }
    25% { transform: translateX(-3px); }
    75% { transform: translateX(3px); }
  }

  // 分页
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .pagination-info {
      font-size: 12px;
      color: #999;
    }
    
    ::v-deep .el-pagination {
      .btn-prev,
      .btn-next,
      .el-pager li {
        &:hover {
          color: #FF7A2F;
        }
      }
      
      .el-pager li.active {
        background-color: #FF7A2F;
        color: #fff;
      }
      
      .btn-prev.is-disabled,
      .btn-next.is-disabled {
        color: #ccc;
        cursor: not-allowed;
      }
    }
  }
}

@media (max-width: 768px) {
  .my-routes-container {
    padding: 10px;
    
    ::v-deep .el-table {
      font-size: 12px;
      
      .el-button--mini {
        padding: 5px 8px;
        font-size: 12px;
      }
    }
  }
}
</style>