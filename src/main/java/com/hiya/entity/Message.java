package com.hiya.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 聊天消息实体：对应数据库 hiya_message 表。
 * 存储聊天会话中的每一条消息。
 *
 * 旧系统约定：
 *   - msg_type：1文本，2图片，3文件，4语音，5系统指令（真实数据里目前只有文本=1）
 *   - is_deleted：消息"撤回"标记（0正常，1已撤回），撤回功能旧系统未实现指令，字段预留
 *   - content 列是 TEXT 类型，可存较长内容
 */
@Table(name = "hiya_message")
@Entity
public class Message {

    /**
     * 消息ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 发送者用户ID
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 所属聊天会话ID
     */
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * 消息类型：1文本，2图片，3文件，4语音，5系统指令
     */
    @Column(name = "msg_type")
    private Integer msgType;

    /**
     * 消息内容
     */
    @Column(name = "content")
    private String content;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "send_time")
    private LocalDateTime sendTime;

    /**
     * 是否被撤回：0未撤回，1已撤回
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Integer getMsgType() { return msgType; }
    public void setMsgType(Integer msgType) { this.msgType = msgType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
