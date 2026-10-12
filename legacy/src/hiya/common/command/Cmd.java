package hiya.common.command;

/**
 * 指令类型枚举，对应JSON指令中"cmd"字段的值。
 * 每个值代表一种具体的业务操作。
 */
public enum Cmd {
    // 登录相关
    LOGIN,                        // 客户端请求登录
    LOGIN_SUCCESS,                // 服务器反馈登录成功
    LOGIN_FAILURE,                // 服务器反馈登录失败

    // 聊天室列表相关
    GET_CHAT_LIST,                // 客户端请求获取聊天室列表
    CHAT_LIST_RESULT,             // 服务器返回聊天室列表结果

    // 好友列表相关
    GET_FRIEND_LIST,              // 客户端请求获取好友列表
    FRIEND_LIST_RESULT,           // 服务器返回好友列表结果

    // 好友请求相关
    ADD_FRIEND_REQUEST,           // 客户端发送好友请求
    FRIEND_REQUEST_NOTIFY,        // 服务器通知目标用户有好友请求
    ADD_FRIEND_ACCEPT,            // 目标用户同意好友请求
    ADD_FRIEND_REJECT,            // 目标用户拒绝好友请求
    ADD_FRIEND_RESULT,            // 服务器反馈好友请求最终结果

    // 用户信息相关
    GET_USER_INFO,                // 客户端请求获取用户信息
    USER_INFO_RESULT,             // 服务器返回用户信息结果

    // 私聊创建相关
    CREATE_PRIVATE_CHAT,          // 客户端请求创建私聊会话
    PRIVATE_CHAT_RESULT,          // 服务器返回私聊创建结果

    // 删除好友相关
    DELETE_FRIEND,                // 客户端请求删除好友
    DELETE_FRIEND_RESULT,         // 服务器反馈删除好友结果

    // 聊天记录相关
    GET_CHAT_MESSAGES,            // 客户端请求获取聊天记录
    CHAT_MESSAGES_RESULT,         // 服务器返回聊天记录结果

    // 发送消息相关
    SEND_MESSAGE,                 // 客户端请求发送消息
    SEND_MESSAGE_RESULT,          // 服务器反馈消息发送结果
    BROADCAST_MESSAGE,            // 服务器广播新消息给聊天室其他成员

    // 聊天室成员相关
    GET_CHAT_MEMBERS,             // 客户端请求获取聊天室成员列表
    CHAT_MEMBERS_RESULT           // 服务器返回聊天室成员列表结果
}