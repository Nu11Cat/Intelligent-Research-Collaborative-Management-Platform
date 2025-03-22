<template>
  <div class="update-log">
    <div class="markdown-content" v-html="renderedContent"></div>
  </div>
</template>

<script>
import { marked } from 'marked'
import axios from 'axios'

export default {
  name: 'UpdateLogPage',
  data() {
    return {
      content: ''
    }
  },
  async created() {
    try {
      const response = await axios.get('/aboutUpdate.md')
      this.content = response.data
    } catch (error) {
      console.error('加载更新日志失败:', error)
      this.content = '# 更新日志\n\n加载失败，请稍后重试。'
    }
  },
  computed: {
    renderedContent() {
      return marked(this.content)
    }
  }
}
</script>

<style scoped>
.update-log {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.markdown-content {
  line-height: 1.6;
  color: #333;
}

.markdown-content h1 {
  color: #409EFF;
  margin-bottom: 30px;
  text-align: center;
  font-size: 28px;
}

.markdown-content h2 {
  color: #606266;
  margin: 25px 0 15px;
  font-size: 22px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.markdown-content ul {
  padding-left: 20px;
  margin: 10px 0;
}

.markdown-content li {
  margin: 8px 0;
  color: #606266;
}

.markdown-content li:before {
  content: "•";
  color: #409EFF;
  font-weight: bold;
  display: inline-block;
  width: 1em;
  margin-left: -1em;
}
</style> 