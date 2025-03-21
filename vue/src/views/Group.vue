<template>
  <div class="group-container">
    <!-- 本组考勤情况 -->
    <el-card class="attendance-container">
      <template #header>
        <div class="card-header">
          <h2>本组考勤情况</h2>
        </div>
      </template>
      
      <!-- 查询表单 -->
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 考勤记录表格 -->
      <el-table :data="attendanceList" style="width: 100%" v-loading="loading">
        <el-table-column prop="userName" label="用户名" />
        <el-table-column prop="groupName" label="小组" />
        <el-table-column prop="checkIn" label="日期" width="180">
          <template #default="scope">
            {{ new Date(scope.row.checkIn).toLocaleDateString() }}
          </template>
        </el-table-column>
        <el-table-column prop="checkIn" label="签到时间" width="180">
          <template #default="scope">
            {{ scope.row.checkIn ? new Date(scope.row.checkIn).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="checkOut" label="签退时间" width="180">
          <template #default="scope">
            {{ scope.row.checkOut ? new Date(scope.row.checkOut).toLocaleString() : '-' }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 人员查看部分 -->
    <el-card class="user-list-container">
      <template #header>
        <div class="card-header">
          <h2>人员查看</h2>
        </div>
      </template>
      
      <!-- 用户查询表单 -->
      <el-form :inline="true" :model="userQueryForm" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input v-model="userQueryForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="小组">
          <el-input v-model="userQueryForm.groupName" placeholder="请输入小组名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button 
            :type="userQueryForm.role === 'USER' ? 'primary' : ''" 
            @click="handleRoleFilter('USER')"
          >只显示组员</el-button>
          <el-button 
            :type="userQueryForm.role === 'LEADER' ? 'primary' : ''" 
            @click="handleRoleFilter('LEADER')"
          >只显示组长</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUserQuery">查询</el-button>
          <el-button @click="resetUserQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="userList" style="width: 100%" v-loading="userLoading">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            {{ scope.row.role === 'ADMIN' ? '管理员' : scope.row.role === 'LEADER' ? '组长' : '组员' }}
          </template>
        </el-table-column>
        <el-table-column prop="groupName" label="所属小组" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="scope">
            {{ new Date(scope.row.updatedAt).toLocaleString() }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'GroupView',
  data() {
    return {
      queryForm: {
        dateRange: []
      },
      attendanceList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      userList: [],
      userLoading: false,
      userQueryForm: {
        username: '',
        groupName: '',
        role: ''
      }
    }
  },
  methods: {
    async fetchAttendanceList() {
      try {
        this.loading = true
        const token = localStorage.getItem('token')
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize
        }
        
        if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
          params.begin = this.queryForm.dateRange[0]
          params.end = this.queryForm.dateRange[1]
        }

        const response = await axios.get('http://localhost:8080/attendance/groupRecords', {
          params,
          headers: {
            'token': token
          }
        })

        if (response.data.code === 1) {
          this.attendanceList = response.data.data.rows
          this.total = response.data.data.total
        }
      } catch (error) {
        console.error('获取考勤记录失败:', error)
        ElMessage.error('获取考勤记录失败')
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.currentPage = 1
      this.fetchAttendanceList()
    },
    resetQuery() {
      this.queryForm.dateRange = []
      this.currentPage = 1
      this.fetchAttendanceList()
    },
    async fetchUserList() {
      try {
        this.userLoading = true
        const token = localStorage.getItem('token')
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          ...this.userQueryForm
        }
        const response = await axios.get('http://localhost:8080/user/getAll', {
          params,
          headers: {
            'token': token
          }
        })
        if (response.data.code === 1) {
          this.userList = response.data.data.rows
          this.total = response.data.data.total
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      } finally {
        this.userLoading = false
      }
    },
    handleUserQuery() {
      this.currentPage = 1
      this.fetchUserList()
    },
    resetUserQuery() {
      this.userQueryForm = {
        username: '',
        groupName: '',
        role: ''
      }
      this.currentPage = 1
      this.fetchUserList()
    },
    handleRoleFilter(role) {
      this.userQueryForm.role = this.userQueryForm.role === role ? '' : role
      this.handleUserQuery()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchUserList()
      this.fetchAttendanceList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchUserList()
      this.fetchAttendanceList()
    }
  },
  mounted() {
    this.fetchUserList()
    this.fetchAttendanceList()
  }
}
</script>

<style scoped>
.group-container {
  padding: 20px;
}

.attendance-container {
  margin-bottom: 20px;
}

.user-list-container {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style> 