package com.hiya.common;

/**
 * 业务状态码枚举：集中管理所有响应码，避免散落在代码各处的"魔法数字"。
 *
 * 设计约定：
 *   - 2xx 系列：成功
 *   - 4xx 系列：客户端错误（参数错误、未登录、无权限等），与 HTTP 状态码语义对齐
 *   - 5xx 系列：服务器内部错误
 *   - 10xx 起：业务规则错误（用户名已存在、登录失败等）
 */
public enum ResultCode {

    // ===== 成功 =====
    SUCCESS(200, "操作成功"),

    // ===== 客户端错误 =====
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限执行该操作"),
    NOT_FOUND(404, "资源不存在"),
    // 资源冲突/已存在：如"已经是好友了"（与旧系统 ResultCode.ALREADY_EXISTS 对齐）
    ALREADY_EXISTS(409, "资源已存在"),

    // ===== 服务器错误 =====
    ERROR(500, "服务器内部错误"),

    // ===== 业务错误（10xx 起） =====
    USERNAME_EXISTS(1001, "用户名已被注册"),
    LOGIN_FAILED(1002, "用户名或密码错误"),
    USER_NOT_FOUND(1003, "用户不存在");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 默认提示信息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() { return code; }
    public String getMessage() { return message; }
}
