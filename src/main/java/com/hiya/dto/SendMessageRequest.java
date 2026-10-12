package com.hiya.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 发送消息的请求参数。
 * 对应旧系统 SEND_MESSAGE 指令的 data：{chatId, msgType, content}（chatId 走 URL 路径）。
 */
public class SendMessageRequest {

    /**
     * 消息类型：1文本，2图片，3文件，4语音，5系统。
     * 不传默认按文本(1)处理（旧系统客户端实际只发文本）。
     */
    private Integer msgType;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过 2000 字")
    private String content;

    public Integer getMsgType() { return msgType; }
    public void setMsgType(Integer msgType) { this.msgType = msgType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
