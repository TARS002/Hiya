package com.hiya.controller;

import com.hiya.common.Result;
import com.hiya.dto.CreatePrivateChatRequest;
import com.hiya.dto.CreateRoomRequest;
import com.hiya.dto.SendMessageRequest;
import com.hiya.entity.Chat;
import com.hiya.entity.Message;
import com.hiya.entity.User;
import com.hiya.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 聊天控制器 —— 聊天室板块的三大任务。
 *
 * ① 管理聊天室
 *   GET  /api/chat/list?userId=1           我加入的所有会话
 *   POST /api/chat/room?userId=1           建群 {chatTitle}
 *   POST /api/chat/private?userId=1        私聊 {friendId}
 * ② 管理聊天室里的人员
 *   GET  /api/chat/{chatId}/members        成员列表
 *   POST /api/chat/{chatId}/join?userId=1  加群
 *   POST /api/chat/{chatId}/leave?userId=1 退群
 * ③ 管理聊天室里的消息
 *   GET  /api/chat/{chatId}/messages       聊天记录
 *   POST /api/chat/{chatId}/messages?userId=1  发消息 {content}
 *
 * 发消息的效果：消息存进数据库，然后服务器通过 WebSocket 推给会话里的在线成员。
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 我的会话列表：GET /api/chat/list?userId=1
     * 
     * @param userId
     * @return 统一后的Result对象。其下的data是List<Chat>
     */
    @GetMapping("/list")
    public Result<List<Chat>> list(@RequestParam("userId") Long userId) {
        return Result.ok(chatService.getChatList(userId));
    }

    /**
     * 建群：POST /api/chat/room?userId=1，body {"chatTitle":"群名"}
     * 
     * @param userId
     * @param request
     * @return 一个Result包装的Chat对象
     */
    @PostMapping("/room")
    public Result<Chat> createRoom(@RequestParam("userId") Long userId,
                                   @Valid @RequestBody CreateRoomRequest request) {
        return Result.ok(chatService.createRoom(userId, request.getChatTitle()));
    }

    /**
     * 请求私聊：POST /api/chat/private?userId=1，body {"friendId":3}
     * 
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/private")
    public Result<Chat> privateChat(@RequestParam("userId") Long userId,
                                    @Valid @RequestBody CreatePrivateChatRequest request) {
        return Result.ok(chatService.createPrivateChat(userId, request.getFriendId()));
    }

    /** 查群成员：GET /api/chat/1/members */
    @GetMapping("/{chatId}/members")
    public Result<List<User>> members(@PathVariable("chatId") Long chatId) {
        return Result.ok(chatService.getChatMembers(chatId));
    }

    /** 加群：POST /api/chat/1/join?userId=1 */
    @PostMapping("/{chatId}/join")
    public Result<Void> join(@PathVariable("chatId") Long chatId,
                             @RequestParam("userId") Long userId) {
        chatService.joinChat(userId, chatId);
        return Result.ok();
    }

    /** 退群：POST /api/chat/1/leave?userId=1 */
    @PostMapping("/{chatId}/leave")
    public Result<Void> leave(@PathVariable("chatId") Long chatId,
                              @RequestParam("userId") Long userId) {
        chatService.leaveChat(userId, chatId);
        return Result.ok();
    }

    /** 翻聊天记录：GET /api/chat/1/messages */
    @GetMapping("/{chatId}/messages")
    public Result<List<Message>> messages(@PathVariable("chatId") Long chatId) {
        return Result.ok(chatService.getChatMessages(chatId));
    }

    /** 用户发消息：POST /api/chat/1/messages?userId=1，body {"content":"你好"} */
    @PostMapping("/{chatId}/messages")
    public Result<Message> sendMessage(@PathVariable("chatId") Long chatId,
                                       @RequestParam("userId") Long userId,
                                       @Valid @RequestBody SendMessageRequest request) {
        return Result.ok(chatService.sendMessage(userId, chatId, request.getMsgType(), request.getContent()));
    }
}
