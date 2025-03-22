<template>
  <div class="agreement-container">
    <div class="agreement-content">
      <h1>用户协议</h1>
      <div class="markdown-body" v-html="renderedContent"></div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import MarkdownIt from 'markdown-it'

export default {
  name: 'UserAgreement',
  data() {
    return {
      content: '',
      md: new MarkdownIt()
    }
  },
  computed: {
    renderedContent() {
      return this.md.render(this.content)
    }
  },
  async created() {
    try {
      const response = await axios.get('/user-agreement.md')
      this.content = response.data
    } catch (error) {
      console.error('加载用户协议失败:', error)
      this.content = '加载用户协议失败，请稍后重试'
    }
  }
}
</script>

<style scoped>
.agreement-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.agreement-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.markdown-body {
  line-height: 1.6;
  color: #333;
}

.markdown-body h2 {
  color: #4a90e2;
  margin-top: 30px;
  margin-bottom: 15px;
}

.markdown-body p {
  margin-bottom: 15px;
}

.markdown-body ul, .markdown-body ol {
  margin-bottom: 15px;
  padding-left: 20px;
}

.markdown-body li {
  margin-bottom: 5px;
}
</style> 