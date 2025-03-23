<template>
  <div class="recruitment-container">
    <div class="page-title">
      <el-icon><Bell /></el-icon>
      <span>团队招募</span>
    </div>
    <div class="attendance-section">
      <div class="section-header">
        <h3>招募列表</h3>
      </div>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="招募标题">
          <el-input v-model="queryForm.title" placeholder="请输入招募标题">
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
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            发布招募
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 招募列表 -->
      <el-table :data="recruitmentList" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="title" label="招募标题">
          <template #header>
            <div class="table-header">
              <el-icon><Document /></el-icon>
              <span>招募标题</span>
            </div>
          </template>
          <template #default="scope">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span style="flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                {{ scope.row.title }}
              </span>
              <el-button type="primary" link @click="showDescriptionDetail(scope.row, 'title')">
                查看详情
              </el-button>
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
          <template #default="scope">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span style="flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                {{ scope.row.description }}
              </span>
              <el-button type="primary" link @click="showDescriptionDetail(scope.row, 'description')">
                查看详情
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系方式" width="180">
          <template #header>
            <div class="table-header">
              <el-icon><Phone /></el-icon>
              <span>联系方式</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布日期" min-width="150" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" type="info">
              {{ scope.row.createTime ? new Date(scope.row.createTime).toISOString().split('T')[0] : '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #header>
            <div class="table-header">
              <el-icon><Operation /></el-icon>
              <span>操作</span>
            </div>
          </template>
          <template #default="scope">
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
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑招募对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布招募' : '编辑招募'"
      width="500px"
    >
      <el-form :model="recruitmentForm" label-width="100px">
        <el-form-item label="招募标题">
          <el-input 
            v-model="recruitmentForm.title" 
            maxlength="50"
            show-word-limit
            placeholder="请输入招募标题（50字以内）"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="recruitmentForm.description" 
            type="textarea" 
            maxlength="100"
            show-word-limit
            :rows="4"
            placeholder="请输入描述（100字以内）"
          />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="recruitmentForm.contact" />
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
      <span>确定要删除这条招募信息吗？此操作不可恢复。</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 描述详情对话框 -->
    <el-dialog
      v-model="descriptionDialogVisible"
      :title="detailType === 'title' ? '标题详情' : '描述详情'"
      width="600px"
    >
      <div style="white-space: pre-wrap; word-break: break-all; line-height: 1.5; padding: 10px; max-height: 400px; overflow-y: auto;">
        {{ currentRecruitment?.[detailType] }}
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="descriptionDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Bell, Search, Refresh, Plus, Calendar, Document, Edit, Delete, Phone, Operation } from '@element-plus/icons-vue'

export default {
  name: 'RecruitmentView',
  components: {
    Bell,
    Search,
    Refresh,
    Plus,
    Calendar,
    Document,
    Edit,
    Delete,
    Phone,
    Operation
  },
  data() {
    return {
      recruitmentList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      queryForm: {
        title: '',
        dateRange: []
      },
      dialogVisible: false,
      dialogType: 'add',
      recruitmentForm: {
        title: '',
        description: '',
        contact: ''
      },
      deleteDialogVisible: false,
      currentRecruitment: null,
      descriptionDialogVisible: false,
      detailType: 'description'
    }
  },
  created() {
    this.getRecruitmentList()
  },
  methods: {
    async getRecruitmentList() {
      try {
        this.loading = true
        const token = localStorage.getItem('token')
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize
        }
        
        if (this.queryForm.title) {
          params.title = this.queryForm.title
        }
        if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
          params.begin = this.formatDate(this.queryForm.dateRange[0])
          params.end = this.formatDate(this.queryForm.dateRange[1])
        }

        const response = await request.get('/teamRecruitment/getAll', {
          params,
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          this.recruitmentList = response.data.data.rows
          this.total = response.data.data.total
        } else {
          ElMessage.error(response.data.msg || '获取招募列表失败')
        }
      } catch (error) {
        console.error('获取招募列表失败:', error)
        ElMessage.error('获取招募列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.getRecruitmentList()
    },
    handleReset() {
      this.queryForm = {
        title: '',
        dateRange: []
      }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getRecruitmentList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getRecruitmentList()
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toISOString().split('T')[0]
    },
    handleAdd() {
      this.dialogType = 'add'
      this.recruitmentForm = {
        title: '',
        description: '',
        contact: ''
      }
      this.dialogVisible = true
    },
    async handleEdit(row) {
      try {
        const token = localStorage.getItem('token')
        const response = await request.get(`/teamRecruitment/getById?teamRecruitmentId=${row.id}`, {
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          this.dialogType = 'edit'
          this.recruitmentForm = response.data.data
          this.dialogVisible = true
        } else {
          ElMessage.error(response.data.msg || '获取招募信息失败')
        }
      } catch (error) {
        console.error('获取招募信息失败:', error)
        ElMessage.error('获取招募信息失败')
      }
    },
    handleDelete(row) {
      this.currentRecruitment = row
      this.deleteDialogVisible = true
    },
    async confirmDelete() {
      try {
        const token = localStorage.getItem('token')
        const response = await request.delete(`/teamRecruitment/delete?teamRecruitmentId=${this.currentRecruitment.id}`, {
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success('删除成功')
          this.deleteDialogVisible = false
          this.getRecruitmentList()
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
          ? '/teamRecruitment/add'
          : '/teamRecruitment/update'
        
        const method = this.dialogType === 'add' ? 'put' : 'post'
        
        const response = await request[method](url, this.recruitmentForm, {
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          }
        })
        
        if (response.data.code === 1) {
          ElMessage.success(this.dialogType === 'add' ? '发布成功' : '修改成功')
          this.dialogVisible = false
          this.getRecruitmentList()
        } else {
          ElMessage.error(response.data.msg || (this.dialogType === 'add' ? '发布失败' : '修改失败'))
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(this.dialogType === 'add' ? '发布失败' : '修改失败')
      }
    },
    showDescriptionDetail(row, type) {
      this.currentRecruitment = row
      this.detailType = type
      this.descriptionDialogVisible = true
    }
  }
}
</script>

<style scoped>
.recruitment-container {
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
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.section-header {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style> 