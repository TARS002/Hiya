package com.hiya.dto;

/**
 * 登录成功后的响应数据：用户基本信息。
 *
 *   前端拿到 userId 后记在本地，之后：
 *     - REST 请求：在请求头带上 X-User-Id: {userId}
 *     - WebSocket 连接：ws://localhost:8080/ws?userId={userId}
 *   服务器据此识别"这次请求/这条连接是谁"，不做任何校验。
 */
public class LoginResponse {

    /** 用户ID：前端后续请求的身份凭据 */
    private Long userId;

    /** 用户名 */
    private String userName;

    // 无参构造器：Jackson 反序列化需要
    public LoginResponse() {
    }

    // 全参构造器：Controller 组装响应时方便
    public LoginResponse(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
