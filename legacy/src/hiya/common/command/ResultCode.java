package hiya.common.command;

/**
 * 消息结果码枚举，对应JSON指令中"code"字段的值。
 * 200表示成功，其他值表示不同类型的失败。
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),              // 操作成功
    FAILURE(400, "操作失败"),              // 通用失败
    UNAUTHORIZED(401, "未登录或登录已过期"), // 未授权
    NOT_FOUND(404, "资源不存在"),           // 资源未找到
    ALREADY_EXISTS(409, "资源已存在"),      // 资源冲突/已存在
    SERVER_ERROR(500, "服务器内部错误");    // 服务器内部错误

    private final int code;    // 数值码
    private final String msg;  // 结果描述

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}