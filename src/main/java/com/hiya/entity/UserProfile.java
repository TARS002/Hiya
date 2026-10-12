package com.hiya.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * 用户资料实体：对应数据库 hiya_user_profile 表。
 * 存储用户的个性化展示信息（昵称、头像、签名等）。
 *
 * 注意：这里"资料"与"账号"分离（hiya_user 管账号，hiya_user_profile 管展示），
 * 是旧系统的设计——用户基本信息查询（GET /info/{targetUserId}）会把两者合并返回。
 * 本实体只做查询（旧系统没有修改资料的指令），字段与旧表一致。
 *
 * birthday 列是 DATE 类型 → Java 用 LocalDate 对应（只精确到天）。
 */
@Table(name = "hiya_user_profile")
@Entity
public class UserProfile {

    /**
     * 资料ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Long userProfileId;

    /**
     * 关联的用户ID（数据库有外键 fk_hiya_user_id 指向 hiya_user.user_id）
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Hiya 昵称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 头像 URL 地址
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 个性签名
     */
    @Column(name = "signature")
    private String signature;

    /**
     * 性别：0不便透露，1男，2女
     */
    @Column(name = "gender")
    private Integer gender;

    /**
     * 生日（DATE 类型 → LocalDate）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "birthday")
    private LocalDate birthday;

    public Long getUserProfileId() { return userProfileId; }
    public void setUserProfileId(Long userProfileId) { this.userProfileId = userProfileId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
}
