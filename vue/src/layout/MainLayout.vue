<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="logo">实验室管理系统</div>
      <div class="nav-buttons">
        <button @click="handleAbout" class="about-btn">关于系统</button>
        <div class="user-info">
          <el-tag size="small" :type="getRoleType(userRole)">{{ getRoleText(userRole) }}</el-tag>
          <span>{{ username }}</span>
          <button @click="handleLogout" class="logout-btn">退出登录</button>
        </div>
      </div>
    </div>
    
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
      groupName: ''
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

.top-nav {
  height: 90px;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.nav-buttons {
  display: flex;
  align-items: center;
  gap: 20px;
}

.about-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background: #409eff;
  color: white;
  cursor: pointer;
  transition: background 0.3s;
  font-size: 16px;
}

.about-btn:hover {
  background: #66b1ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.el-tag {
  margin-right: 4px;
}

.logout-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background: #f56c6c;
  color: white;
  cursor: pointer;
  transition: background 0.3s;
  font-size: 16px;
}

.logout-btn:hover {
  background: #f78989;
}

.main-content {
  display: flex;
  margin-top: 90px;
  flex: 1;
}

.side-menu {
  width: 360px;
  background: #fff;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 90px;
  bottom: 0;
  left: 0;
  display: flex;
  flex-direction: column;
}

.menu-content {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.menu-footer {
  padding: 20px 0;
  background: #fff;
}

.el-menu-vertical {
  border-right: none;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
  padding: 0 20px;
  font-size: 14px;
}

.el-menu-item.is-active {
  background-color: #ecf5ff !important;
  color: #409eff !important;
  border-right: 3px solid #409eff;
}

.el-menu-item:hover {
  background-color: #f5f7fa !important;
}

.content-area {
  flex: 1;
  margin-left: 360px;
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 90px);
}

.menu-item {
  padding: 16px 24px;
  cursor: pointer;
  transition: all 0.3s;
  color: #333;
  font-size: 16px;
}

.menu-item:hover {
  background: #f5f7fa;
}

.menu-item.active {
  background: #ecf5ff;
  color: #409eff;
  border-right: 3px solid #409eff;
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
</style> 