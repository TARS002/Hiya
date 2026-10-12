package com.hiya.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建聊天室（群聊）的请求参数（对应 POST /api/chat/room）。
 *
 * 对应数据库 hiya_chat 表：type=2 表示群聊，chat_title 存群名称。
 * 创建者会自动成为群成员，并且角色是"群主"（role=2）。
 */
public class CreateRoomRequest {

    /** 群名称 */
    @NotBlank(message = "群名称不能为空")
    @Size(max = 64, message = "群名称不能超过 64 个字符")
    private String chatTitle;

    public String getChatTitle() { return chatTitle; }
    public void setChatTitle(String chatTitle) { this.chatTitle = chatTitle; }
}
