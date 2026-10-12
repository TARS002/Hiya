package com.hiya.service.impl;

import com.hiya.entity.Friend;
import com.hiya.entity.User;
import com.hiya.repository.FriendRepository;
import com.hiya.repository.UserRepository;
import com.hiya.service.FriendService;
import com.hiya.ws.SessionManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 好友接口实现类。
 *
 * 好友关系在数据库里是两行
 * 删好友把这两行逻辑删除。
 *
 * 申请加好友的流程：A 申请 → 服务器通过 WebSocket 通知 B → B 同意/拒绝 → 通知 A。
 */
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public FriendServiceImpl(FriendRepository friendRepository,
                             UserRepository userRepository,
                             SessionManager sessionManager) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    /** 获取用户的好友列表 */
    @Override
    public List<Friend> getFriendList(Long userId) {
        return friendRepository.findByUserIdAndIsDeleted(userId, 0);
    }

    /** 获取用户的在线好友 */
    @Override
    public Set<Long> getOnlineUserIds() {
        // 在线状态不落库，直接问 WebSocket 的会话管理器要（谁连着谁就在线）
        return sessionManager.onlineUserIds();
    }

    /** 收到他人的好友申请 */
    @Override
    public void addFriendRequest(Long requesterId, Long friendId) {
        // 查一下申请人的名字，拼进通知里
        User requester = userRepository.findById(requesterId).orElse(null);
        String requesterName = requester != null ? requester.getUserName() : "未知用户";

        // 通过 WebSocket 通知对方
        sessionManager.pushToUser(friendId, "FRIEND_REQUEST_NOTIFY",
                Map.of("requesterId", requesterId, "requesterName", requesterName));
    }

    /** 接收对方的好友申请 */
    @Override
    public void acceptFriendRequest(Long accepterId, Long requesterId) {
        // 1. 双向插入好友关系
        insertFriendBidirectional(requesterId, accepterId);
        // 2. 通知申请人：对方同意了
        sessionManager.pushToUser(requesterId, "ADD_FRIEND_RESULT",
                Map.of("message", "对方已同意你的好友请求", "success", true));
    }

    /** 拒绝对方的好友申请 */
    @Override
    public void rejectFriendRequest(Long rejecterId, Long requesterId) {
        // 通知发起方已拒绝
        sessionManager.pushToUser(requesterId, "ADD_FRIEND_RESULT",
                Map.of("message", "对方拒绝了你的好友请求", "success", false));
    }

    /** 两人删除好友。 */
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        // 把 A→B 和 B→A 两条记录的 is_deleted 置 1
        deleteFriendRow(userId, friendId);
        deleteFriendRow(friendId, userId);
    }

    /**
     * 插入一对双向好友关系（acceptFriendRequest使用）
     */
    private void insertFriendBidirectional(Long userA, Long userB) {
        if (!friendRepository.existsByUserIdAndFriendIdAndIsDeleted(userA, userB, 0)) {
            friendRepository.save(buildFriend(userA, userB));
            friendRepository.save(buildFriend(userB, userA));
        }
    }

    /**
     * 组装一条好友关系记录
     */
    private Friend buildFriend(Long userId, Long friendId) {
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus(0); // 0=正常好友，1=拉黑（拉黑功能旧系统未实现指令）
        friend.setCreateTime(LocalDateTime.now());
        friend.setUpdateTime(LocalDateTime.now());
        friend.setIsDeleted(0);
        return friend;
    }

    /**
     * 逻辑删除一条好友关系记录
     */
    private void deleteFriendRow(Long userId, Long friendId) {
        friendRepository.findByUserIdAndFriendIdAndIsDeleted(userId, friendId, 0)
                .ifPresent(friend -> {
                    friend.setIsDeleted(1);
                    friend.setUpdateTime(LocalDateTime.now());
                    friendRepository.save(friend); // 有主键的实体 save = UPDATE
                });
    }
}
