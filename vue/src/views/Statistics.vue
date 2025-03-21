<template>
  <div class="statistics-container">
    <el-card class="statistics-card">
      <template #header>
        <div class="card-header">
          <h2>今日考勤统计</h2>
          <div class="group-info" v-if="statistics.groupName">
            <el-tag type="info">{{ statistics.groupName }}</el-tag>
          </div>
        </div>
      </template>
      
      <!-- 统计卡片 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <template #header>
              <div class="stat-header">
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
                <span>请假人数</span>
              </div>
            </template>
            <div class="stat-value">{{ statistics.leaveCount }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 签到统计饼图 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>签到情况统计</span>
              </div>
            </template>
            <div ref="pieChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>出勤情况统计</span>
              </div>
            </template>
            <div ref="barChart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 未签到和未签退列表 -->
      <el-row :gutter="20" class="list-row">
        <el-col :span="8">
          <el-card shadow="hover" class="list-card">
            <template #header>
              <div class="card-header">
                <span>未签到人员</span>
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
                <span>已签到未签退</span>
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
                <span>请假人员</span>
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
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

export default {
  name: 'StatisticsView',
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
        const response = await axios.get('http://localhost:8080/user/groupTodayStatus', {
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
}

.statistics-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.group-info {
  display: flex;
  align-items: center;
}

.statistics-content {
  margin-top: 20px;
}

.stat-card {
  text-align: center;
}

.stat-header {
  font-size: 16px;
  color: #606266;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0;
}

.chart-row {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.list-row {
  margin-top: 20px;
}

.list-card {
  margin-bottom: 20px;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style> 