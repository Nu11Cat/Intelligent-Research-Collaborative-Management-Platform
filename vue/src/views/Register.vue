<template>
  <div class="register-container">
    <div class="register-content">
      <div class="register-left">
        <h1>加入我们</h1>
        <p>创建新账号，开启您的旅程</p>
      </div>
      <div class="register-right">
        <div class="register-form">
          <h2>用户注册</h2>
          <form @submit.prevent="handleRegister">
            <div class="form-group">
              <label>用户名</label>
              <input 
                type="text" 
                v-model="username" 
                placeholder="请输入用户名"
                required
              >
            </div>
            <div class="form-group">
              <label>密码</label>
              <input 
                type="password" 
                v-model="password" 
                placeholder="请输入密码"
                required
              >
            </div>
            <div class="form-group">
              <label>确认密码</label>
              <input 
                type="password" 
                v-model="confirmPassword" 
                placeholder="请再次输入密码"
                required
              >
            </div>
            <div class="form-group">
              <button type="submit" class="register-btn">注册</button>
            </div>
            <div class="form-footer">
              <span>已有账号？</span>
              <router-link to="/login" class="login-link">立即登录</router-link>
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
  name: 'UserRegister',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: ''
    }
  },
  methods: {
    async handleRegister() {
      if (this.password !== this.confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }
      
      try {
        const response = await axios.post('http://localhost:8080/user/register', {
          username: this.username,
          password: this.password
        })
        
        if (response.data.code === 1) {
          alert('注册成功，请登录')
          // 跳转到登录页面，并传递用户名
          this.$router.push({
            path: '/login',
            query: { username: this.username }
          })
        } else {
          alert(response.data.msg)
        }
      } catch (error) {
        alert(error.response?.data?.msg || '注册失败')
      }
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-content {
  display: flex;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 1000px;
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  text-align: center;
}

.register-left h1 {
  font-size: 2.5em;
  margin-bottom: 20px;
}

.register-left p {
  font-size: 1.2em;
  opacity: 0.9;
}

.register-right {
  flex: 1;
  padding: 40px;
}

.register-form {
  max-width: 400px;
  margin: 0 auto;
}

.register-form h2 {
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

.register-btn {
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

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-content {
    flex-direction: column;
  }
  
  .register-left {
    padding: 30px;
  }
  
  .register-right {
    padding: 30px;
  }
}
</style> 