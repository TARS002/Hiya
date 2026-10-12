package com.hiya.service;

import com.hiya.entity.Chat;
import com.hiya.entity.Message;
import com.hiya.entity.User;

import java.util.List;

/**
 * 聊天业务接口
 *
 *   管理聊天室        → createRoom（添加聊天室）
 *   管理聊天室里的人员 → joinChat（加入）、leaveChat（退出）、getChatMembers（查询成员）
 *   管理聊天室里的消息 → sendMessage（发消息）、getChatMessages（聊天记录）
 *   另外保留私聊：createPrivateChat（和好友一对一，旧系统原有功能）
 *
 *   hiya_chat        每个会话一行；type=1 私聊、type=2 群聊
 *   hiya_chat_member "谁在哪个会话里"；role=0 群员、1 绿标、2 群主
 *   一次"建群" = 插 1 行 chat + 插 1 行 chat_member（创建者，role=2 群主）
 *   一次"加群" = 插 1 行 chat_member（role=0）
 *   一次"退群" = 删 1 行 chat_member
 */
public interface ChatService {

    // ---------- 管理聊天室 ----------

    /** 查询某用户加入的所有会话（私聊 + 群聊） */
    List<Chat> getChatList(Long userId);

    /** 创建一个群聊，创建者自动成为群主（role=2） */
    Chat createRoom(Long creatorId, String chatTitle);

    /** 创建或获取与某好友的私聊会话（已存在则直接返回，不会重复建） */
    Chat createPrivateChat(Long userId, Long friendId);

    // ---------- 管理聊天室里的人员 ----------

    /** 查询某会话的所有成员 */
    List<User> getChatMembers(Long chatId);

    /** 加入群聊（只能加入群聊；重复加入会被拒绝） */
    void joinChat(Long userId, Long chatId);

    /** 退出群聊（只能退出群聊；不在群里会被拒绝） */
    void leaveChat(Long userId, Long chatId);

    // ---------- 管理聊天室里的消息 ----------

    /**
     * 发送消息：落库 + 通过 WebSocket 广播给会话内所有在线成员。
     * @return 落库后的消息（含自增 id 和发送时间），前端可以直接拿去渲染
     */
    Message sendMessage(Long senderId, Long chatId, Integer msgType, String content);

    /** 查询某会话的聊天记录（未撤回，按发送时间升序） */
    List<Message> getChatMessages(Long chatId);
}
