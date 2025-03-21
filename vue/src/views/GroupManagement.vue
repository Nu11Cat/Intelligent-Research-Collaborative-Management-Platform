<template>
  <div class="group-management-container">
    <h2>小组管理</h2>
    
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="小组名称">
          <el-input v-model="queryForm.name" placeholder="请输入小组名称" clearable />
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

    <!-- 小组列表 -->
    <div class="group-list">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增小组</el-button>
      </div>
      <el-table :data="groups" style="width: 100%">
        <el-table-column prop="name" label="小组名称" width="180" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="adminAName" label="管理员A" width="120" />
        <el-table-column prop="adminBName" label="管理员B" width="120" />
        <el-table-column prop="adminCName" label="管理员C" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      :append-to-body="true"
    >
      <el-form :model="groupForm" label-width="100px" :rules="rules">
        <el-form-item label="小组名称" prop="name">
          <el-input v-model="groupForm.name" placeholder="请输入小组名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="groupForm.description" type="textarea" placeholder="请输入小组描述" />
        </el-form-item>
        <el-form-item label="管理员A" prop="adminAName">
          <el-input v-model="groupForm.adminAName" placeholder="请输入管理员A的用户名" />
        </el-form-item>
        <el-form-item label="管理员B" prop="adminBName">
          <el-input v-model="groupForm.adminBName" placeholder="请输入管理员B的用户名" />
        </el-form-item>
        <el-form-item label="管理员C" prop="adminCName">
          <el-input v-model="groupForm.adminCName" placeholder="请输入管理员C的用户名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户信息展示 -->
    <div class="user-list-container">
      <h3>用户信息列表</h3>
      <!-- 用户查询表单 -->
      <div class="query-form">
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
      </div>
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
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'GroupManagement',
  data() {
    return {
      groups: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      queryForm: {
        name: '',
        dateRange: []
      },
      dialogVisible: false,
      dialogTitle: '',
      groupForm: {
        id: '',
        name: '',
        description: '',
        adminAName: '',
        adminBName: '',
        adminCName: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入小组名称', trigger: 'blur' }
        ],
        adminAName: [
          { required: true, message: '请输入管理员A', trigger: 'blur' }
        ],
        adminBName: [
          { required: true, message: '请输入管理员B', trigger: 'blur' }
        ],
        adminCName: [
          { required: true, message: '请输入管理员C', trigger: 'blur' }
        ]
      },
      loading: false,
      userList: [],
      userLoading: false,
      userQueryForm: {
        username: '',
        groupName: '',
        role: ''
      }
    }
  },
  created() {
    this.getGroups()
    this.fetchUserList()
  },
  methods: {
    async getGroups() {
      try {
        const token = localStorage.getItem('token')
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize
        }
        
        // 添加查询条件
        if (this.queryForm.name) {
          params.name = this.queryForm.name
        }
        if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
          params.begin = this.queryForm.dateRange[0]
          params.end = this.queryForm.dateRange[1]
        }

        const response = await axios.get('http://localhost:8080/group/getAll', {
          params,
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          // 获取小组列表
          const groups = response.data.data.rows
          this.total = response.data.data.total

          // 获取所有管理员的用户信息
          const adminIds = new Set()
          groups.forEach(group => {
            if (group.adminA) adminIds.add(group.adminA)
            if (group.adminB) adminIds.add(group.adminB)
            if (group.adminC) adminIds.add(group.adminC)
          })

          // 创建一个用户ID到用户名的映射
          const userMap = new Map()
          
          // 获取每个管理员的用户信息
          for (const userId of adminIds) {
            try {
              console.log('正在获取用户信息，userId:', userId)
              const userResponse = await axios.get(`http://localhost:8080/user/getById/${userId}`, {
                headers: {
                  'token': token
                }
              })
              console.log('获取到的用户信息响应:', userResponse.data)
              
              if (userResponse.data.code === 1) {
                console.log('用户数据:', userResponse.data.data)
                if (userResponse.data.data && userResponse.data.data.username) {
                  userMap.set(userId, userResponse.data.data.username)
                  console.log('已保存用户名映射:', userId, '->', userResponse.data.data.username)
                } else {
                  console.log('用户数据格式不正确:', userResponse.data.data)
                }
              } else {
                console.log('获取用户信息失败, code:', userResponse.data.code)
              }
            } catch (error) {
              console.error('获取用户信息失败:', error)
            }
          }

          // 更新小组列表中的管理员信息
          this.groups = groups.map(group => {
            console.log('正在处理小组:', group.id, group.name)
            console.log('管理员ID:', {
              A: group.adminA,
              B: group.adminB,
              C: group.adminC
            })
            console.log('用户名映射:', Array.from(userMap.entries()))
            
            return {
              ...group,
              adminAName: group.adminA && userMap.has(group.adminA) ? userMap.get(group.adminA) : (group.adminA ? '未知用户' : ''),
              adminBName: group.adminB && userMap.has(group.adminB) ? userMap.get(group.adminB) : (group.adminB ? '未知用户' : ''),
              adminCName: group.adminC && userMap.has(group.adminC) ? userMap.get(group.adminC) : (group.adminC ? '未知用户' : '')
            }
          })
        } else {
          console.error('获取小组列表失败:', response.data)
        }
      } catch (error) {
        console.error('请求错误:', error)
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchUserList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchUserList()
    },
    handleQuery() {
      this.currentPage = 1
      this.getGroups()
    },
    resetQuery() {
      this.queryForm = {
        name: '',
        dateRange: []
      }
      this.currentPage = 1
      this.getGroups()
    },
    handleAdd() {
      this.groupForm = {
        id: '',
        name: '',
        description: '',
        adminAName: '',
        adminBName: '',
        adminCName: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.groupForm = {
        id: row.id,
        name: row.name,
        description: row.description,
        adminAName: row.adminAName,
        adminBName: row.adminBName,
        adminCName: row.adminCName
      }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await ElMessageBox.confirm('确定要删除该小组吗？', '提示', {
          type: 'warning'
        })
        
        const token = localStorage.getItem('token')
        const response = await axios.delete('http://localhost:8080/group/delete', {
          data: {
            name: row.name
          },
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('删除成功')
          this.getGroups()
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
        let response
        
        // 构建提交数据
        const submitData = {
          id: this.groupForm.id,
          name: this.groupForm.name,
          description: this.groupForm.description,
          adminAName: this.groupForm.adminAName,
          adminBName: this.groupForm.adminBName,
          adminCName: this.groupForm.adminCName
        }
        
        if (this.groupForm.id) {
          // 编辑
          response = await axios.post('http://localhost:8080/group/update', submitData, {
            headers: {
              'token': token,
              'Content-Type': 'application/json'
            }
          })
        } else {
          // 新增
          response = await axios.put('http://localhost:8080/group/add', submitData, {
            headers: {
              'token': token,
              'Content-Type': 'application/json'
            }
          })
        }
        
        if (response.data.code === 1) {
          ElMessage.success(this.groupForm.id ? '修改成功' : '添加成功')
          this.dialogVisible = false
          this.getGroups()
        } else {
          ElMessage.error(response.data.msg || (this.groupForm.id ? '修改失败' : '添加失败'))
        }
      } catch (error) {
        console.error('请求错误:', error)
        ElMessage.error(this.groupForm.id ? '修改失败' : '添加失败')
      }
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
    }
  }
}
</script>

<style scoped>
.group-management-container {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.group-list {
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

.user-list-container {
  margin-top: 40px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-list-container h3 {
  margin-bottom: 20px;
  color: #333;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 