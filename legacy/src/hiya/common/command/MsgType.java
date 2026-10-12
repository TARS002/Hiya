package hiya.common.command;

/**
 * 消息类型枚举，用于区分JSON指令中"消息类型"字段的值。
 * REQUEST表示客户端主动发起的请求，RESPONSE表示服务器对请求的反馈，
 * NOTIFY表示服务器主动推送的通知（如收到新消息、好友请求等）。
 */
public enum MsgType {
    REQUEST,   // 客户端请求
    RESPONSE,  // 服务器反馈
    NOTIFY     // 服务器主动通知
}