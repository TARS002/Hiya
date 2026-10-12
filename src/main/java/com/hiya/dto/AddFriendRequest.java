package com.hiya.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 发送好友请求的请求参数。
 */
public class AddFriendRequest {

    /**
     * 目标好友的用户ID
     */
    @NotNull(message = "friendId 不能为空")
    private Long friendId;

    public Long getFriendId() { return friendId; }
    public void setFriendId(Long friendId) { this.friendId = friendId; }
}
