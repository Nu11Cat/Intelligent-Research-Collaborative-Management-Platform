<template>
  <div class="admin-container">
    <h2 class="page-title">
      <el-icon><Setting /></el-icon>
      考勤管理
    </h2>
    
    <!-- 全部考勤记录查看 -->
    <div class="attendance-section">
      <div class="section-header">
        <h3>全部考勤记录</h3>
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
      <div class="query-form">
        <el-form :inline="true" :model="queryForm" class="demo-form-inline">
          <div class="form-row">
            <el-form-item label="用户名">
              <el-input 
                v-model="queryForm.username" 
                placeholder="请输入用户名" 
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="组别名称">
              <el-input 
                v-model="queryForm.groupName" 
                placeholder="请输入组别名称" 
                clearable
              >
                <template #prefix>
                  <el-icon><FolderOpened /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="queryForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                @change="handleDateRangeChange"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
              <el-button @click="resetQuery">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </div>
        </el-form>
      </div>
      
      <el-table 
        :data="attendanceRecords" 
        style="width: 100%" 
        border
        v-loading="loading"
        :stripe="true"
      >
        <el-table-column prop="checkIn" label="日期" min-width="150" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ scope.row.checkIn ? new Date(new Date(scope.row.checkIn).getTime() + 8 * 60 * 60 * 1000).toISOString().split('T')[0] : '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户名" min-width="150" align="center" />
        <el-table-column prop="groupName" label="组别名称" min-width="150" align="center" />
        <el-table-column prop="checkIn" label="签到时间" min-width="150" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ scope.row.checkIn ? new Date(scope.row.checkIn).toISOString().split('T')[0] : '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkOut" label="签退时间" min-width="200" align="center">
          <template #default="scope">
            <el-tag 
              size="small" 
              :type="scope.row.checkOut ? 'success' : 'warning'"
              effect="light"
            >
              {{ scope.row.checkOut ? new Date(scope.row.checkOut).toLocaleString() : '未签退' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isLeave" label="请假状态" min-width="120" align="center">
          <template #default="scope">
            <el-tag 
              size="small" 
              :type="scope.row.isLeave ? 'danger' : 'success'"
              effect="light"
            >
              {{ scope.row.isLeave ? '已请假' : '未请假' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import {
  User,
  FolderOpened,
  Calendar,
  Search,
  Refresh,
  Setting
} from '@element-plus/icons-vue'

export default {
  name: 'AdminView',
  components: {
    User,
    FolderOpened,
    Calendar,
    Search,
    Refresh,
    Setting
  },
  data() {
    return {
      loading: false,
      // 查询表单数据
      queryForm: {
        username: '',
        groupName: '',
        dateRange: [],
        begin: '',
        end: '',
        isLeave: ''
      },
      // 表格数据
      attendanceRecords: [],
      total: 0,
      page: 1,
      pageSize: 10
    }
  },
  created() {
    // 设置默认日期范围为今天
    const today = new Date()
    const year = today.getFullYear()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    const formattedDate = `${year}-${month}-${day}`
    this.queryForm.dateRange = [formattedDate, formattedDate]
    this.queryForm.begin = formattedDate
    this.queryForm.end = formattedDate
    
    // 获取考勤记录
    this.getAttendanceRecords()
  },
  methods: {
    // 日期范围改变
    handleDateRangeChange(val) {
      if (val) {
        this.queryForm.begin = val[0]
        this.queryForm.end = val[1]
      } else {
        this.queryForm.begin = ''
        this.queryForm.end = ''
      }
    },
    // 查询按钮点击事件
    handleQuery() {
      this.page = 1 // 重置页码
      this.getAttendanceRecords()
    },
    // 重置查询
    resetQuery() {
      this.queryForm.username = ''
      this.queryForm.groupName = ''
      this.queryForm.dateRange = []
      this.queryForm.begin = ''
      this.queryForm.end = ''
      this.queryForm.isLeave = ''
      this.page = 1 // 重置页码
      this.getAttendanceRecords()
    },
    // 获取考勤记录
    async getAttendanceRecords() {
      try {
        const response = await request.get('/attendance/allRecords', {
          params: {
            page: this.page,
            pageSize: this.pageSize,
            username: this.queryForm.username,
            groupName: this.queryForm.groupName,
            begin: this.queryForm.begin,
            end: this.queryForm.end,
            isLeave: this.queryForm.isLeave
          }
        })
        
        if (response.data.code === 1) {
          console.log('后端返回的原始数据:', response.data)
          console.log('考勤记录数据:', response.data.data.rows)
          this.attendanceRecords = response.data.data.rows
          this.total = response.data.data.total
        } else {
          ElMessage.warning(response.data.msg)
        }
      } catch (error) {
        console.error('获取考勤记录错误:', error)
        ElMessage.error('获取考勤记录失败')
      }
    },
    // 分页大小改变
    handleSizeChange(val) {
      this.pageSize = val
      this.getAttendanceRecords()
    },
    // 页码改变
    handleCurrentChange(val) {
      this.page = val
      this.getAttendanceRecords()
    },
    // 刷新数据
    refreshData() {
      this.getAttendanceRecords()
    }
  }
}
</script>

<style scoped>
.admin-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 30px;
  font-size: 24px;
  color: #303133;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-left: 15px;
  border-left: 4px solid #409EFF;
}

.page-title .el-icon {
  font-size: 24px;
  color: #409EFF;
}

.attendance-section {
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.query-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.demo-form-inline {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.form-row:first-child {
  flex: 1;
}

.form-row:last-child {
  justify-content: center;
}

.el-form-item {
  margin-bottom: 0;
  flex: 1;
  min-width: 200px;
}

:deep(.el-input__wrapper),
:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  margin-top: 10px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  padding: 12px 0;
}

:deep(.el-table td) {
  padding: 12px 0;
  color: #606266;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafafa;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-table .cell) {
  padding: 0 12px;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 12px;
  height: 24px;
  line-height: 22px;
  font-size: 12px;
}

:deep(.el-tag--info) {
  background-color: #f4f4f5;
  border-color: #e9e9eb;
  color: #909399;
}

.pagination {
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
}

:deep(.el-pagination) {
  justify-content: flex-end;
}

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style> 