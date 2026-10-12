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
 * 聊天会话实体：对应数据库 hiya_chat 表。
 * 每条记录代表一个聊天会话（私聊或群聊）。
 *
 * 旧系统约定：
 *   - type = 1 私聊，type = 2 群聊
 *   - chatTitle 群聊时是群名，私聊时可为空
 *   - 旧系统只有"创建私聊"指令，群聊字段已预留但未使用
 */
@Table(name = "hiya_chat")
@Entity
public class Chat {

    /**
     * 会话ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * 会话类型：1私聊，2群聊
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 会话标题：群聊时为群名，私聊时可为空
     */
    @Column(name = "chat_title")
    private String chatTitle;

    /**
     * 会话创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 会话最后更新时间（数据库 ON UPDATE CURRENT_TIMESTAMP 自动维护）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getChatTitle() { return chatTitle; }
    public void setChatTitle(String chatTitle) { this.chatTitle = chatTitle; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
