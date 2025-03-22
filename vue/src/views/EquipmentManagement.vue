<template>
  <div class="equipment-container">
    <div class="page-title">
      <el-icon><Box /></el-icon>
      <span>器材借用</span>
    </div>
    <div class="attendance-section">
      <div class="section-header">
        <h3>器材列表</h3>
      </div>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="器材名称">
          <el-input v-model="queryForm.name" placeholder="请输入器材名称" clearable>
            <template #prefix>
              <el-icon><Box /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="器材描述">
          <el-input v-model="queryForm.description" placeholder="请输入器材描述" clearable>
            <template #prefix>
              <el-icon><Document /></el-icon>
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
          >
            <template #prefix>
              <el-icon><Calendar /></el-icon>
            </template>
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button
            v-if="userRole === 'LEADER'"
            type="success"
            @click="handleAdd"
          >
            <el-icon><Plus /></el-icon>
            新增器材
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 器材列表 -->
      <el-table :data="equipmentList" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="imageUrl" label="图片" width="120">
          <template #header>
            <div class="table-header">
              <el-icon><Picture /></el-icon>
              <span>图片</span>
            </div>
          </template>
          <template #default="scope">
            <el-image
              v-if="scope.row.imageUrl"
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]"
              fit="cover"
              style="width: 50px; height: 50px"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="器材名称" width="180">
          <template #header>
            <div class="table-header">
              <el-icon><Box /></el-icon>
              <span>器材名称</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述">
          <template #header>
            <div class="table-header">
              <el-icon><Document /></el-icon>
              <span>描述</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="破旧程度" width="120">
          <template #header>
            <div class="table-header">
              <el-icon><Star /></el-icon>
              <span>破旧程度</span>
            </div>
          </template>
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.level)">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #header>
            <div class="table-header">
              <el-icon><CircleCheck /></el-icon>
              <span>状态</span>
            </div>
          </template>
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? '可借用' : '已借出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #header>
            <div class="table-header">
              <el-icon><Timer /></el-icon>
              <span>创建时间</span>
            </div>
          </template>
          <template #default="scope">
            <el-tag type="success" effect="plain">
              {{ formatDate(scope.row.createdAt) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="400">
          <template #header>
            <div class="table-header">
              <el-icon><Operation /></el-icon>
              <span>操作</span>
            </div>
          </template>
          <template #default="scope">
            <div class="button-group">
              <!-- 组长操作按钮 -->
              <template v-if="userRole === 'LEADER'">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleEdit(scope.row)"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDelete(scope.row)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
              <!-- 所有用户都可以看到的按钮 -->
              <el-button
                v-if="scope.row.status === 0"
                type="primary"
                size="small"
                @click="handleBorrow(scope.row)"
              >
                <el-icon><Box /></el-icon>
                借用
              </el-button>
              <el-button
                v-else
                type="success"
                size="small"
                @click="handleReturn(scope.row)"
              >
                <el-icon><Box /></el-icon>
                归还
              </el-button>
              <el-button
                type="info"
                size="small"
                @click="handleViewRecords(scope.row)"
              >
                借用记录
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑器材对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增器材' : '编辑器材'"
      width="500px"
    >
      <el-form :model="equipmentForm" label-width="100px">
        <el-form-item label="器材名称">
          <el-input v-model="equipmentForm.name" />
        </el-form-item>
        <el-form-item label="器材描述">
          <el-input v-model="equipmentForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="器材等级">
          <el-select v-model="equipmentForm.level" placeholder="请选择等级">
            <el-option label="崭新出厂" :value="1" />
            <el-option label="略有磨损" :value="2" />
            <el-option label="久经沙场" :value="3" />
            <el-option label="破损不堪" :value="4" />
            <el-option label="战痕累累" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="器材图片">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            name="image"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <img v-if="equipmentForm.imageUrl" :src="equipmentForm.imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="300px"
    >
      <span>确定要删除这个器材吗？此操作不可恢复。</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 借用记录对话框 -->
    <el-dialog
      v-model="recordsDialogVisible"
      title="借用记录"
      width="800px"
      :append-to-body="true"
    >
      <el-table :data="borrowRecords" style="width: 100%" v-loading="recordsLoading">
        <el-table-column prop="name" label="借用人" width="120" />
        <el-table-column prop="groupName" label="所属组别" width="120" />
        <el-table-column prop="borrowTime" label="借用时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.borrowTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnTime" label="归还时间" width="180">
          <template #default="scope">
            {{ scope.row.returnTime ? formatDate(scope.row.returnTime) : '未归还' }}
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="recordsCurrentPage"
          v-model:page-size="recordsPageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="recordsTotal"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleRecordsSizeChange"
          @current-change="handleRecordsCurrentChange"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Plus, Box, Search, Refresh, Calendar, Document, Edit, Delete, Picture, Star, CircleCheck, Timer, Operation } from '@element-plus/icons-vue'

export default {
  name: 'EquipmentManagement',
  components: {
    Plus,
    Box,
    Search,
    Refresh,
    Calendar,
    Document,
    Edit,
    Delete,
    Picture,
    Star,
    CircleCheck,
    Timer,
    Operation
  },
  data() {
    return {
      equipmentList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      queryForm: {
        name: '',
        description: '',
        dateRange: []
      },
      // 借用记录相关
      recordsDialogVisible: false,
      borrowRecords: [],
      recordsLoading: false,
      recordsCurrentPage: 1,
      recordsPageSize: 10,
      recordsTotal: 0,
      currentEquipmentId: null,
      dialogVisible: false,
      dialogType: 'add',
      equipmentForm: {
        name: '',
        description: '',
        level: 1,
        imageUrl: ''
      },
      deleteDialogVisible: false,
      currentEquipment: null,
      userRole: '',
      uploadHeaders: {
        'token': localStorage.getItem('token')
      }
    }
  },
  created() {
    this.getUserRole()
    this.getEquipmentList()
  },
  methods: {
    async getUserRole() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get('http://localhost:8080/user/getRole', {
          headers: {
            'token': token
          }
        })
        if (response.data.code === 1) {
          this.userRole = response.data.data
          console.log('获取到的用户角色:', this.userRole)
        }
      } catch (error) {
        console.error('获取用户角色失败:', error)
      }
    },
    async getEquipmentList() {
      try {
        this.loading = true
        const token = localStorage.getItem('token')
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize
        }
        
        // 添加查询条件
        if (this.queryForm.name) {
          params.name = this.queryForm.name
        }
        if (this.queryForm.description) {
          params.description = this.queryForm.description
        }
        if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
          params.begin = this.queryForm.dateRange[0]
          params.end = this.queryForm.dateRange[1]
        }

        console.log('发送请求参数:', params)
        const response = await axios.get('http://localhost:8080/equipment', {
          params,
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          this.equipmentList = response.data.data.rows
          this.total = response.data.data.total
          console.log('获取到的器材列表:', this.equipmentList)
          console.log('当前用户角色:', this.userRole)
          console.log('是否应该显示编辑按钮:', this.userRole === 'LEADER')
        } else {
          console.error('获取器材列表失败:', response.data)
        }
      } catch (error) {
        console.error('请求错误:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.getEquipmentList()
    },
    handleReset() {
      this.queryForm = {
        name: '',
        description: '',
        dateRange: []
      }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getEquipmentList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getEquipmentList()
    },
    getLevelType(level) {
      const types = {
        1: 'success',    // 崭新出厂
        2: 'info',       // 略有磨损
        3: 'warning',    // 久经沙场
        4: 'danger',     // 破损不堪
        5: 'danger'      // 战痕累累
      }
      return types[level] || 'info'
    },
    getLevelText(level) {
      const texts = {
        1: '崭新出厂',
        2: '略有磨损',
        3: '久经沙场',
        4: '破损不堪',
        5: '战痕累累'
      }
      return texts[level] || '未知'
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString()
    },
    async handleBorrow(row) {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.post(`http://localhost:8080/equipment/borrow?equipmentId=${row.id}`, null, {
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('借用成功')
          this.getEquipmentList()
        } else {
          ElMessage.error(response.data.msg || '借用失败')
        }
      } catch (error) {
        console.error('请求错误:', error)
        ElMessage.error('借用失败')
      }
    },
    async handleReturn(row) {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.post(`http://localhost:8080/equipment/return?equipmentId=${row.id}`, null, {
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('归还成功')
          this.getEquipmentList()
        } else {
          ElMessage.error(response.data.msg || '归还失败')
        }
      } catch (error) {
        console.error('请求错误:', error)
        ElMessage.error('归还失败')
      }
    },
    async handleViewRecords(row) {
      this.currentEquipmentId = row.id
      this.recordsDialogVisible = true
      await this.getBorrowRecords()
    },
    async getBorrowRecords() {
      try {
        this.recordsLoading = true
        const token = localStorage.getItem('token')
        const params = {
          page: this.recordsCurrentPage,
          pageSize: this.recordsPageSize,
          equipmentId: this.currentEquipmentId
        }
        
        if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
          params.begin = this.queryForm.dateRange[0]
          params.end = this.queryForm.dateRange[1]
        }

        const response = await axios.get('http://localhost:8080/equipment/borrowRecords', {
          params,
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          this.borrowRecords = response.data.data.rows
          this.recordsTotal = response.data.data.total
        } else {
          console.error('获取借用记录失败:', response.data)
        }
      } catch (error) {
        console.error('请求错误:', error)
      } finally {
        this.recordsLoading = false
      }
    },
    handleRecordsSizeChange(val) {
      this.recordsPageSize = val
      this.getBorrowRecords()
    },
    handleRecordsCurrentChange(val) {
      this.recordsCurrentPage = val
      this.getBorrowRecords()
    },
    handleAdd() {
      this.dialogType = 'add'
      this.dialogVisible = true
      this.equipmentForm = {
        name: '',
        description: '',
        level: 1,
        imageUrl: ''
      }
    },
    handleEdit(row) {
      this.dialogType = 'edit'
      this.equipmentForm = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.currentEquipment = row
      this.deleteDialogVisible = true
    },
    async confirmDelete() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.delete(`http://localhost:8080/equipment/delete?equipmentId=${this.currentEquipment.id}`, {
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('删除成功')
          this.deleteDialogVisible = false
          this.getEquipmentList()
        } else {
          ElMessage.error(response.data.msg || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    },
    async handleSubmit() {
      try {
        const token = localStorage.getItem('token')
        const url = this.dialogType === 'add' 
          ? 'http://localhost:8080/equipment/add'
          : 'http://localhost:8080/equipment/update'
        
        const method = this.dialogType === 'add' ? 'put' : 'post'
        
        const response = await axios[method](url, this.equipmentForm, {
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success(this.dialogType === 'add' ? '添加成功' : '修改成功')
          this.dialogVisible = false
          this.getEquipmentList()
        } else {
          ElMessage.error(response.data.msg || (this.dialogType === 'add' ? '添加失败' : '修改失败'))
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(this.dialogType === 'add' ? '添加失败' : '修改失败')
      }
    },
    handleUploadSuccess(response) {
      if (response.code === 1) {
        this.equipmentForm.imageUrl = response.data
        ElMessage.success('上传成功')
      } else {
        ElMessage.error(response.msg || '上传失败')
      }
    },
    handleUploadError() {
      ElMessage.error('上传失败')
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    }
  }
}
</script>

<style scoped>
.equipment-container {
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

.attendance-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.query-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

:deep(.el-form-item) {
  margin-bottom: 0;
  flex: 1;
  min-width: 200px;
}

:deep(.el-input__wrapper),
:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-table) {
  margin-top: 20px;
}

.button-group {
  display: flex;
  gap: 8px;
  justify-content: center;
}

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 15px 20px;
  border-top: 1px solid #ebeef5;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
  transition: all 0.3s;
}

.avatar-uploader:hover .avatar-uploader-icon {
  color: #409EFF;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
  border-radius: 8px;
}

.table-header {
  display: flex;
  align-items: center;
  gap: 4px;
}

.table-header .el-icon {
  font-size: 16px;
  color: #606266;
}
</style> 