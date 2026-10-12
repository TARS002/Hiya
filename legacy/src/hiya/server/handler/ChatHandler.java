package hiya.server.handler;

import hiya.common.command.*;
import hiya.common.model.Chat;
import hiya.common.model.User;
import hiya.server.db.ChatDAO;
import hiya.server.db.ChatMemberDAO;
import hiya.server.db.UserDAO;
import hiya.server.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天室业务处理器。处理聊天室列表获取、私聊创建、成员列表查询等。
 */
public class ChatHandler implements Handler {
    private ClientHandler clientHandler;           // 客户端连接处理器
    private ChatDAO chatDAO = new ChatDAO();       // 聊天会话DAO
    private ChatMemberDAO chatMemberDAO = new ChatMemberDAO(); // 聊天室成员DAO
    private UserDAO userDAO = new UserDAO();       // 用户DAO

    public ChatHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 处理聊天室相关的指令，根据cmd类型分别处理。
     * @param command 客户端发来的指令
     */
    @Override
    public void handle(Command command) {
        switch (command.getCmdEnum()) {
            case GET_CHAT_LIST:
                handleGetChatList(command);
                break;
            case CREATE_PRIVATE_CHAT:
                handleCreatePrivateChat(command);
                break;
            case GET_CHAT_MEMBERS:
                handleGetChatMembers(command);
                break;
            default:
                break;
        }
    }

    /**
     * 处理获取聊天室列表请求。
     * 先通过hiya_chat_member查到用户所在的所有chat_id，再通过这些chat_id查hiya_chat获取会话信息。
     * @param command 获取聊天室列表请求
     */
    private void handleGetChatList(Command command) {
        Long userId = command.getRequesterId(); // 获取请求者ID
        // 第一步：查询该用户所在的所有聊天室ID
        List<Long> chatIds = chatMemberDAO.findChatIdsByUserId(userId);
        // 第二步：根据每个chat_id查询聊天室详细信息
        List<Chat> chatList = new ArrayList<>();
        for (Long chatId : chatIds) {
            Chat chat = chatDAO.findById(chatId);
            if (chat != null) {
                chatList.add(chat);
            }
        }
        // 返回聊天室列表给客户端
        clientHandler.sendResponse(userId, Cmd.CHAT_LIST_RESULT, ResultCode.SUCCESS, chatList);
    }

    /**
     * 处理创建私聊会话请求。
     * 先检查两人之间是否已存在私聊，已存在则直接返回已有会话。
     * 不存在则创建新会话，使用getGeneratedKeys获取自增chat_id，然后添加两个成员。
     * @param command 创建私聊请求
     */
    private void handleCreatePrivateChat(Command command) {
        Long userId = command.getRequesterId(); // 当前用户ID
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long。Gson会转成Double。
        Long friendId = ((Number) data.get("friendId")).longValue(); // 好友用户ID

        // 第一步：检查两人之间是否已存在私聊会话
        Chat existingChat = chatDAO.findPrivateChatBetween(userId, friendId);
        if (existingChat != null) {
            // 已存在私聊，直接返回已有的会话信息
            clientHandler.sendResponse(userId, Cmd.PRIVATE_CHAT_RESULT, ResultCode.SUCCESS, existingChat);
            return;
        }

        // 第二步：创建新的私聊会话，type=1表示私聊
        Long chatId = chatDAO.insert(1, null);
        if (chatId == null) {
            // 创建会话失败
            clientHandler.sendResponse(userId, Cmd.PRIVATE_CHAT_RESULT,
                    ResultCode.SERVER_ERROR, Map.of("message", "创建会话失败"));
            return;
        }

        // 第三步：向hiya_chat_member添加两个成员，私聊双方角色都是0（普通成员）
        boolean member1 = chatMemberDAO.insertMember(chatId, userId, 0);   // 添加当前用户
        boolean member2 = chatMemberDAO.insertMember(chatId, friendId, 0); // 添加好友用户

        if (member1 && member2) {
            // 两个成员都添加成功，查询完整的会话信息并返回
            Chat newChat = chatDAO.findById(chatId);
            clientHandler.sendResponse(userId, Cmd.PRIVATE_CHAT_RESULT, ResultCode.SUCCESS, newChat);
        } else {
            // 成员添加失败
            clientHandler.sendResponse(userId, Cmd.PRIVATE_CHAT_RESULT,
                    ResultCode.SERVER_ERROR, Map.of("message", "添加会话成员失败"));
        }
    }

    /**
     * 处理获取聊天室成员列表请求。
     * 先查hiya_chat_member获取chat_id下的所有user_id，再查hiya_user获取用户信息。
     * @param command 获取成员列表请求
     */
    private void handleGetChatMembers(Command command) {
        Long userId = command.getRequesterId();
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long。Gson会转成Double。
        Long chatId = ((Number) data.get("chatId")).longValue(); // 获取目标聊天室ID

        // 第一步：查询该聊天室的所有成员user_id
        List<Long> userIds = chatMemberDAO.findUserIdsByChatId(chatId);
        // 第二步：根据每个user_id查询用户详细信息
        List<User> members = new ArrayList<>();
        for (Long uid : userIds) {
            User user = userDAO.findById(uid);
            if (user != null) {
                members.add(user);
            }
        }
        // 返回成员列表给客户端
        clientHandler.sendResponse(userId, Cmd.CHAT_MEMBERS_RESULT, ResultCode.SUCCESS, members);
    }
}