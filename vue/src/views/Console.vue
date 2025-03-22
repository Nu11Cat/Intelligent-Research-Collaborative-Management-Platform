<template>
  <div class="console-container">
    <h2>控制台</h2>
    
    <!-- 数据统计展示 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="4" v-for="(stat, index) in stats" :key="index">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
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
                <span>今日签到状态分布</span>
              </div>
            </template>
            <div ref="pieChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>各分组签到情况</span>
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
            <span>分组统计</span>
          </div>
        </template>
        <el-table :data="groupStats" style="width: 100%">
          <el-table-column prop="groupName" label="分组名称" />
          <el-table-column prop="totalMembers" label="总人数" width="100" />
          <el-table-column prop="checkedInCount" label="已签到" width="100" />
          <el-table-column prop="checkedOutCount" label="已签退" width="100" />
          <el-table-column prop="leaveCount" label="请假" width="100" />
          <el-table-column label="未签到人员">
            <template #default="scope">
              <el-tag 
                v-for="user in scope.row.notCheckedInUsers" 
                :key="user"
                size="small"
                type="warning"
                style="margin-right: 5px; margin-bottom: 5px;"
              >
                {{ user }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="请假人员">
            <template #default="scope">
              <el-tag 
                v-for="user in scope.row.leaveUsers" 
                :key="user"
                size="small"
                type="info"
                style="margin-right: 5px; margin-bottom: 5px;"
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

export default {
  name: 'SystemConsole',
  data() {
    return {
      stats: [
        { label: '总用户数', value: 0 },
        { label: '总分组数', value: 0 },
        { label: '今日已签到', value: 0 },
        { label: '今日已签退', value: 0 },
        { label: '请假人数', value: 0 }
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
            { label: '总用户数', value: data.totalUsers },
            { label: '总分组数', value: data.totalGroups },
            { label: '今日已签到', value: data.totalCheckedIn },
            { label: '今日已签退', value: data.totalCheckedOut },
            { label: '请假人数', value: data.totalLeave }
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
    }
  }
}
</script>

<style scoped>
.console-container {
  padding: 20px;
}

.stats-container {
  margin-top: 30px;
  padding: 0 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  text-align: center;
  padding: 20px 0;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.chart-row {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  height: 400px;
}

.group-stats-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}
</style> 