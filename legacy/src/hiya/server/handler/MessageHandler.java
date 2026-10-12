package hiya.server.handler;

import hiya.common.command.*;
import hiya.common.model.Message;
import hiya.server.db.ChatMemberDAO;
import hiya.server.db.MessageDAO;
import hiya.server.server.ClientHandler;

import java.util.List;
import java.util.Map;

/**
 * 消息业务处理器。处理消息发送和聊天记录查询。
 * 发送消息时：先存数据库，成功后广播给聊天室其他成员，失败则返回错误给发送者。
 */
public class MessageHandler implements Handler {
    private ClientHandler clientHandler;               // 客户端连接处理器
    private MessageDAO messageDAO = new MessageDAO();  // 消息DAO
    private ChatMemberDAO chatMemberDAO = new ChatMemberDAO(); // 聊天室成员DAO

    public MessageHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 处理消息相关指令，根据cmd类型分别处理。
     * @param command 客户端发来的指令
     */
    @Override
    public void handle(Command command) {
        switch (command.getCmdEnum()) {
            case SEND_MESSAGE:
                handleSendMessage(command);
                break;
            case GET_CHAT_MESSAGES:
                handleGetChatMessages(command);
                break;
            default:
                break;
        }
    }

    /**
     * 处理发送消息请求。这是最复杂的功能：
     * 1. 从指令中提取chat_id、msg_type、content
     * 2. 向数据库插入消息记录
     * 3. 插入成功：向发送者返回成功，并向聊天室其他所有在线成员广播该消息
     * 4. 插入失败：向发送者返回失败结果
     * @param command 发送消息请求
     */
    private void handleSendMessage(Command command) {
        Long senderId = command.getRequesterId(); // 发送者ID
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long。Gson会转成Double。
        Long chatId = ((Number) data.get("chatId")).longValue();     // 聊天室ID
        int msgType = ((Number) data.get("msgType")).intValue(); // 消息类型
        String content = data.get("content").toString();                // 消息内容

        // 第一步：将消息存入数据库
        Message savedMsg = messageDAO.insert(senderId, chatId, msgType, content);

        if (savedMsg != null) {
            // 第二步：数据库插入成功，向发送者返回成功响应
            clientHandler.sendResponse(senderId, Cmd.SEND_MESSAGE_RESULT,
                    ResultCode.SUCCESS, Map.of("message", "发送成功"));

            // 第三步：查询该聊天室的所有成员ID
            List<Long> memberIds = chatMemberDAO.findUserIdsByChatId(chatId);

            // 第四步：遍历成员列表，将消息广播给除发送者外的所有在线成员
            for (Long memberId : memberIds) {
                // if (!memberId.equals(senderId)) { // 不广播给发送者自己
                    ClientHandler memberHandler = clientHandler.getServer().getOnlineClients().get(memberId);
                    if (memberHandler != null) {
                        // 该成员在线，发送NOTIFY类型的BROADCAST_MESSAGE
                        memberHandler.sendNotify(memberId, Cmd.BROADCAST_MESSAGE, savedMsg);
                    }
                // }
            }
        } else {
            // 数据库插入失败，向发送者返回失败响应
            clientHandler.sendResponse(senderId, Cmd.SEND_MESSAGE_RESULT,
                    ResultCode.SERVER_ERROR, Map.of("message", "消息发送失败，服务器错误"));
        }
    }

    /**
     * 处理获取聊天记录请求。查询指定聊天室的所有消息并返回。
     * @param command 获取聊天记录请求
     */
    private void handleGetChatMessages(Command command) {
        Long userId = command.getRequesterId();
        Map<String, Object> data = command.getDataAs(Map.class);
        // 同样先转Number再转Long。
        Long chatId = ((Number) data.get("chatId")).longValue(); // 目标聊天室ID

        // 查询该聊天室的所有消息记录
        List<Message> messages = messageDAO.findByChatId(chatId);
        // 返回聊天记录
        clientHandler.sendResponse(userId, Cmd.CHAT_MESSAGES_RESULT, ResultCode.SUCCESS, messages);
    }
}