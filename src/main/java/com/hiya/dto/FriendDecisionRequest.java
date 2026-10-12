package com.hiya.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 好友请求的"同意/拒绝"请求参数。
 * 对应旧系统 ADD_FRIEND_ACCEPT / ADD_FRIEND_REJECT 指令的 data：{requesterId}。
 * requesterId 是【发起好友请求的那个人】的 ID。
 */
public class FriendDecisionRequest {

    /**
     * 发起好友请求的用户ID
     */
    @NotNull(message = "requesterId 不能为空")
    private Long requesterId;

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
}
