package com.hiya.ws;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 聊天 WebSocket 处理器：一条 WebSocket 连接上的所有事件都在这里回调。
 *
 * TextWebSocketHandler 是 Spring 提供的"文本消息"处理器基类，回调时机：
 *   建立连接 → afterConnectionEstablished
 *   收到文本 → handleTextMessage
 *   连接关闭 → afterConnectionClosed
 *   传输出错 → handleTransportError
 *
 * 客户端→服务器 的消息协议（JSON 字符串）：
 *   {"type":"ping"}          心跳消息，服务器回 pong
 *
 * 聊天消息的【发送】走 REST（POST /chat/{chatId}/messages），
 * WebSocket 只负责两件事：
 *   1. 维持长连接（在线状态由 SessionManager 管理）；
 *   2. 接收服务器推送（好友通知、消息广播，见 SessionManager.pushToUser）。
 * 所以本处理器目前只需处理心跳，收到其他消息一律忽略并记录日志。
 *
 * 服务器→客户端 的消息格式（JSON 字符串）：
 *   {"type":"pong"}
 *   {"type":"BROADCAST_MESSAGE","data":{...}}
 *   {"type":"FRIEND_REQUEST_NOTIFY","data":{...}}
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    /**
     * 会话管理器（构造器注入，依赖注入的说明见 UserServiceImpl 类注释）
     */
    private final SessionManager sessionManager;

    /**
     * Jackson 3 对象映射器：解析客户端发来的 JSON（SpringBoot 4 默认 JSON 库）
     */
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(SessionManager sessionManager, ObjectMapper objectMapper) {
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
    }

    /**
     * 连接建立成功：从连接地址里取出 userId，把会话注册进 SessionManager（标记用户在线）
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 连接地址形如 /ws?userId=1，从里面把 userId 解出来
        Long userId = getUserIdFromUri(session);
        if (userId == null) {
            closeQuietly(session); // 没带 userId 就不让连
            return;
        }
        session.getAttributes().put("userId", userId); // 存起来，后面收消息时用
        sessionManager.add(userId, session);
        log.info("用户 [{}] 上线，当前在线 {} 人", userId, sessionManager.onlineCount());
    }

    /** 从 session 的 URL 里解析 ?userId= 参数 */
    private Long getUserIdFromUri(WebSocketSession session) {
        String query = session.getUri() == null ? null : session.getUri().getQuery();
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && "userId".equals(kv[0])) {
                try {
                    return Long.valueOf(kv[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 收到客户端消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        JsonNode node = objectMapper.readTree(message.getPayload());
        // asString()：Jackson 3 中读取节点字符串值的方法（asText() 已废弃）
        String type = node.path("type").asString();

        switch (type) {
            case "ping":
                // 心跳：客户端定时发 ping，服务器回 pong。
                // 为什么需要心跳？中间代理（Nginx 等）会关闭长时间空闲的连接，
                // 心跳能让连接保持活跃，也能让服务器及时发现"假死"的连接。
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                break;
            default:
                // 业务上行消息统一走 REST（见类注释），这里收到未知类型仅记录日志
                log.info("收到用户 [{}] 的未识别消息: {}", userId, message.getPayload());
                break;
        }
    }

    /**
     * 连接关闭：从 SessionManager 移除，标记用户下线
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.remove(userId);
            log.info("用户 [{}] 下线，当前在线 {} 人", userId, sessionManager.onlineCount());
        }
    }

    /**
     * 传输错误：打日志并关闭连接
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 传输异常，sessionId={}", session.getId(), exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    /**
     * 安静的关闭连接（不抛异常）
     */
    private void closeQuietly(WebSocketSession session) {
        try {
            session.close(CloseStatus.POLICY_VIOLATION);
        } catch (Exception ignored) {
            // 关闭失败无需处理
        }
    }
}
