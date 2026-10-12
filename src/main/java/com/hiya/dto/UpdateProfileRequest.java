package com.hiya.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 修改"我自己的资料"的请求参数（对应 PUT /api/me）。
 *
 * 【设计要点：所有字段都可选】
 *   前端可能只想改昵称，不想动头像——所以每个字段都允许为 null，
 *   Service 里只更新"传了值"的字段（null 表示"这项不改"）。
 *   这就是为什么这里没有 @NotBlank 之类的必填校验。
 *
 * 对应数据库表 hiya_user_profile 的可改字段：
 *   nick_name（昵称）、avatar_url（头像）、signature（签名）、gender（性别）、birthday（生日）
 *
 * 注意：用户名（user_name）和密码（password）不在这里——用户名是登录凭据，
 *   改密码有单独接口（PUT /api/me/password，需要验证原密码）。
 */
public class UpdateProfileRequest {

    /** Hiya 昵称 */
    @Size(max = 32, message = "昵称不能超过 32 个字符")
    private String nickName;

    /** 头像 URL */
    @Size(max = 256, message = "头像地址不能超过 256 个字符")
    private String avatarUrl;

    /** 个性签名 */
    @Size(max = 128, message = "签名不能超过 128 个字符")
    private String signature;

    /** 性别：0不便透露，1男，2女 */
    private Integer gender;

    /**
     * 生日（格式 2000-01-01）。
     * @JsonFormat 告诉 Jackson：前端传来的日期字符串按 "yyyy-MM-dd" 解析成 LocalDate。
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

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
