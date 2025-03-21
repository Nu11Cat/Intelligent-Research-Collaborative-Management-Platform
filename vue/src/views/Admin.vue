<template>
  <div class="admin-container">
    <h2>系统管理</h2>
    
    <!-- 全部考勤记录查看 -->
    <div class="attendance-section">
      <h3>全部考勤记录</h3>
      <div class="query-form">
        <el-form :inline="true" :model="queryForm" class="demo-form-inline">
          <el-form-item label="用户名">
            <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="组别名称">
            <el-input v-model="queryForm.groupName" placeholder="请输入组别名称" clearable />
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
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="attendanceRecords" style="width: 100%" border>
        <el-table-column prop="checkIn" label="日期" width="120">
          <template #default="scope">
            {{ scope.row.checkIn ? new Date(scope.row.checkIn).toISOString().split('T')[0] : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="groupName" label="组别名称" width="120" />
        <el-table-column prop="checkIn" label="签到时间" width="180">
          <template #default="scope">
            {{ scope.row.checkIn ? new Date(scope.row.checkIn).toLocaleString() : '未签到' }}
          </template>
        </el-table-column>
        <el-table-column prop="checkOut" label="签退时间" width="180">
          <template #default="scope">
            {{ scope.row.checkOut ? new Date(scope.row.checkOut).toLocaleString() : '未签退' }}
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

export default {
  name: 'AdminView',
  data() {
    return {
      // 查询表单数据
      queryForm: {
        username: '',
        groupName: '',
        dateRange: [],
        begin: '',
        end: ''
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
            end: this.queryForm.end
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
    }
  }
}
</script>

<style scoped>
.admin-container {
  padding: 20px;
}

.attendance-section {
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.attendance-section h3 {
  color: #333;
  margin-bottom: 20px;
}

.query-form {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.demo-form-inline {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-form-item {
  margin-bottom: 0;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 