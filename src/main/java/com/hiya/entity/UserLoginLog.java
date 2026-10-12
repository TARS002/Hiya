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
 * 登录日志实体：对应数据库 hiya_user_login_log 表。
 * 旧系统登录成功后都会往这张表插一条记录（记录"谁在什么时候登录"），
 * 用于审计/统计，是还原旧行为的一部分。
 *
 * 真实表结构：id（自增）、user_id、login_time（DATETIME，默认当前时间）。
 */
@Table(name = "hiya_user_login_log")
@Entity
public class UserLoginLog {

    /**
     * 日志ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 登录的用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "login_time")
    private LocalDateTime loginTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
}
