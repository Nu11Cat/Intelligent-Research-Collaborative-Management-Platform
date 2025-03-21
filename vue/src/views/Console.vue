<template>
  <div class="console-container">
    <h2>控制台</h2>
    
    <!-- 功能导航卡片 -->
    <div class="nav-cards">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="nav-card" @click="handleNavClick('/group-management')">
            <h3>小组管理</h3>
            <p>管理小组信息，包括添加、修改、删除小组等操作</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="nav-card" @click="handleNavClick('/pre-registered-user-management')">
            <h3>预注册用户管理</h3>
            <p>管理预注册用户，包括添加、删除预注册用户等操作</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="nav-card" @click="handleNavClick('/admin')">
            <h3>考勤记录管理</h3>
            <p>查看和管理所有用户的考勤记录</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="console-content">
      <el-empty description="功能开发中..." />
    </div>
  </div>
</template>

<script>
import eventBus from '../utils/eventBus'

export default {
  name: 'SystemConsole',
  methods: {
    handleNavClick(path) {
      // 发送导航更新事件
      eventBus.emit('updateNav', path)
      // 跳转到指定路径
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.console-container {
  padding: 20px;
}

.nav-cards {
  margin-bottom: 30px;
}

.nav-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 120px;
}

.nav-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.nav-card h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.nav-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.console-content {
  margin-top: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style> 