<template>
  <div class="statistics-container">
    <h2 class="page-title">
      <el-icon><DataLine /></el-icon>
      今日考勤统计
    </h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><User /></el-icon>
              <span>组内总人数</span>
            </div>
          </template>
          <div class="stat-value">{{ statistics.totalMembers }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><CircleCheckFilled /></el-icon>
              <span>今日已签到</span>
            </div>
          </template>
          <div class="stat-value">{{ statistics.checkedInCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>今日已签退</span>
            </div>
          </template>
          <div class="stat-value">{{ statistics.checkedOutCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><Calendar /></el-icon>
              <span>请假人数</span>
            </div>
          </template>
          <div class="stat-value">{{ statistics.leaveCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">签到情况统计</span>
              <el-tooltip content="展示今日签到、未签到和请假的人数分布" placement="top">
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div ref="pieChart" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">出勤情况统计</span>
              <el-tooltip content="展示已签到、已签退和请假的人数对比" placement="top">
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div ref="barChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表展示 -->
    <el-row :gutter="20" class="list-row">
      <el-col :span="8">
        <el-card shadow="hover" class="list-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">未签到人员</span>
              <el-tag type="warning" effect="light">{{ statistics.notCheckedInUsers.length }}人</el-tag>
            </div>
          </template>
          <el-table :data="statistics.notCheckedInUsers" style="width: 100%">
            <el-table-column label="用户名">
              <template #default="scope">
                {{ scope.row }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="list-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">已签到未签退</span>
              <el-tag type="info" effect="light">{{ statistics.notCheckedOutUsers.length }}人</el-tag>
            </div>
          </template>
          <el-table :data="statistics.notCheckedOutUsers" style="width: 100%">
            <el-table-column label="用户名">
              <template #default="scope">
                {{ scope.row }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="list-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">请假人员</span>
              <el-tag type="success" effect="light">{{ statistics.leaveUsers.length }}人</el-tag>
            </div>
          </template>
          <el-table :data="statistics.leaveUsers" style="width: 100%">
            <el-table-column label="用户名">
              <template #default="scope">
                {{ scope.row }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  DataLine,
  User,
  CircleCheckFilled,
  CircleCloseFilled,
  Calendar,
  InfoFilled
} from '@element-plus/icons-vue'

export default {
  name: 'StatisticsView',
  components: {
    DataLine,
    User,
    CircleCheckFilled,
    CircleCloseFilled,
    Calendar,
    InfoFilled
  },
  data() {
    return {
      statistics: {
        totalMembers: 0,
        checkedInCount: 0,
        checkedOutCount: 0,
        notCheckedInUsers: [],
        notCheckedOutUsers: [],
        groupName: null,
        date: null,
        leaveCount: 0,
        leaveUsers: []
      },
      pieChart: null,
      barChart: null
    }
  },
  methods: {
    async fetchStatistics() {
      try {
        const token = localStorage.getItem('token')
        const response = await request.get('/user/groupTodayStatus', {
          headers: {
            'token': token
          }
        })
        if (response.data.code === 1) {
          this.statistics = response.data.data
          this.updatePieChart()
          this.updateBarChart()
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        ElMessage.error('获取统计数据失败')
      }
    },
    initPieChart() {
      this.pieChart = echarts.init(this.$refs.pieChart)
      this.updatePieChart()
    },
    initBarChart() {
      this.barChart = echarts.init(this.$refs.barChart)
      this.updateBarChart()
    },
    updatePieChart() {
      if (!this.pieChart) return
      
      const option = {
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
            name: '签到情况',
            type: 'pie',
            radius: '70%',
            avoidLabelOverlap: false,
            label: {
              show: true,
              position: 'outside'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            data: [
              { value: this.statistics.checkedInCount, name: '已签到' },
              { value: this.statistics.totalMembers - this.statistics.checkedInCount - this.statistics.leaveCount, name: '未签到' },
              { value: this.statistics.leaveCount, name: '请假' }
            ]
          }
        ]
      }
      
      this.pieChart.setOption(option)
    },
    updateBarChart() {
      if (!this.barChart) return
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['出勤情况']
        },
        xAxis: {
          type: 'category',
          data: ['已签到', '已签退', '请假']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '人数',
            type: 'bar',
            data: [
              this.statistics.checkedInCount,
              this.statistics.checkedOutCount,
              this.statistics.leaveCount
            ]
          }
        ]
      }
      
      this.barChart.setOption(option)
    }
  },
  mounted() {
    this.fetchStatistics()
    this.$nextTick(() => {
      this.initPieChart()
      this.initBarChart()
    })
  },
  beforeUnmount() {
    if (this.pieChart) {
      this.pieChart.dispose()
    }
    if (this.barChart) {
      this.barChart.dispose()
    }
  }
}
</script>

<style scoped>
.statistics-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  border: none;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-header {
  font-size: 16px;
  color: #606266;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin: 15px 0;
  text-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
}

.stat-value .unit {
  font-size: 16px;
  color: #909399;
  font-weight: normal;
  text-shadow: none;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
  border: none;
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.chart {
  height: 320px;
  padding: 20px;
}

.list-row {
  margin-bottom: 20px;
}

.list-card {
  border: none;
  transition: all 0.3s ease;
}

.list-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: #fff;
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

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fff;
}

:deep(.el-card__body) {
  padding: 20px;
  background-color: #fff;
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

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 12px;
  height: 28px;
  line-height: 26px;
}

:deep(.el-tag--info) {
  background-color: #f4f4f5;
  border-color: #e9e9eb;
  color: #909399;
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
</style> 