<template>
  <div class="pre-registered-user-container">
    <h2>
      <el-icon><UserFilled /></el-icon>
      预注册用户管理
    </h2>
    
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable>
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="创建时间">
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

    <!-- 预注册用户列表 -->
    <div class="user-list">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增预注册用户
        </el-button>
      </div>
      <el-table 
        :data="users" 
        style="width: 100%" 
        border
        v-loading="loading"
        :stripe="true"
      >
        <el-table-column prop="username" label="用户名" min-width="150" align="center">
          <template #header>
            <el-icon><User /></el-icon>
            <span style="margin-left: 4px">用户名</span>
          </template>
        </el-table-column>
        <el-table-column label="组别" min-width="150" align="center">
          <template #header>
            <el-icon><Folder /></el-icon>
            <span style="margin-left: 4px">组别</span>
          </template>
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ groups.find(g => g.id === scope.row.groupId)?.name || scope.row.groupId }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="120" align="center">
          <template #header>
            <el-icon><InfoFilled /></el-icon>
            <span style="margin-left: 4px">状态</span>
          </template>
          <template #default="scope">
            <el-tag 
              size="small" 
              :type="scope.row.status === '未注册' ? 'warning' : 'success'"
              effect="light"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="180" align="center">
          <template #header>
            <el-icon><Timer /></el-icon>
            <span style="margin-left: 4px">创建时间</span>
          </template>
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ new Date(scope.row.createdAt).toLocaleString() }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" align="center">
          <template #header>
            <el-icon><Operation /></el-icon>
            <span style="margin-left: 4px">操作</span>
          </template>
          <template #default="scope">
            <el-button type="danger" link @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增对话框 -->
    <el-dialog
      title="新增预注册用户"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="组别">
          <el-select v-model="userForm.groupId" placeholder="请选择组别">
            <el-option
              v-for="group in groups"
              :key="group.id"
              :label="group.name"
              :value="group.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  UserFilled,
  Search,
  Refresh,
  Plus,
  Delete,
  Folder,
  InfoFilled,
  Timer,
  Operation,
  Calendar
} from '@element-plus/icons-vue'

export default {
  name: 'PreRegisteredUserManagement',
  components: {
    User,
    UserFilled,
    Search,
    Refresh,
    Plus,
    Delete,
    Folder,
    InfoFilled,
    Timer,
    Operation,
    Calendar
  },
  data() {
    return {
      users: [],
      groups: [], // 用于存储所有小组列表
      currentPage: 1,
      pageSize: 10,
      total: 0,
      queryForm: {
        username: '',
        dateRange: []
      },
      dialogVisible: false,
      userForm: {
        username: '',
        groupId: ''
      },
      loading: false
    }
  },
  created() {
    this.getUsers()
    this.getGroups() // 获取小组列表
  },
  methods: {
    async getUsers() {
      try {
        const token = localStorage.getItem('token')
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize
        }
        
        if (this.queryForm.username) {
          params.username = this.queryForm.username
        }
        
        if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
          params.begin = this.queryForm.dateRange[0]
          params.end = this.queryForm.dateRange[1]
        }

        const response = await request.get('/preRegisteredUser/getAll', {
          params,
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          this.users = response.data.data.rows
          this.total = response.data.data.total
        } else {
          console.error('获取预注册用户列表失败:', response.data)
        }
      } catch (error) {
        console.error('请求错误:', error)
      }
    },
    async getGroups() {
      try {
        const token = localStorage.getItem('token')
        const response = await request.get('/group/getAll', {
          params: {
            page: 1,
            pageSize: 100 // 获取所有小组
          },
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          this.groups = response.data.data.rows
        }
      } catch (error) {
        console.error('获取小组列表失败:', error)
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getUsers()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getUsers()
    },
    handleQuery() {
      this.currentPage = 1
      this.getUsers()
    },
    handleDateRangeChange(val) {
      this.queryForm.dateRange = val
    },
    resetQuery() {
      this.queryForm = {
        username: '',
        dateRange: []
      }
      this.currentPage = 1
      this.getUsers()
    },
    handleAdd() {
      this.userForm = {
        username: '',
        groupId: ''
      }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await ElMessageBox.confirm('确定要删除该预注册用户吗？', '提示', {
          type: 'warning'
        })
        
        const token = localStorage.getItem('token')
        const response = await request.delete('/preRegisteredUser/delete', {
          data: {
            username: row.username
          },
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('删除成功')
          this.getUsers()
        } else {
          ElMessage.error(response.data.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('请求错误:', error)
          ElMessage.error('删除失败')
        }
      }
    },
    async handleSubmit() {
      try {
        const token = localStorage.getItem('token')
        const response = await request.put('/preRegisteredUser/add', {
          username: this.userForm.username,
          groupId: this.userForm.groupId
        }, {
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('添加成功')
          this.dialogVisible = false
          this.getUsers()
        } else {
          ElMessage.error(response.data.msg || '添加失败')
        }
      } catch (error) {
        console.error('请求错误:', error)
        ElMessage.error('添加失败')
      }
    }
  }
}
</script>

<style scoped>
.pre-registered-user-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;

  h2 {
    margin-bottom: 30px;
    font-size: 24px;
    color: #303133;
    font-weight: 600;
    display: flex;
    align-items: center;
    
    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 24px;
      background-color: #409eff;
      margin-right: 12px;
      border-radius: 2px;
    }
  }

  .query-form {
    margin-bottom: 20px;
    background: #ffffff;
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    .el-form-item {
      margin-bottom: 0;
      margin-right: 20px;
    }

    .el-input,
    .el-date-picker {
      width: 240px;
    }

    .el-button {
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
  }

  .user-list {
    background: #ffffff;
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    .table-header {
      margin-bottom: 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      h3 {
        margin: 0;
        font-size: 18px;
        color: #303133;
        font-weight: 600;
      }
    }

    .el-table {
      border-radius: 8px;
      overflow: hidden;

      th {
        background-color: #f5f7fa !important;
        color: #606266;
        font-weight: 600;
      }

      td {
        padding: 12px 0;
      }

      .el-table__row {
        transition: all 0.3s ease;

        &:hover {
          background-color: #f5f7fa !important;
        }
      }
    }

    .el-tag {
      border-radius: 4px;
      padding: 0 8px;
      height: 24px;
      line-height: 24px;
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
      padding: 16px;
      background-color: #ffffff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    }
  }

  .el-dialog {
    border-radius: 8px;
    overflow: hidden;

    .el-dialog__header {
      margin: 0;
      padding: 20px 24px;
      background-color: #f5f7fa;
      border-bottom: 1px solid #ebeef5;

      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
    }

    .el-dialog__body {
      padding: 24px;
    }

    .el-dialog__footer {
      padding: 16px 24px;
      border-top: 1px solid #ebeef5;
    }
  }
}
</style> 