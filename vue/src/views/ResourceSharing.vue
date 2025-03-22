<template>
  <div class="resource-sharing-container">
    <div class="page-title">
      <el-icon><FolderOpened /></el-icon>
      <span>资源分享</span>
    </div>

    <el-card class="coming-soon-card" shadow="hover">
      <div class="coming-soon-content">
        <el-icon class="main-icon"><FolderOpened /></el-icon>
        <h2>敬请期待</h2>
        <p>资源分享功能正在开发中，敬请期待...</p>
        <div class="loading-dots">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { FolderOpened } from '@element-plus/icons-vue'

export default {
  name: 'ResourceSharing',
  components: {
    FolderOpened
  }
}
</script>

<style scoped>
.resource-sharing-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #409EFF;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.coming-soon-card {
  border: none;
  transition: all 0.3s ease;
}

.coming-soon-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.coming-soon-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.main-icon {
  font-size: 64px;
  color: #409EFF;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 16px;
  font-weight: 600;
}

p {
  font-size: 16px;
  color: #606266;
  margin-bottom: 24px;
}

.loading-dots {
  display: flex;
  gap: 8px;
  margin-top: 20px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background-color: #409EFF;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
  100% {
    transform: translateY(0px);
  }
}

@keyframes bounce {
  0%, 80%, 100% { 
    transform: scale(0);
  }
  40% { 
    transform: scale(1.0);
  }
}
</style> 