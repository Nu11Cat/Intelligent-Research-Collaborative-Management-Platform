<template>
  <div class="login-container">
    <!-- 视频背景 -->
    <video class="background-video" autoplay loop muted playsinline>
      <source src="/background.mp4" type="video/mp4">
    </video>
    
    <div class="login-content">
      <div class="login-right">
        <div class="login-form">
          <h2>用户登录</h2>
          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label>用户名</label>
              <input 
                type="text" 
                v-model="username" 
                placeholder="请输入用户名"
                required
                autocomplete="username"
              >
            </div>
            <div class="form-group">
              <label>密码</label>
              <input 
                type="password" 
                v-model="password" 
                placeholder="请输入密码"
                required
                autocomplete="current-password"
              >
            </div>
            <div class="form-group">
              <button type="submit" class="login-btn">登录</button>
            </div>
            <div class="form-footer">
              <span>还没有账号？</span>
              <router-link to="/register" class="register-link">立即注册</router-link>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UserLogin',
  data() {
    return {
      username: '',
      password: ''
    }
  },
  created() {
    // 从路由参数中获取用户名
    const username = this.$route.query.username
    if (username) {
      this.username = username
    }
  },
  methods: {
    async handleLogin() {
      try {
        const response = await axios.post('http://localhost:8080/user/login', {
          username: this.username,
          password: this.password
        })
        
        console.log('登录响应:', response.data)
        
        if (response.data.code === 1) {
          const token = response.data.data
          console.log('获取到的token:', token)
          localStorage.setItem('token', token)
          
          // 获取用户角色
          const roleResponse = await axios.get('http://localhost:8080/user/getRole', {
            headers: {
              'token': token
            }
          })
          
          console.log('角色响应:', roleResponse.data)
          
          if (roleResponse.data.code === 1) {
            localStorage.setItem('userRole', roleResponse.data.data)
            this.$message.success(response.data.msg)
            this.$router.push('/')
          } else {
            console.error('获取角色失败:', roleResponse.data)
            this.$message.error(roleResponse.data.msg || '获取用户角色失败')
          }
        } else {
          console.error('登录失败:', response.data)
          this.$message.error(response.data.msg || '登录失败')
        }
      } catch (error) {
        console.error('请求错误:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          this.$message.error(error.response.data.msg || '登录失败')
        } else if (error.request) {
          console.error('请求未收到响应:', error.request)
          this.$message.error('服务器无响应，请检查网络连接')
        } else {
          console.error('请求配置错误:', error.message)
          this.$message.error('请求配置错误')
        }
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.login-content {
  display: flex;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-right {
  width: 100%;
  padding: 40px;
}

.login-form {
  max-width: 400px;
  margin: 0 auto;
}

.login-form h2 {
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e1e1e1;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
}

input:focus {
  border-color: #00b8ff;
  outline: none;
  box-shadow: 0 0 0 3px rgba(0, 184, 255, 0.1);
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #00b8ff 0%, #0080ff 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 184, 255, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link {
  color: #00b8ff;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.register-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-content {
    flex-direction: column;
  }
  
  .login-right {
    padding: 30px;
  }
}
</style> 