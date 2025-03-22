<template>
  <div class="console-container">
    <h2 class="page-title">
      <el-icon><Monitor /></el-icon>
      控制台
    </h2>
    
    <!-- 数据统计展示 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="4" v-for="(stat, index) in stats" :key="index">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="24" :color="stat.color">
                  <component :is="stat.icon" />
                </el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表展示 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title">今日签到状态分布</span>
                <el-tooltip content="展示今日所有用户的签到状态分布" placement="top">
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <div ref="pieChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title">各分组签到情况</span>
                <el-tooltip content="展示各个分组的签到、未签到和请假人数" placement="top">
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <div ref="barChart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分组统计表格 -->
      <el-card class="group-stats-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">分组统计</span>
            <el-button type="primary" size="small" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </template>
        <el-table 
          :data="groupStats" 
          style="width: 100%"
          :stripe="true"
          :border="true"
          v-loading="loading"
        >
          <el-table-column prop="groupName" label="分组名称" min-width="120" />
          <el-table-column prop="totalMembers" label="总人数" width="100" align="center" />
          <el-table-column prop="checkedInCount" label="已签到" width="100" align="center">
            <template #default="scope">
              <el-tag type="success" effect="light">{{ scope.row.checkedInCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="checkedOutCount" label="已签退" width="100" align="center">
            <template #default="scope">
              <el-tag type="info" effect="light">{{ scope.row.checkedOutCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="leaveCount" label="请假" width="100" align="center">
            <template #default="scope">
              <el-tag type="warning" effect="light">{{ scope.row.leaveCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="未签到人员" min-width="200">
            <template #default="scope">
              <el-tag 
                v-for="user in scope.row.notCheckedInUsers" 
                :key="user"
                size="small"
                type="danger"
                effect="plain"
                class="user-tag"
              >
                {{ user }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="请假人员" min-width="200">
            <template #default="scope">
              <el-tag 
                v-for="user in scope.row.leaveUsers" 
                :key="user"
                size="small"
                type="info"
                effect="plain"
                class="user-tag"
              >
                {{ user }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import * as echarts from 'echarts'
import {
  UserFilled,
  FolderOpened,
  CircleCheckFilled,
  CircleCloseFilled,
  Calendar,
  Refresh,
  Monitor
} from '@element-plus/icons-vue'

export default {
  name: 'SystemConsole',
  components: {
    UserFilled,
    FolderOpened,
    CircleCheckFilled,
    CircleCloseFilled,
    Calendar,
    Refresh,
    Monitor
  },
  data() {
    return {
      loading: false,
      stats: [
        { label: '总用户数', value: 0, icon: 'UserFilled', color: '#409EFF' },
        { label: '总分组数', value: 0, icon: 'FolderOpened', color: '#67C23A' },
        { label: '今日已签到', value: 0, icon: 'CircleCheckFilled', color: '#E6A23C' },
        { label: '今日已签退', value: 0, icon: 'CircleCloseFilled', color: '#F56C6C' },
        { label: '请假人数', value: 0, icon: 'Calendar', color: '#909399' }
      ],
      groupStats: [],
      pieChart: null,
      barChart: null
    }
  },
  created() {
    this.fetchAdminData()
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
    if (this.pieChart) {
      this.pieChart.dispose()
    }
    if (this.barChart) {
      this.barChart.dispose()
    }
  },
  methods: {
    initCharts() {
      this.pieChart = echarts.init(this.$refs.pieChart)
      this.barChart = echarts.init(this.$refs.barChart)
    },
    handleResize() {
      if (this.pieChart) {
        this.pieChart.resize()
      }
      if (this.barChart) {
        this.barChart.resize()
      }
    },
    updateCharts() {
      // 更新饼图
      const pieOption = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: ['已签到', '未签到', '请假']
        },
        series: [
          {
            name: '签到状态',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: this.stats[2].value, name: '已签到' },
              { value: this.stats[0].value - this.stats[2].value, name: '未签到' },
              { value: this.groupStats.reduce((sum, group) => sum + group.leaveCount, 0), name: '请假' }
            ]
          }
        ]
      }
      this.pieChart.setOption(pieOption)

      // 更新柱状图
      const barOption = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['已签到', '未签到', '请假']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.groupStats.map(group => group.groupName)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '已签到',
            type: 'bar',
            data: this.groupStats.map(group => group.checkedInCount)
          },
          {
            name: '未签到',
            type: 'bar',
            data: this.groupStats.map(group => group.totalMembers - group.checkedInCount)
          },
          {
            name: '请假',
            type: 'bar',
            data: this.groupStats.map(group => group.leaveCount)
          }
        ]
      }
      this.barChart.setOption(barOption)
    },
    async fetchAdminData() {
      try {
        const response = await axios.get('http://localhost:8080/user/admin/todayStatus', {
          headers: {
            'token': localStorage.getItem('token')
          }
        })
        
        if (response.data.code === 1) {
          const data = response.data.data
          this.stats = [
            { label: '总用户数', value: data.totalUsers, icon: 'UserFilled', color: '#409EFF' },
            { label: '总分组数', value: data.totalGroups, icon: 'FolderOpened', color: '#67C23A' },
            { label: '今日已签到', value: data.totalCheckedIn, icon: 'CircleCheckFilled', color: '#E6A23C' },
            { label: '今日已签退', value: data.totalCheckedOut, icon: 'CircleCloseFilled', color: '#F56C6C' },
            { label: '请假人数', value: data.totalLeave, icon: 'Calendar', color: '#909399' }
          ]
          this.groupStats = data.groupStats
          this.updateCharts()
        } else {
          this.$message.error(response.data.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取管理员数据失败:', error)
        this.$message.error('获取数据失败')
      }
    },
    refreshData() {
      this.fetchAdminData()
    }
  }
}
</script>

<style scoped>
.console-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 30px;
  font-size: 24px;
  color: #303133;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-left: 15px;
  border-left: 4px solid #409EFF;
}

.page-title .el-icon {
  font-size: 24px;
  color: #409EFF;
}

.stats-container {
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.stat-card {
  transition: all 0.3s ease;
  border: none;
  background: #ffffff;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  margin-right: 15px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(64, 158, 255, 0.1);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-row {
  margin-bottom: 30px;
  margin-top: 20px;
}

.chart-card {
  height: 400px;
  border: none;
  background: #ffffff;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.info-icon {
  color: #909399;
  cursor: help;
  margin-left: 8px;
}

.chart {
  height: 320px;
  padding: 20px;
}

.group-stats-card {
  border: none;
  background: #ffffff;
}

.user-tag {
  margin: 4px;
  transition: all 0.3s ease;
}

.user-tag:hover {
  transform: scale(1.05);
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafafa;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}
</style> 