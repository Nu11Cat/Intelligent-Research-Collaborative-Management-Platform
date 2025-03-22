package cn.nullcat.sckj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置全局 CORS 策略
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置跨域的 URL 映射
        registry.addMapping("/**")  // 允许所有路径
                .allowedOrigins("http://localhost:8081")  // 只允许前端地址为 http://localhost:3000 的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 允许的 HTTP 方法
                .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization", "token")
                .allowCredentials(true)  // 是否允许发送凭证（例如 Cookies 或 Authorization 认证信息）
                .maxAge(3600);  // 预检请求的缓存时间，单位为秒，默认是 1800（30分钟）
    }
}
