package hiya.server.handler;

import hiya.common.command.*;
import hiya.common.model.User;
import hiya.common.model.UserProfile;
import hiya.server.db.UserDAO;
import hiya.server.db.UserProfileDAO;
import hiya.server.server.ClientHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息业务处理器。处理查询用户基本信息和资料信息的请求。
 */
public class UserHandler implements Handler {
    private ClientHandler clientHandler;               // 客户端连接处理器
    private UserDAO userDAO = new UserDAO();           // 用户DAO
    private UserProfileDAO userProfileDAO = new UserProfileDAO(); // 用户资料DAO

    public UserHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 处理用户信息查询请求。分别查hiya_user和hiya_user_profile，合并后返回。
     * @param command 查询用户信息请求
     */
    @Override
    public void handle(Command command) {
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long
        Long targetUserId = ((Number) data.get("targetUserId")).longValue(); // 目标用户ID

        // 第一步：查询用户基本信息
        User user = userDAO.findById(targetUserId);
        if (user == null) {
            // 用户不存在
            clientHandler.sendResponse(command.getRequesterId(), Cmd.USER_INFO_RESULT,
                    ResultCode.NOT_FOUND, Map.of("message", "用户不存在"));
            return;
        }

        // 第二步：查询用户资料信息
        UserProfile profile = userProfileDAO.findByUserId(targetUserId);

        // 第三步：将user和profile封装到data中返回
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("user", user);       // 放入用户基本信息
        resultData.put("profile", profile); // 放入用户资料信息
        clientHandler.sendResponse(command.getRequesterId(), Cmd.USER_INFO_RESULT, ResultCode.SUCCESS, resultData);
    }
}