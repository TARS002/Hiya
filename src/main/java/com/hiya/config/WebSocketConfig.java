package com.hiya.config;

import com.hiya.ws.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类：把聊天处理器挂到 /ws 上。
 *
 * 连接地址：ws://localhost:8080/ws?userId=1
 *   （浏览器 WebSocket 不能自定义请求头，所以 userId 只能写在 URL 里，
 *    由 ChatWebSocketHandler 在连接建立时解析出来）
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws")
                // 允许的前端来源：
                //   localhost:5173 / 127.0.0.1:5173 = Vue3 开发服务器（两种写法都算）
                //   localhost:8080 / 127.0.0.1:8080 = 后端自带的测试页（/test.html），方便没有前端时联调
                // 注意：WebSocket 握手也会带 Origin 头，不在白名单里会被直接拒绝（403），
                // 表现就是"页面能打开、登录也好用，但一直显示未连接"。
                .setAllowedOriginPatterns(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://localhost:8080",
                        "http://127.0.0.1:8080");
    }
}
