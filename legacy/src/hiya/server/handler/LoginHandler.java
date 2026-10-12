package hiya.server.handler;

import hiya.common.command.*;
import hiya.common.model.User;
import hiya.server.db.UserDAO;
import hiya.server.server.ClientHandler;

import java.util.Map;

/**
 * 登录业务处理器。验证用户名密码，成功则记录登录日志并返回用户信息。
 */
public class LoginHandler implements Handler {
    private ClientHandler clientHandler; // 客户端连接处理器
    private UserDAO userDAO = new UserDAO(); // 用户数据访问对象

    /**
     * 构造登录处理器。
     * @param clientHandler 客户端连接处理器
     */
    public LoginHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 处理登录请求。从指令data中提取用户名密码，查询数据库验证。
     * @param command 登录请求指令
     */
    @Override
    @SuppressWarnings("unchecked")
    public void handle(Command command) {
        // 从data中提取用户名和密码
        Map<String, String> data = command.getDataAs(Map.class);
        String userName = data.get("userName"); // 获取用户名
        String password = data.get("password"); // 获取密码

        // 调用DAO查询数据库，验证用户名和密码
        User user = userDAO.findByUserNameAndPassword(userName, password);
        if (user != null) {
            // 验证成功，记录登录日志
            userDAO.insertLoginLog(user.getUserId());
            // 将该用户的连接注册到在线用户列表中
            clientHandler.getServer().getOnlineClients().put(user.getUserId(), clientHandler);
            // 将当前连接绑定到该用户ID
            clientHandler.setUserId(user.getUserId());
            // 返回登录成功响应，data中携带用户信息
            clientHandler.sendResponse(user.getUserId(), Cmd.LOGIN_SUCCESS, ResultCode.SUCCESS, user);
        } else {
            // 验证失败，返回登录失败响应
            clientHandler.sendResponse(command.getRequesterId(), Cmd.LOGIN_FAILURE,
                    ResultCode.FAILURE, Map.of("message", "用户名或密码错误"));
        }
    }
}