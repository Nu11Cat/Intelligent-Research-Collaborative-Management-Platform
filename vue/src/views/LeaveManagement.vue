<template>
  <div class="leave-management-container">
    <h2>请假管理</h2>
    
    <!-- 待审核请假卡片 -->
    <el-card class="leave-card">
      <template #header>
        <div class="card-header">
          <span>待审核请假</span>
          <div class="date-picker-container">
            <el-date-picker
              v-model="unauditedDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleUnauditedDateChange"
            />
            <el-button type="info" size="small" @click="resetUnauditedDate">重置</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="unauditedLeaves" style="width: 100%">
        <el-table-column prop="userName" label="申请人" width="120" />
        <el-table-column prop="leaveDate" label="请假日期" width="120" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="success" size="small" @click="handleApprove(scope.row)">通过</el-button>
            <el-button type="danger" size="small" @click="handleReject(scope.row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="unauditedCurrentPage"
          v-model:page-size="unauditedPageSize"
          :total="unauditedTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleUnauditedSizeChange"
          @current-change="handleUnauditedCurrentChange"
        />
      </div>
    </el-card>

    <!-- 全部审核信息卡片 -->
    <el-card class="leave-card">
      <template #header>
        <div class="card-header">
          <span>全部审核信息</span>
          <div class="date-picker-container">
            <el-date-picker
              v-model="allDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleAllDateChange"
            />
            <el-button type="info" size="small" @click="resetAllDate">重置</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="allLeaves" style="width: 100%">
        <el-table-column prop="userName" label="申请人" width="120" />
        <el-table-column prop="groupName" label="所属小组" width="120" />
        <el-table-column prop="leaveDate" label="请假日期" width="120" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column prop="approveTime" label="审核时间" width="180" />
        <el-table-column prop="approverName" label="审核人" width="120" />
        <el-table-column prop="approverComment" label="审核意见" />
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="allCurrentPage"
          v-model:page-size="allPageSize"
          :total="allTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleAllSizeChange"
          @current-change="handleAllCurrentChange"
        />
      </div>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      :title="approveType === 'approve' ? '通过请假申请' : '拒绝请假申请'"
      width="500px"
    >
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="申请人">
          <span>{{ approveForm.userName }}</span>
        </el-form-item>
        <el-form-item label="请假时间">
          <span>{{ approveForm.leaveDate }}</span>
        </el-form-item>
        <el-form-item label="请假原因">
          <span>{{ approveForm.reason }}</span>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="approveForm.approverComment"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApprove">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'LeaveManagement',
  data() {
    return {
      // 待审核请假数据
      unauditedLeaves: [],
      unauditedCurrentPage: 1,
      unauditedPageSize: 10,
      unauditedTotal: 0,
      unauditedDateRange: [],
      
      // 全部审核信息数据
      allLeaves: [],
      allCurrentPage: 1,
      allPageSize: 10,
      allTotal: 0,
      allDateRange: [],

      // 审核对话框数据
      approveDialogVisible: false,
      approveType: 'approve', // 'approve' 或 'reject'
      approveForm: {
        id: null,
        userName: '',
        leaveDate: '',
        reason: '',
        approverComment: ''
      }
    }
  },
  created() {
    this.fetchUnauditedLeaves()
    this.fetchAllLeaves()
  },
  methods: {
    // 获取待审核请假记录
    async fetchUnauditedLeaves() {
      try {
        const params = {
          page: this.unauditedCurrentPage,
          pageSize: this.unauditedPageSize
        }
        
        if (this.unauditedDateRange && this.unauditedDateRange.length === 2) {
          params.begin = this.unauditedDateRange[0]
          params.end = this.unauditedDateRange[1]
        }
        
        const token = localStorage.getItem('token')
        const response = await axios.get('http://localhost:8080/leave/getUnaudited', { 
          params,
          headers: {
            'token': token
          }
        })
        if (response.data.code === 1) {
          this.unauditedLeaves = response.data.data.rows
          this.unauditedTotal = response.data.data.total
        }
      } catch (error) {
        console.error('获取待审核请假记录失败:', error)
        ElMessage.error('获取待审核请假记录失败')
      }
    },
    
    // 获取全部审核信息
    async fetchAllLeaves() {
      try {
        const params = {
          page: this.allCurrentPage,
          pageSize: this.allPageSize
        }
        
        if (this.allDateRange && this.allDateRange.length === 2) {
          params.begin = this.allDateRange[0]
          params.end = this.allDateRange[1]
        }
        
        const token = localStorage.getItem('token')
        const response = await axios.get('http://localhost:8080/leave/getAll', { 
          params,
          headers: {
            'token': token
          }
        })
        if (response.data.code === 1) {
          this.allLeaves = response.data.data.rows
          this.allTotal = response.data.data.total
        }
      } catch (error) {
        console.error('获取全部审核信息失败:', error)
        ElMessage.error('获取全部审核信息失败')
      }
    },
    
    // 待审核请假分页处理
    handleUnauditedSizeChange(val) {
      this.unauditedPageSize = val
      this.fetchUnauditedLeaves()
    },
    handleUnauditedCurrentChange(val) {
      this.unauditedCurrentPage = val
      this.fetchUnauditedLeaves()
    },
    handleUnauditedDateChange() {
      this.unauditedCurrentPage = 1
      this.fetchUnauditedLeaves()
    },
    
    // 全部审核信息分页处理
    handleAllSizeChange(val) {
      this.allPageSize = val
      this.fetchAllLeaves()
    },
    handleAllCurrentChange(val) {
      this.allCurrentPage = val
      this.fetchAllLeaves()
    },
    handleAllDateChange() {
      this.allCurrentPage = 1
      this.fetchAllLeaves()
    },
    
    // 状态处理
    getStatusType(status) {
      const types = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '待审核',
        1: '已通过',
        2: '已拒绝'
      }
      return texts[status] || '未知状态'
    },
    
    // 审核操作
    handleApprove(row) {
      this.approveType = 'approve'
      this.approveForm = {
        id: row.id,
        userName: row.userName,
        leaveDate: row.leaveDate,
        reason: row.reason,
        approverComment: ''
      }
      this.approveDialogVisible = true
    },
    handleReject(row) {
      this.approveType = 'reject'
      this.approveForm = {
        id: row.id,
        userName: row.userName,
        leaveDate: row.leaveDate,
        reason: row.reason,
        approverComment: ''
      }
      this.approveDialogVisible = true
    },
    
    // 提交审核
    async submitApprove() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.post('http://localhost:8080/leave/approve', {
          id: this.approveForm.id,
          status: this.approveType === 'approve' ? 1 : 2,
          approverComment: this.approveForm.approverComment
        }, {
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('审核成功')
          this.approveDialogVisible = false
          this.fetchUnauditedLeaves()
          this.fetchAllLeaves()
        } else {
          ElMessage.error(response.data.msg || '审核失败')
        }
      } catch (error) {
        console.error('审核失败:', error)
        ElMessage.error('审核失败')
      }
    },
    
    // 重置待审核请假日期筛选
    resetUnauditedDate() {
      this.unauditedDateRange = []
      this.unauditedCurrentPage = 1
      this.fetchUnauditedLeaves()
    },
    
    // 重置全部审核信息日期筛选
    resetAllDate() {
      this.allDateRange = []
      this.allCurrentPage = 1
      this.fetchAllLeaves()
    },

    formatDate(timestamp) {
      const date = new Date(timestamp)
      return date.toLocaleString()
    }
  }
}
</script>

<style scoped>
.leave-management-container {
  padding: 20px;
}

.leave-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.date-picker-container {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style> 