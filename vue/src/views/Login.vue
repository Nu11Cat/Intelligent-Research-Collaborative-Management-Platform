<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-left">
        <h1>欢迎回来</h1>
        <p>登录您的账号以继续</p>
      </div>
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
            alert(response.data.msg)
            this.$router.push('/')
          } else {
            console.error('获取角色失败:', roleResponse.data)
            alert(roleResponse.data.msg || '获取用户角色失败')
          }
        } else {
          console.error('登录失败:', response.data)
          alert(response.data.msg || '登录失败')
        }
      } catch (error) {
        console.error('请求错误:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          alert(error.response.data.msg || '登录失败')
        } else if (error.request) {
          console.error('请求未收到响应:', error.request)
          alert('服务器无响应，请检查网络连接')
        } else {
          console.error('请求配置错误:', error.message)
          alert('请求配置错误')
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-content {
  display: flex;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 1000px;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  text-align: center;
}

.login-left h1 {
  font-size: 2.5em;
  margin-bottom: 20px;
}

.login-left p {
  font-size: 1.2em;
  opacity: 0.9;
}

.login-right {
  flex: 1;
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
  border-color: #667eea;
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link {
  color: #667eea;
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
  
  .login-left {
    padding: 30px;
  }
  
  .login-right {
    padding: 30px;
  }
}
</style> 