package com.hiya.service;

import com.hiya.entity.Friend;

import java.util.List;
import java.util.Set;

/**
 * 好友业务接口
 *
 * 对应旧系统 FriendHandler 的五个指令（列表/请求/同意/拒绝/删除）+ 在线状态查询。
 *
 *   通过SessionManager推送 FRIEND_REQUEST_NOTIFY / ADD_FRIEND_RESULT 消息。
 */
public interface FriendService {

    /** 查询某用户的所有好友关系 */
    List<Friend> getFriendList(Long userId);

    /** 当前在线的用户 ID 集合（前端用来给好友列表标"在线"） */
    Set<Long> getOnlineUserIds();

    /** 发送好友请求：目标在线则推送通知；不在线/已是好友/加自己 都会抛业务异常 */
    void addFriendRequest(Long requesterId, Long friendId);

    /** 同意好友请求：双向插入好友关系，并通知请求方 */
    void acceptFriendRequest(Long accepterId, Long requesterId);

    /** 拒绝好友请求：通知请求方（不落库） */
    void rejectFriendRequest(Long rejecterId, Long requesterId);

    /** 删除好友：双向逻辑删除（is_deleted 置 1） */
    void deleteFriend(Long userId, Long friendId);
}
