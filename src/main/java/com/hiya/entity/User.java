package com.hiya.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 用户实体：对应数据库 hiya_user 表。
 *
 * JPA（Java Persistence API）的核心思想：实体类 <-> 数据库表 的映射。
 * Hibernate 是 JPA 规范的实现，SpringBoot 默认用它。
 *
 * 常用映射注解：
 *   - @Entity         声明这是一个 JPA 实体（会被 Hibernate 管理）
 *   - @Table(name=..) 映射到哪张表（不写则默认以类名建表）
 *   - @Id             标记主键
 *   - @GeneratedValue(strategy = GenerationType.IDENTITY) 主键由数据库自增生成
 *   - @Column(name=..) 属性映射到哪一列（不写则默认以属性名作列名）
 *
 * 时间字段为什么用 LocalDateTime 而不是 String？
 *   旧版代码用 String 读 datetime 列（JDBC 会自动转字符串），但真实表结构里
 *   create_time 等是 DATETIME 类型。JPA 中"Java 时间类型 ↔ 数据库时间类型"
 *   的正确映射就是 LocalDateTime ↔ DATETIME，读写交给 Hibernate 处理，
 *   不要再手动转字符串。
 *
 * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")：控制序列化成 JSON 时的输出格式，
 *   保证前端看到的时间仍是 "2026-06-14 18:01:42" 这种样式（和旧系统一致）。
 *
 * 注意事项：
 *   - 实体必须有无参构造器（JPA 反射创建对象需要），本类没写构造器 = 默认无参构造器
 *   - getter/setter 必须齐全（Jackson 序列化、Hibernate 读写字段都需要）
 */
@Table(name = "hiya_user")
@Entity
public class User {

    /**
     * 用户ID，主键自增（由数据库生成，程序不设置）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户自定义名称，登录时使用（数据库有唯一索引 idx_user_name）
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 密码。
     * @JsonProperty(access = Access.WRITE_ONLY)：Jackson 序列化（Java→JSON）时忽略该字段，
     * 反序列化（JSON→Java）时正常接收。
     * 效果：前端可以传密码进来，但任何接口返回的 JSON 里都不会出现密码（防止泄露）。
     * TODO(旧系统兼容): 旧库密码是明文（测试账号密码=用户名），保持明文以便旧数据可登录；
     *   后续可考虑 BCrypt 加密升级。
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    /**
     * 是否被封禁：0未封禁，1已封禁（数据库 NOT NULL DEFAULT 0）
     */
    @Column(name = "is_banned")
    private Integer isBanned;

    /**
     * 解封时间（数据库 NOT NULL DEFAULT CURRENT_TIMESTAMP，插入时需显式赋值）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "unban_time")
    private LocalDateTime unbanTime;

    /**
     * 账号创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 数据最后更新时间（数据库 ON UPDATE CURRENT_TIMESTAMP 自动维护）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否被删除（逻辑删除）：0未删除，1已删除
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;

    // ===== Getter / Setter =====
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getIsBanned() { return isBanned; }
    public void setIsBanned(Integer isBanned) { this.isBanned = isBanned; }
    public LocalDateTime getUnbanTime() { return unbanTime; }
    public void setUnbanTime(LocalDateTime unbanTime) { this.unbanTime = unbanTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
