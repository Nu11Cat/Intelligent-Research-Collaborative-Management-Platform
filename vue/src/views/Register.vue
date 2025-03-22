<template>
  <div class="register-container">
    <!-- 视频背景 -->
    <video class="background-video" autoplay loop muted playsinline>
      <source src="/background.mp4" type="video/mp4">
    </video>
    
    <!-- 添加遮罩层 -->
    <div class="overlay"></div>
    
    <div class="register-content">
      <div class="register-form">
        <h2>用户注册</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>用户名</label>
            <div class="input-wrapper">
              <i class="el-icon-user"></i>
              <input 
                type="text" 
                v-model="username" 
                placeholder="请输入用户名"
                required
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
              >
            </div>
          </div>
          <div class="form-group">
            <label>确认密码</label>
            <div class="input-wrapper">
              <i class="el-icon-lock"></i>
              <input 
                type="password" 
                v-model="confirmPassword" 
                placeholder="请再次输入密码"
                required
              >
            </div>
          </div>
          <div class="form-group">
            <button type="submit" class="register-btn">注册</button>
          </div>
          <div class="form-footer">
            <span>已有账号？</span>
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
          <div class="agreement-links">
            <span>注册即表示您同意</span>
            <router-link to="/user-agreement" class="agreement-link">《用户协议》</router-link>
            <span>和</span>
            <router-link to="/privacy-policy" class="agreement-link">《隐私政策》</router-link>
          </div>
        </form>
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

.register-content {
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
  padding: 40px;
}

.register-form {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.register-form h2 {
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

.register-btn {
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

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(74, 144, 226, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #333;
}

.login-link {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}

.agreement-links {
  text-align: center;
  margin-top: 15px;
  color: #333;
  font-size: 14px;
}

.agreement-link {
  color: #4a90e2;
  text-decoration: none;
}

.agreement-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-content {
    margin: 20px;
    padding: 30px;
  }
}
</style> 