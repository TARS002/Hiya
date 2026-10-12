package hiya.server.handler;

import hiya.common.command.*;
import hiya.common.model.Friend;
import hiya.common.model.User;
import hiya.server.db.FriendDAO;
import hiya.server.db.UserDAO;
import hiya.server.server.ClientHandler;

import java.util.Map;

/**
 * 好友业务处理器。处理好友列表获取、好友请求发送/接受/拒绝、删除好友等。
 * <h3>本类核心成员变量</h3>
 * <ul>
 *   <li>{@link #clientHandler}：客户端连接处理器，用于与客户端通信。</li>
 *   <li>{@link #friendDAO}：好友关系DAO，用于操作好友关系数据。</li>
 *   <li>{@link #userDAO}：用户DAO，用于操作用户数据。</li>
 * </ul>
 */
public class FriendHandler implements Handler {
    private ClientHandler clientHandler;        // 客户端连接处理器
    private FriendDAO friendDAO = new FriendDAO(); // 好友关系DAO
    private UserDAO userDAO = new UserDAO();       // 用户DAO

    public FriendHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 处理好友相关的指令，根据cmd类型分别处理。
     * @param command 客户端发来的指令
     */
    @Override
    public void handle(Command command) {
        switch (command.getCmdEnum()) {
            case GET_FRIEND_LIST:
                handleGetFriendList(command);
                break;
            case ADD_FRIEND_REQUEST:
                handleAddFriendRequest(command);
                break;
            case ADD_FRIEND_ACCEPT:
                handleAddFriendAccept(command);
                break;
            case ADD_FRIEND_REJECT:
                handleAddFriendReject(command);
                break;
            case DELETE_FRIEND:
                handleDeleteFriend(command);
                break;
            default:
                break;
        }
    }

    /**
     * 处理获取好友列表请求。查hiya_friend获取friend_id列表，返回好友关系记录。
     * @param command 获取好友列表请求
     */
    private void handleGetFriendList(Command command) {
        Long userId = command.getRequesterId();
        // 查询该用户的所有未删除好友关系
        java.util.List<Friend> friendList = friendDAO.findFriendsByUserId(userId);
        // 返回好友列表
        clientHandler.sendResponse(userId, Cmd.FRIEND_LIST_RESULT, ResultCode.SUCCESS, friendList);
    }

    /**
     * 处理添加好友请求。检查是否已是好友，不是则通知目标用户。
     * @param command 添加好友请求
     */
    private void handleAddFriendRequest(Command command) {
        Long userId = command.getRequesterId(); // 发起请求的用户ID
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long。Gson会转成Double。
        Long friendId = ((Number) data.get("friendId")).longValue(); // 目标好友ID

        // 检查不能添加自己为好友
        if (userId.equals(friendId)) {
            clientHandler.sendResponse(userId, Cmd.ADD_FRIEND_RESULT,
                    ResultCode.FAILURE, Map.of("message", "不能添加自己为好友"));
            return;
        }

        // 检查是否已经是好友
        if (friendDAO.isFriend(userId, friendId)) {
            clientHandler.sendResponse(userId, Cmd.ADD_FRIEND_RESULT,
                    ResultCode.ALREADY_EXISTS, Map.of("message", "已经是好友了"));
            return;
        }

        // 检查目标用户是否在线
        ClientHandler targetHandler = clientHandler.getServer().getOnlineClients().get(friendId);
        if (targetHandler == null) {
            // 目标用户不在线，无法发送请求
            clientHandler.sendResponse(userId, Cmd.ADD_FRIEND_RESULT,
                    ResultCode.FAILURE, Map.of("message", "目标用户不在线"));
            return;
        }

        // 查询发起请求的用户名
        User requester = userDAO.findById(userId);
        String requesterName = requester != null ? requester.getUserName() : "未知用户";

        // 向目标用户发送好友请求通知（NOTIFY类型）
        targetHandler.sendNotify(friendId, Cmd.FRIEND_REQUEST_NOTIFY,
                Map.of("requesterId", userId, "requesterName", requesterName));

        // 暂时不给发起方返回结果，等对方同意或拒绝后再通知
    }

    /**
     * 处理同意好友请求。在hiya_friend中插入双向好友关系，并通知发起方。
     * @param command 同意好友请求指令
     */
    private void handleAddFriendAccept(Command command) {
        Long userId = command.getRequesterId(); // 被请求方（同意方）ID
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long。Gson会转成Double。
        Long requesterId = ((Number) data.get("requesterId")).longValue(); // 发起方ID

        // 插入双向好友关系
        boolean success = friendDAO.insertFriend(requesterId, userId);
        if (success) {
            // 通知发起方：好友请求已被接受
            ClientHandler requesterHandler = clientHandler.getServer().getOnlineClients().get(requesterId);
            if (requesterHandler != null) {
                requesterHandler.sendNotify(requesterId, Cmd.ADD_FRIEND_RESULT,
                        Map.of("message", "对方已同意你的好友请求"));
            }
        }
    }

    /**
     * 处理拒绝好友请求。直接通知发起方被拒绝。
     * @param command 拒绝好友请求指令
     */
    private void handleAddFriendReject(Command command) {
        Long userId = command.getRequesterId(); // 被请求方（拒绝方）ID
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long。Gson会转成Double。
        Long requesterId = ((Number) data.get("requesterId")).longValue(); // 发起方ID

        // 通知发起方：好友请求被拒绝
        ClientHandler requesterHandler = clientHandler.getServer().getOnlineClients().get(requesterId);
        if (requesterHandler != null) {
            requesterHandler.sendNotify(requesterId, Cmd.ADD_FRIEND_RESULT,
                    Map.of("message", "对方拒绝了你的好友请求", "success", "false"));
        }
    }

    /**
     * 处理删除好友请求。将hiya_friend中对应记录的is_deleted改为1，双向删除。
     * @param command 删除好友请求
     */
    private void handleDeleteFriend(Command command) {
        Long userId = command.getRequesterId();
        Map<String, Object> data = command.getDataAs(Map.class);
        // 先转Number再转Long。Gson会转成Double。
        Long friendId = ((Number) data.get("friendId")).longValue();

        // 执行双向逻辑删除
        boolean success = friendDAO.deleteFriend(userId, friendId);
        if (success) {
            clientHandler.sendResponse(userId, Cmd.DELETE_FRIEND_RESULT,
                    ResultCode.SUCCESS, Map.of("message", "删除好友成功"));
        } else {
            clientHandler.sendResponse(userId, Cmd.DELETE_FRIEND_RESULT,
                    ResultCode.FAILURE, Map.of("message", "删除好友失败"));
        }
    }
}