package com.hiya.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域（CORS）配置。
 *
 * 为什么需要跨域配置？
 *   Vue3 前端开发时跑在 http://localhost:5173（Vite 默认端口），
 *   后端跑在 http://localhost:8080，两者端口不同 = 不同源。
 *   浏览器的同源策略会拦截"不同源的 AJAX 请求"，所以必须由后端声明
 *   "我允许 localhost:5173 访问我"，浏览器才会放行。
 *
 * 说明：
 *   - allowedOriginPatterns 支持通配符（比 allowedOrigins 更灵活），
 *     生产环境建议改成你的真实前端域名；
 *   - allowCredentials(true)：允许跨域请求携带 Cookie / 自定义请求头；
 *   - maxAge(3600)：预检请求（OPTIONS）的结果缓存 1 小时，减少重复预检。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                              // 所有接口都允许跨域
                // 允许的来源：5173 = Vue3 开发服务器；8080 = 后端自带的测试页
                // （测试页与接口同源，其实不需要 CORS，这里列上便于以后从别处调试）
                .allowedOriginPatterns("http://localhost:5173", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
