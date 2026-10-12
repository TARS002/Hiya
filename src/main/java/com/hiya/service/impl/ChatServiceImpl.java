package com.hiya.service.impl;

import com.hiya.entity.Chat;
import com.hiya.entity.ChatMember;
import com.hiya.entity.Message;
import com.hiya.entity.User;
import com.hiya.repository.ChatMemberRepository;
import com.hiya.repository.ChatRepository;
import com.hiya.repository.MessageRepository;
import com.hiya.repository.UserRepository;
import com.hiya.service.ChatService;
import com.hiya.ws.SessionManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天接口实现类：会话 + 成员 + 消息。
 *
 *   hiya_chat        每个会话一行（type=1 私聊，type=2 群聊）
 *   hiya_chat_member 谁在哪个会话里（role=0 群员，2 群主）
 *   hiya_message     每条消息一行
 *
 * 建群 = 插 1 行 chat + 插 1 行 member（群主）
 * 加群 = 插 1 行 member
 * 退群 = 删 1 行 member
 * 发消息 = 插 1 行 message + 给会话内在线的成员推 WebSocket
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SessionManager sessionManager;

    public ChatServiceImpl(ChatRepository chatRepository,
                           ChatMemberRepository chatMemberRepository,
                           UserRepository userRepository,
                           MessageRepository messageRepository,
                           SessionManager sessionManager) {
        this.chatRepository = chatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.sessionManager = sessionManager;
    }

    // ==================== 管理聊天室 ====================

    @Override
    public List<Chat> getChatList(Long userId) {
        // 先查用户加入了哪些会话，再按 ID 查会话详情
        List<Long> chatIds = chatMemberRepository.findChatIdsByUserId(userId);
        if (chatIds.isEmpty()) {
            return List.of();
        }
        return chatRepository.findAllById(chatIds);
    }

    @Override
    public Chat createRoom(Long creatorId, String chatTitle) {
        // 1. 建群聊会话（type=2）
        Chat chat = new Chat();
        chat.setType(2);
        chat.setChatTitle(chatTitle);
        chat.setCreateTime(LocalDateTime.now());
        chat.setUpdateTime(LocalDateTime.now());
        Chat saved = chatRepository.save(chat); // save 后拿到自增 chatId

        // 2. 创建者加进去，角色 = 2（群主）
        addMember(saved.getChatId(), creatorId, 2);
        return saved;
    }

    @Override
    public Chat createPrivateChat(Long userId, Long friendId) {
        // 两人已经有私聊就直接返回，没有才新建（避免每次点开都建一个新的）
        Chat existing = chatRepository.findPrivateChatBetween(userId, friendId).orElse(null);
        if (existing != null) {
            return existing;
        }

        Chat chat = new Chat();
        chat.setType(1); // 1=私聊
        chat.setCreateTime(LocalDateTime.now());
        chat.setUpdateTime(LocalDateTime.now());
        Chat saved = chatRepository.save(chat);

        // 双方都加为成员
        addMember(saved.getChatId(), userId, 0);
        addMember(saved.getChatId(), friendId, 0);
        return saved;
    }

    // ==================== 管理聊天室里的人员 ====================

    @Override
    public List<User> getChatMembers(Long chatId) {
        List<Long> userIds = chatMemberRepository.findUserIdsByChatId(chatId);
        if (userIds.isEmpty()) {
            return List.of();
        }
        return userRepository.findAllById(userIds);
    }

    @Override
    public void joinChat(Long userId, Long chatId) {
        // 加一行成员记录（已经在里面的话 addMember 里会跳过）
        addMember(chatId, userId, 0);
    }

    @Override
    public void leaveChat(Long userId, Long chatId) {
        // 找到这条成员记录就删掉
        chatMemberRepository.findByChatIdAndUserId(chatId, userId)
                .ifPresent(chatMemberRepository::delete);
    }

    // ==================== 管理聊天室里的消息 ====================

    @Override
    public Message sendMessage(Long senderId, Long chatId, Integer msgType, String content) {
        // 1. 消息存库
        Message message = new Message();
        message.setSenderId(senderId);
        message.setChatId(chatId);
        message.setMsgType(msgType == null ? 1 : msgType); // 不传就是文本(1)
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        message.setIsDeleted(0);
        Message saved = messageRepository.save(message);

        // 2. 推给会话里所有在线成员（不在线收不到，以后再看）
        List<Long> memberIds = chatMemberRepository.findUserIdsByChatId(chatId);
        sessionManager.pushToUsers(memberIds, "BROADCAST_MESSAGE", saved);

        return saved;
    }

    @Override
    public List<Message> getChatMessages(Long chatId) {
        return messageRepository.findByChatIdAndIsDeletedOrderBySendTimeAsc(chatId, 0);
    }

    // ==================== 私有方法 ====================

    /** 往会话里加一个成员（已经是成员就直接返回，不重复插） */
    private void addMember(Long chatId, Long userId, int role) {
        if (chatMemberRepository.existsByChatIdAndUserId(chatId, userId)) {
            return;
        }
        ChatMember member = new ChatMember();
        member.setChatId(chatId);
        member.setUserId(userId);
        member.setRole(role);
        member.setJoinTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());
        member.setIsMuted(0);
        member.setMuteUntil(LocalDateTime.now()); // 这列NOT NULL，给个当前时间
        chatMemberRepository.save(member);
    }
}
