<template>
  <div class="about-container">
    <div class="markdown-content" v-html="markdownContent"></div>
  </div>
</template>

<script>
import { marked } from 'marked'

export default {
  name: 'AboutView',
  data() {
    return {
      markdownContent: ''
    }
  },
  async created() {
    try {
      const response = await fetch('/aboutProject.md')
      const text = await response.text()
      this.markdownContent = marked(text)
    } catch (error) {
      console.error('Error loading markdown:', error)
      this.markdownContent = '<p>加载文档失败</p>'
    }
  }
}
</script>

<style scoped>
.about-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.markdown-content {
  line-height: 1.6;
}

.markdown-content :deep(h1) {
  color: #333;
  margin-bottom: 20px;
}

.markdown-content :deep(p) {
  margin-bottom: 16px;
  color: #666;
}
</style> 