package com.hiya.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理器：存<用户ID, WS连接>的键值对。
 *
 * 为什么用 ConcurrentHashMap？
 *   WebSocket 是多线程环境：多个客户端同时连入/断开/发消息，
 *   多个 Tomcat 线程会并发读写这个 Map。HashMap 在并发下可能出问题
 *   （死循环、丢数据），ConcurrentHashMap 是线程安全的替代品，
 *   是 Java 并发编程的标配容器。
 *
 * 业务用途：
 *   - 用户上线：add()          （对应旧 LoginHandler 里 onlineClients.put）
 *   - 用户下线：remove()        （对应旧 ClientHandler.cleanup）
 *   - 推送消息：pushToUser / pushToUsers（对应旧 sendNotify）
 *
 * 推送协议（服务器 → 客户端），与旧系统 NOTIFY 语义对应：
 *   {"type":"BROADCAST_MESSAGE","data":{...}}
 *   {"type":"FRIEND_REQUEST_NOTIFY","data":{...}}
 */
@Component
public class SessionManager {

    /**
     * key = 用户ID，value = 该用户的 WebSocket 会话（简化设计：一个用户同一时刻只保留一个连接）
     */
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * Jackson 3 对象映射器：把推送内容序列化成 JSON（注入方式见 UserServiceImpl 注释）
     */
    private final ObjectMapper objectMapper;

    public SessionManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 用户上线：注册会话
     */
    public void add(Long userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    /**
     * 用户下线：移除会话
     */
    public void remove(Long userId) {
        sessions.remove(userId);
    }

    /**
     * 查询用户是否在线
     */
    public boolean isOnline(Long userId) {
        return sessions.containsKey(userId);
    }

    /**
     * 当前在线人数
     */
    public int onlineCount() {
        return sessions.size();
    }

    /**
     * 当前所有在线用户ID集合（新接口，供前端初始化好友在线状态）
     */
    public Set<Long> onlineUserIds() {
        return sessions.keySet();
    }

    /**
     * 给指定用户推送一条"业务消息"（对应旧系统的 sendNotify）。
     * 消息格式统一为 {"type":"xxx","data":{...}}。
     *
     * @param userId 接收者用户ID
     * @param type   消息类型（如 BROADCAST_MESSAGE、FRIEND_REQUEST_NOTIFY）
     * @param data   业务数据（任意对象，Jackson 序列化成 JSON）
     */
    public void pushToUser(Long userId, String type, Object data) {
        sendToUser(userId, buildPush(type, data));
    }

    /**
     * 给多个用户推送同一条业务消息（聊天广播用）。
     * 序列化只做一次，避免每个用户重复序列化（性能小优化）。
     */
    public void pushToUsers(Collection<Long> userIds, String type, Object data) {
        String json = buildPush(type, data);
        for (Long userId : userIds) {
            sendToUser(userId, json);
        }
    }

    /**
     * 给指定用户发送原始文本消息（心跳 pong 等简单场景用）。
     * WebSocketSession 的 sendMessage 不是线程安全的，多线程同时发送可能抛异常，
     * 所以用 synchronized 保证同一会话的发送是串行的。
     */
    public void sendToUser(Long userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session == null || !session.isOpen()) {
            // TODO(离线消息): 用户不在线时消息直接丢弃；后续可参照旧系统局限的改进方向，
            //   实现"离线消息落库 + 上线后补发"。
            return;
        }
        synchronized (session) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                // 发送失败说明连接已坏，清理掉，避免占着位置
                sessions.remove(userId);
            }
        }
    }

    /**
     * 把 {type, data} 组装成 JSON 字符串
     */
    private String buildPush(String type, Object data) {
        try {
            // Map.of 不允许 null 值，所以业务推送的 data 保证非 null
            return objectMapper.writeValueAsString(Map.of("type", type, "data", data));
        } catch (Exception e) {
            // 序列化失败属于编程错误，直接抛运行时异常让上层感知
            throw new RuntimeException("WebSocket 推送消息序列化失败", e);
        }
    }
}
