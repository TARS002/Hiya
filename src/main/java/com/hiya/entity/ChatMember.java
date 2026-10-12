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
 * 会话成员实体：对应数据库 hiya_chat_member 表。
 * 记录"用户属于哪些会话"以及在该会话中的角色/禁言状态。
 *
 * 注意：实体字段比旧模型类多（update_time、mute_until），
 * 这两个列在真实表结构里存在（旧模型类只是没有读出来）。
 *
 * 数据库层面 (chat_id, user_id) 有 UNIQUE 约束（idx_group_member），
 * 一个用户在一个会话里只能有一条记录。
 */
@Table(name = "hiya_chat_member")
@Entity
public class ChatMember {

    /**
     * 记录ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 所属聊天会话ID
     */
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * 成员用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 角色：0群员，1绿标，2群主
     */
    @Column(name = "role")
    private Integer role;

    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "join_time")
    private LocalDateTime joinTime;

    /**
     * 记录最后更新时间（数据库自动维护）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否被禁言：0否，1是
     */
    @Column(name = "is_muted")
    private Integer isMuted;

    /**
     * 禁言截止时间（数据库 NOT NULL DEFAULT CURRENT_TIMESTAMP，插入时需显式赋值）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "mute_until")
    private LocalDateTime muteUntil;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public LocalDateTime getJoinTime() { return joinTime; }
    public void setJoinTime(LocalDateTime joinTime) { this.joinTime = joinTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getIsMuted() { return isMuted; }
    public void setIsMuted(Integer isMuted) { this.isMuted = isMuted; }
    public LocalDateTime getMuteUntil() { return muteUntil; }
    public void setMuteUntil(LocalDateTime muteUntil) { this.muteUntil = muteUntil; }
}
