<template>
  <div class="login-container">
    <!-- 视频背景 -->
    <video class="background-video" autoplay loop muted playsinline>
      <source src="/background.mp4" type="video/mp4">
    </video>
    
    <!-- 添加遮罩层 -->
    <div class="overlay"></div>
    
    <div class="login-content">
      <div class="login-right">
        <div class="login-form">
          <h2>用户登录</h2>
          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label>用户名</label>
              <div class="input-wrapper">
                <i class="el-icon-user"></i>
                <input 
                  type="text" 
                  v-model="username" 
                  placeholder="请输入用户名"
                  required
                  autocomplete="username"
                >
              </div>
            </div>
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <i class="el-icon-lock"></i>
                <input 
                  type="password" 
                  v-model="password" 
                  placeholder="请输入密码"
                  required
                  autocomplete="current-password"
                >
              </div>
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

/* 添加遮罩层样式 */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

.login-content {
  display: flex;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 2;
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
  font-size: 28px;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper i {
  position: absolute;
  left: 15px;
  color: #666;
  font-size: 18px;
}

input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 2px solid #e1e1e1;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

input:focus {
  border-color: #4a90e2;
  outline: none;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
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
  box-shadow: 0 5px 15px rgba(74, 144, 226, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #333;
}

.register-link {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.register-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-content {
    margin: 20px;
  }
  
  .login-right {
    padding: 30px;
  }
}
</style> 