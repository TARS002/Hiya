package com.hiya.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 创建私聊会话的请求参数。
 */
public class CreatePrivateChatRequest {

    /**
     * 要私聊的好友用户ID
     */
    @NotNull(message = "friendId 不能为空")
    private Long friendId;

    public Long getFriendId() { return friendId; }
    public void setFriendId(Long friendId) { this.friendId = friendId; }
}
