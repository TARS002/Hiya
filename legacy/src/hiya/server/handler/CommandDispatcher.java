package hiya.server.handler;

import hiya.common.command.Cmd;
import hiya.common.command.Command;
import hiya.server.server.ClientHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令分发器，接收网络层解析好的Command对象，根据cmd字段分发到对应的Handler处理。
 * 每个ClientHandler持有一个CommandDispatcher实例。
 */
public class CommandDispatcher {
    private Map<String, Handler> handlerMap = new HashMap<>(); // Handler注册表，key为Cmd名称
    private ClientHandler clientHandler;                        // 所属的客户端连接处理器

    /**
     * 构造指令分发器，注册所有业务Handler。
     * @param clientHandler 所属的客户端连接处理器，Handler通过它发送响应和访问在线用户列表
     */
    public CommandDispatcher(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        // 创建各业务Handler并注册
        registerHandler(Cmd.LOGIN, new LoginHandler(clientHandler));
        registerHandler(Cmd.GET_CHAT_LIST, new ChatHandler(clientHandler));
        registerHandler(Cmd.CREATE_PRIVATE_CHAT, new ChatHandler(clientHandler));
        registerHandler(Cmd.GET_CHAT_MEMBERS, new ChatHandler(clientHandler));
        registerHandler(Cmd.GET_FRIEND_LIST, new FriendHandler(clientHandler));
        registerHandler(Cmd.ADD_FRIEND_REQUEST, new FriendHandler(clientHandler));
        registerHandler(Cmd.ADD_FRIEND_ACCEPT, new FriendHandler(clientHandler));
        registerHandler(Cmd.ADD_FRIEND_REJECT, new FriendHandler(clientHandler));
        registerHandler(Cmd.DELETE_FRIEND, new FriendHandler(clientHandler));
        registerHandler(Cmd.GET_USER_INFO, new UserHandler(clientHandler));
        registerHandler(Cmd.SEND_MESSAGE, new MessageHandler(clientHandler));
        registerHandler(Cmd.GET_CHAT_MESSAGES, new MessageHandler(clientHandler));
    }

    /**
     * 注册一个Handler到指定cmd名称下。
     * @param cmd 指令类型
     * @param handler 处理该指令的Handler
     */
    private void registerHandler(Cmd cmd, Handler handler) {
        handlerMap.put(cmd.name(), handler);
    }

    /**
     * 分发指令到对应的Handler处理。
     * @param command 要分发的指令对象
     */
    public void dispatch(Command command) {
        // 根据cmd名称查找对应的Handler
        Handler handler = handlerMap.get(command.getCmd());
        if (handler != null) {
            // 找到Handler，调用其handle方法处理
            handler.handle(command);
        } else {
            // 未找到对应Handler，返回不支持的指令错误
            clientHandler.sendResponse(command.getRequesterId(), Cmd.LOGIN_FAILURE,
                    hiya.common.command.ResultCode.FAILURE, Map.of("message", "不支持的指令: " + command.getCmd()));
        }
    }
}
