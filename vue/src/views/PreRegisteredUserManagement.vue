<template>
  <div class="pre-registered-user-container">
    <h2>预注册用户管理</h2>
    
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
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
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 预注册用户列表 -->
    <div class="user-list">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增预注册用户</el-button>
      </div>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column label="组别" width="180">
          <template #default="scope">
            {{ groups.find(g => g.id === scope.row.groupId)?.name || scope.row.groupId }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === '未注册' ? 'warning' : 'success'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'PreRegisteredUserManagement',
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
      }
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

        const response = await axios.get('http://localhost:8080/preRegisteredUser/getAll', {
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
        const response = await axios.get('http://localhost:8080/group/getAll', {
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
        const response = await axios.delete('http://localhost:8080/preRegisteredUser/delete', {
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
        const response = await axios.put('http://localhost:8080/preRegisteredUser/add', {
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
}

.query-form {
  margin-bottom: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-list {
  margin-top: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-header {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 