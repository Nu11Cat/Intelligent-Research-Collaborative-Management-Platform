<template>
  <router-view></router-view>
  <div class="user-info">
    <el-dropdown trigger="click">
      <span class="user-name">
        {{ userRole === 'admin' ? '管理员' : userRole === 'team_leader' ? '组长' : '组员' }}
        {{ userName }}
        <span v-if="userGroup" class="group-name">({{ userGroup }})</span>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      userRole: '',
      userName: '',
      userGroup: '',
      isLoggedIn: false
    }
  },
  methods: {
    async checkLoginStatus() {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          this.isLoggedIn = false
          return
        }

        const response = await request.get('/user/onself', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (response.ok) {
          const result = await response.json()
          console.log('获取个人信息响应数据:', result)
          if (result.code === 1) {
            const data = result.data
            console.log('用户数据:', data)
            this.userRole = data.role
            this.userName = data.username
            this.userGroup = data.groupName
            this.isLoggedIn = true
          } else {
            this.isLoggedIn = false
          }
        } else {
          this.isLoggedIn = false
        }
      } catch (error) {
        console.error('检查登录状态失败:', error)
        this.isLoggedIn = false
      }
    },
    handleLogout() {
      // 实现退出登录的逻辑
    }
  },
  mounted() {
    this.checkLoginStatus()
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  margin: 0;
  padding: 0;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #333;
}

.group-name {
  color: #666;
  font-size: 0.9em;
}
</style>
