<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <div class="navbar">
      <div class="navbar-left">
        <div class="logo">
          <span>研智协同管理平台</span>
        </div>
      </div>
      <div class="navbar-right">
        <button @click="handleUpdateLog" class="update-btn">关于更新</button>
        <button @click="handleAbout" class="about-btn">关于系统</button>
        <div class="user-info">
          <el-tag size="small" :type="getRoleType(userRole)">{{ getRoleText(userRole) }}</el-tag>
          <span>{{ username }}</span>
          <button @click="showChangePasswordDialog" class="change-pwd-btn">修改密码</button>
          <button @click="handleLogout" class="logout-btn">退出登录</button>
        </div>
      </div>
    </div>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleChangePassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <div class="main-content">
      <!-- 左侧菜单栏 -->
      <div class="side-menu">
        <div class="menu-content">
          <template v-for="(item, index) in mainMenuItems" :key="index">
            <div v-if="item.type === 'divider'" class="menu-divider"></div>
            <div v-else
                 class="menu-item"
                 :class="{ active: currentPath === item.path }"
                 @click="handleMenuClick(item.path)">
              {{ item.title }}
            </div>
          </template>
        </div>
        <div class="menu-footer">
          <div class="menu-item"
               :class="{ active: currentPath === '/feedback' }"
               @click="handleMenuClick('/feedback')">
            我要反馈
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区 -->
      <div class="content-area">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
          <div v-if="$route.meta.description" class="development-notice">
            {{ $route.meta.description }}
          </div>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script>
import eventBus from '../utils/eventBus'
import axios from 'axios'
import {
  House,
  User,
  Folder,
  Box,
  Bell,
  Setting,
  DataLine
} from '@element-plus/icons-vue'

export default {
  name: 'MainLayout',
  components: {
    House,
    User,
    Folder,
    Box,
    Bell,
    Setting,
    DataLine
  },
  data() {
    return {
      username: '',
      currentPath: this.$route.path,
      mainMenuItems: [],
      activeMenu: this.$route.path,
      userRole: '',
      groupName: '',
      passwordDialogVisible: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      passwordFormRef: null
    }
  },
  created() {
    this.getUserInfo()
    // 根据用户角色设置菜单项
    const role = localStorage.getItem('userRole')
    switch(role) {
      case 'USER':
        this.mainMenuItems = [
          { title: '考勤', path: '/attendance' },
          { title: '器材借用', path: '/equipment' },
          { title: '发布招募', path: '/recruitment' },
          { title: '资源共享', path: '/resource-sharing' },
          { title: '历史公告', path: '/history-announcement' }
        ]
        break
      case 'LEADER':
        this.mainMenuItems = [
          { title: '数据统计', path: '/statistics' },
          { title: '考勤', path: '/attendance' },
          { title: '器材借用', path: '/equipment' },
          { title: '发布招募', path: '/recruitment' },
          { title: '小组管理', path: '/group' },
          { title: '请假管理', path: '/leave-management' },
          { title: '资源共享', path: '/resource-sharing' },
          { title: '历史公告', path: '/history-announcement' }
        ]
        break
      case 'ADMIN':
        this.mainMenuItems = [
          { title: '控制台', path: '/console' },
          { title: '考勤管理', path: '/admin' },
          { title: '预注册用户管理', path: '/pre-registered-user-management' },
          { title: '小组管理', path: '/group-management' }
        ]
        break
    }

    // 监听导航更新事件
    eventBus.on('updateNav', (path) => {
      this.currentPath = path
    })
  },
  beforeUnmount() {
    // 移除事件监听
    eventBus.off('updateNav')
  },
  methods: {
    handleMenuClick(path) {
      this.currentPath = path
      this.$router.push(path)
    },
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('username')
      this.$router.push('/login')
    },
    handleAbout() {
      this.$router.push('/about')
    },
    handleUpdateLog() {
      this.$router.push('/update-log')
    },
    getRoleType(role) {
      const types = {
        'ADMIN': 'danger',
        'USER': 'primary',
        'LEADER': 'warning'
      }
      return types[role] || 'info'
    },
    getRoleText(role) {
      const texts = {
        'ADMIN': '管理员',
        'USER': '普通用户',
        'LEADER': '组长'
      }
      return texts[role] || '未知角色'
    },
    async getUserInfo() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get('http://localhost:8080/user/onself', {
          headers: {
            'token': token
          }
        })
        
        if (response.data.code === 1) {
          const userData = response.data.data
          this.username = userData.username
          this.userRole = userData.role
          this.groupName = userData.groupName
        } else {
          console.error('获取用户信息失败:', response.data)
        }
      } catch (error) {
        console.error('获取用户信息请求错误:', error)
      }
    },
    showChangePasswordDialog() {
      this.passwordDialogVisible = true
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    },
    async handleChangePassword() {
      try {
        await this.$refs.passwordFormRef.validate()
        const response = await axios.post('http://localhost:8080/user/updatePassword', this.passwordForm, {
          headers: {
            'token': localStorage.getItem('token')
          }
        })
        
        if (response.data.code === 1) {
          this.$message.success('密码修改成功')
          this.passwordDialogVisible = false
          this.passwordForm = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          }
        } else {
          this.$message.error(response.data.msg || '密码修改失败')
        }
      } catch (error) {
        if (error.response) {
          this.$message.error(error.response.data.msg || '密码修改失败')
        } else {
          this.$message.error('密码修改失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  height: 80px;
  background: linear-gradient(to right, #409eff, #66b1ff);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  font-size: 28px;
  font-weight: bold;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.navbar-right button {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.update-btn, .about-btn, .logout-btn {
  background: rgba(255, 255, 255, 0.2);
}

.update-btn:hover, .about-btn:hover, .logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
}

.el-tag {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
}

.main-content {
  display: flex;
  margin-top: 80px;
  flex: 1;
}

.side-menu {
  width: 280px;
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  position: fixed;
  top: 80px;
  bottom: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e6e6e6;
}

.menu-content {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.menu-footer {
  padding: 20px 0;
  background: #fff;
  border-top: 1px solid #f0f0f0;
}

.menu-item {
  padding: 16px 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #606266;
  font-size: 15px;
  position: relative;
}

.menu-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.menu-item.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

.menu-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #409eff;
  border-radius: 0 2px 2px 0;
}

.content-area {
  flex: 1;
  margin-left: 280px;
  padding: 30px;
  background: #f5f7fa;
  min-height: calc(100vh - 80px);
}

.development-notice {
  color: #909399;
  font-size: 14px;
  margin: 20px;
  font-style: italic;
  text-align: center;
}

.menu-divider {
  height: 1px;
  background-color: #e6e6e6;
  margin: 8px 0;
}

.change-pwd-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.2);
}

.change-pwd-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 