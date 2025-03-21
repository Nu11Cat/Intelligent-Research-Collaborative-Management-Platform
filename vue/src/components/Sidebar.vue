<template>
  <el-menu
    :default-active="activeMenu"
    class="sidebar-menu"
    :router="true"
  >
    <el-menu-item index="/attendance">
      <el-icon><Calendar /></el-icon>
      <span>考勤管理</span>
    </el-menu-item>
    
    <el-menu-item index="/equipment">
      <el-icon><Box /></el-icon>
      <span>器材管理</span>
    </el-menu-item>

    <el-menu-item index="/team-recruitment">
      <el-icon><User /></el-icon>
      <span>招募信息管理</span>
    </el-menu-item>

    <el-menu-item v-if="userRole === 'LEADER'" index="/group">
      <el-icon><UserFilled /></el-icon>
      <span>组员管理</span>
    </el-menu-item>

    <el-menu-item index="/resource-sharing">
      <el-icon><Folder /></el-icon>
      <span>资源共享</span>
    </el-menu-item>

    <el-menu-item index="/about">
      <el-icon><InfoFilled /></el-icon>
      <span>关于系统</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Calendar, Box, User, UserFilled, InfoFilled, Folder } from '@element-plus/icons-vue'

export default {
  name: 'Sidebar',
  components: {
    Calendar,
    Box,
    User,
    UserFilled,
    InfoFilled,
    Folder
  },
  setup() {
    const route = useRoute()
    const userRole = localStorage.getItem('userRole')

    const activeMenu = computed(() => {
      return route.path
    })

    return {
      activeMenu,
      userRole
    }
  }
}
</script>

<style scoped>
.sidebar-menu {
  height: 100%;
  border-right: none;
}
</style> 