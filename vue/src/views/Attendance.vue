<template>
  <div class="attendance-container">
    <h2 class="page-title">
      <el-icon><Timer /></el-icon>
      考勤管理
    </h2>

    <!-- 签到签退卡片 -->
    <el-card shadow="hover" class="sign-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">今日考勤</span>
        </div>
      </template>
      
      <div class="sign-content">
        <div class="sign-buttons">
          <el-button 
            type="primary" 
            size="large" 
            @click="handleSignIn"
          >
            <el-icon><CircleCheckFilled /></el-icon>
            签到
          </el-button>
          <el-button 
            type="warning" 
            size="large" 
            @click="handleSignOut"
          >
            <el-icon><CircleCloseFilled /></el-icon>
            签退
          </el-button>
        </div>

        <div class="sign-status">
          <el-alert
            v-if="hasSignedIn"
            title="今日已签到"
            type="success"
            :closable="false"
            show-icon
            class="status-alert"
          />
          <el-alert
            v-if="hasSignedOut"
            title="今日已签退"
            type="info"
            :closable="false"
            show-icon
            class="status-alert"
          />
        </div>

        <div class="sign-times" v-if="todayRecord">
          <div class="time-item">
            <el-icon><Timer /></el-icon>
            <span class="label">签到时间：</span>
            <span class="value">{{ todayRecord.checkIn ? new Date(todayRecord.checkIn).toLocaleString() : '未签到' }}</span>
          </div>
          <div class="time-item">
            <el-icon><Timer /></el-icon>
            <span class="label">签退时间：</span>
            <span class="value">{{ todayRecord.checkOut ? new Date(todayRecord.checkOut).toLocaleString() : '未签退' }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 考勤记录卡片 -->
    <el-card shadow="hover" class="record-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">考勤记录</span>
          <el-button type="primary" @click="showLeaveDialog">
            <el-icon><Calendar /></el-icon>
            申请请假
          </el-button>
        </div>
      </template>

      <div class="query-form">
        <el-form :inline="true" :model="queryForm" class="demo-form-inline">
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
        </el-form>
      </div>

      <el-table :data="records" style="width: 100%" border v-loading="loading" :stripe="true">
        <el-table-column prop="checkIn" label="日期" min-width="150" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ scope.row.checkIn ? new Date(scope.row.checkIn).toISOString().split('T')[0] : '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkIn" label="签到时间" min-width="200" align="center">
          <template #default="scope">
            <el-tag 
              size="small" 
              :type="scope.row.checkIn ? 'success' : 'danger'"
              effect="light"
            >
              {{ scope.row.checkIn ? new Date(scope.row.checkIn).toLocaleString() : '未签到' }}
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
    </el-card>

    <!-- 请假记录卡片 -->
    <el-card shadow="hover" class="leave-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">请假记录</span>
        </div>
      </template>

      <el-table :data="leaveRecords" style="width: 100%" border v-loading="loading">
        <el-table-column prop="leaveDate" label="请假日期" width="120" align="center">
          <template #default="scope">
            {{ scope.row.leaveDate }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getLeaveStatusType(scope.row.status)" size="small">
              {{ getLeaveStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ scope.row.createTime ? new Date(scope.row.createTime).toLocaleString() : '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveTime" label="审核时间" width="160" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ scope.row.approveTime ? new Date(scope.row.approveTime).toLocaleString() : '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approver" label="审核人" width="120" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ scope.row.approverName || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approverComment" label="审核意见" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="leaveCurrentPage"
          v-model:page-size="leavePageSize"
          :total="leaveTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleLeaveSizeChange"
          @current-change="handleLeaveCurrentChange"
        />
      </div>
    </el-card>
  </div>

  <!-- 请假申请对话框 -->
  <el-dialog
    v-model="leaveDialogVisible"
    title="请假申请"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form :model="leaveForm" label-width="100px">
      <el-form-item label="请假日期">
        <el-date-picker
          v-model="leaveForm.leaveDate"
          type="date"
          placeholder="选择请假日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="请假原因">
        <el-input
          v-model="leaveForm.reason"
          type="textarea"
          :rows="4"
          placeholder="请输入请假原因"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="leaveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLeave">提交</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import axios from 'axios'
import {
  Timer,
  CircleCheckFilled,
  CircleCloseFilled,
  Calendar,
  Search,
  Refresh
} from '@element-plus/icons-vue'

export default {
  name: 'AttendanceView',
  components: {
    Timer,
    CircleCheckFilled,
    CircleCloseFilled,
    Calendar,
    Search,
    Refresh
  },
  data() {
    return {
      hasSignedIn: false,
      hasSignedOut: false,
      todayRecord: null,
      // 查询表单数据
      queryForm: {
        page: 1,
        pageSize: 10,
        dateRange: [], // 添加日期范围
        begin: '',
        end: ''
      },
      // 表格数据
      records: [],
      total: 0,
      page: 1,
      pageSize: 10,
      
      // 请假相关数据
      leaveDialogVisible: false,
      leaveForm: {
        leaveDate: '',
        reason: ''
      },
      leaveRecords: [],
      leaveCurrentPage: 1,
      leavePageSize: 10,
      leaveTotal: 0
    }
  },
  created() {
    this.getTodayRecord()
    this.getRecords() // 获取考勤记录
    this.fetchLeaveRecords()
  },
  methods: {
    async handleSignIn() {
      try {
        console.log('开始签到...')
        const response = await request.post('/attendance/signIn')
        console.log('签到响应:', response.data)
        if (response.data.code === 1) {
          ElMessage.success('签到成功')
          console.log('签到成功，更新状态...')
          this.hasSignedIn = true
          // 刷新今日记录
          console.log('开始刷新今日记录...')
          await this.getTodayRecord()
          console.log('今日记录刷新完成')
          // 刷新考勤记录列表
          console.log('开始刷新考勤记录列表...')
          await this.getRecords()
          console.log('考勤记录列表刷新完成')
        } else {
          ElMessage.warning(response.data.msg)
        }
      } catch (error) {
        console.error('签到错误:', error)
        ElMessage.error('签到失败')
      }
    },
    async handleSignOut() {
      try {
        console.log('开始签退...')
        const response = await request.post('/attendance/signOut')
        console.log('签退响应:', response.data)
        if (response.data.code === 1) {
          ElMessage.success('签退成功')
          console.log('签退成功，更新状态...')
          this.hasSignedOut = true
          // 刷新今日记录
          console.log('开始刷新今日记录...')
          await this.getTodayRecord()
          console.log('今日记录刷新完成')
          // 刷新考勤记录列表
          console.log('开始刷新考勤记录列表...')
          await this.getRecords()
          console.log('考勤记录列表刷新完成')
        } else {
          ElMessage.warning(response.data.msg)
        }
      } catch (error) {
        console.error('签退错误:', error)
        ElMessage.error('签退失败')
      }
    },
    async getTodayRecord() {
      try {
        // 格式化日期为 yyyy-MM-dd
        const today = new Date()
        const year = today.getFullYear()
        const month = String(today.getMonth() + 1).padStart(2, '0')
        const day = String(today.getDate()).padStart(2, '0')
        const formattedDate = `${year}-${month}-${day}`
        
        console.log('获取今日记录，日期:', formattedDate)
        const response = await request.get('/attendance/myRecords', {
          params: {
            page: 1,
            pageSize: 10,  // 使用默认值10
            begin: formattedDate,
            end: formattedDate
          }
        })
        console.log('今日记录响应:', response.data)
        
        if (response.data.code === 1) {
          const records = response.data.data.rows
          // 查找今天的记录
          const todayRecord = records.find(record => {
            const recordDate = new Date(record.checkIn).toISOString().split('T')[0]
            return recordDate === formattedDate
          })
          
          console.log('找到的今日记录:', todayRecord)
          if (todayRecord) {
            this.todayRecord = todayRecord
            this.hasSignedIn = true
            this.hasSignedOut = !!todayRecord.checkOut
            console.log('更新状态 - hasSignedIn:', this.hasSignedIn, 'hasSignedOut:', this.hasSignedOut)
          } else if (!this.hasSignedIn) {  // 只有在未签到状态下才重置
            this.todayRecord = null
            this.hasSignedIn = false
            this.hasSignedOut = false
            console.log('未找到今日记录，重置状态')
          } else {
            console.log('已签到但未找到记录，保持状态')
          }
        } else {
          console.log('获取记录失败，保持状态')
        }
      } catch (error) {
        console.error('获取考勤记录错误:', error)
        // 发生错误时保持状态
        console.log('发生错误，保持状态')
      }
    },
    async getRecords() {
      try {
        const response = await request.get('/attendance/myRecords', {
          params: {
            page: this.page,
            pageSize: this.pageSize,
            begin: this.queryForm.begin,
            end: this.queryForm.end
          }
        })
        
        if (response.data.code === 1) {
          console.log('考勤记录响应:', response.data)
          this.records = response.data.data.rows
          this.total = response.data.data.total
          console.log('处理后的记录:', this.records)
        } else {
          ElMessage.warning(response.data.msg)
        }
      } catch (error) {
        console.error('获取考勤记录错误:', error)
        ElMessage.error('获取考勤记录失败')
      }
    },
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
      this.getRecords()
    },
    // 重置查询
    resetQuery() {
      this.queryForm.dateRange = []
      this.queryForm.begin = ''
      this.queryForm.end = ''
      this.page = 1 // 重置页码
      this.getRecords()
    },
    // 分页大小改变
    handleSizeChange(val) {
      this.pageSize = val
      this.getRecords()
    },
    // 页码改变
    handleCurrentChange(val) {
      this.page = val
      this.getRecords()
    },

    // 显示请假申请对话框
    showLeaveDialog() {
      this.leaveForm = {
        leaveDate: '',
        reason: ''
      }
      this.leaveDialogVisible = true
    },

    // 提交请假申请
    async submitLeave() {
      if (!this.leaveForm.leaveDate || !this.leaveForm.reason) {
        ElMessage.warning('请填写完整的请假信息')
        return
      }

      try {
        const token = localStorage.getItem('token')
        const response = await axios.post('http://localhost:8080/leave/apply', this.leaveForm, {
          headers: {
            'token': token
          }
        })

        if (response.data.code === 1) {
          ElMessage.success('请假申请提交成功')
          this.leaveDialogVisible = false
          this.fetchLeaveRecords()
        } else {
          ElMessage.error(response.data.msg || '请假申请提交失败')
        }
      } catch (error) {
        console.error('请假申请提交失败:', error)
        ElMessage.error('请假申请提交失败')
      }
    },

    // 获取个人请假记录
    async fetchLeaveRecords() {
      try {
        const params = {
          page: this.leaveCurrentPage,
          pageSize: this.leavePageSize
        }
        
        const token = localStorage.getItem('token')
        const response = await axios.get('http://localhost:8080/leave/getMyLeave', {
          params,
          headers: {
            'token': token
          }
        })

        if (response.data.code === 1) {
          this.leaveRecords = response.data.data.rows
          this.leaveTotal = response.data.data.total
        }
      } catch (error) {
        console.error('获取请假记录失败:', error)
        ElMessage.error('获取请假记录失败')
      }
    },

    // 请假记录分页处理
    handleLeaveSizeChange(val) {
      this.leavePageSize = val
      this.fetchLeaveRecords()
    },
    handleLeaveCurrentChange(val) {
      this.leaveCurrentPage = val
      this.fetchLeaveRecords()
    },

    // 请假状态处理
    getLeaveStatusType(status) {
      const types = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return types[status] || 'info'
    },
    getLeaveStatusText(status) {
      const texts = {
        0: '待审核',
        1: '已通过',
        2: '已拒绝'
      }
      return texts[status] || '未知状态'
    },

    // 获取状态类型
    getStatusType(status) {
      const types = {
        '已签到': 'success',
        '已签退': 'info',
        '未签到': 'warning',
        '未签退': 'warning'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        '已签到': '已签到',
        '已签退': '已签退',
        '未签到': '未签到',
        '未签退': '未签退'
      }
      return texts[status] || status
    }
  }
}
</script>

<style scoped>
.attendance-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
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

.sign-card,
.record-card,
.leave-card {
  margin-bottom: 20px;
  border: none;
  transition: all 0.3s ease;
}

.sign-card:hover,
.record-card:hover,
.leave-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.sign-content {
  padding: 20px;
}

.sign-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.sign-buttons .el-button {
  padding: 12px 30px;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.sign-status {
  max-width: 400px;
  margin: 0 auto 20px;
}

.status-alert {
  margin-bottom: 10px;
}

.sign-times {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.time-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 16px;
}

.time-item:last-child {
  margin-bottom: 0;
}

.time-item .el-icon {
  color: #409EFF;
}

.label {
  color: #606266;
}

.value {
  color: #409EFF;
  font-weight: 500;
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
  align-items: center;
  gap: 10px;
}

.el-form-item {
  margin-bottom: 0;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fff;
}

:deep(.el-card__body) {
  padding: 20px;
  background-color: #fff;
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
</style> 