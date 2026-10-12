package com.hiya.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码的请求参数（对应 PUT /api/me/password）。
 *
 * 为什么要传原密码？
 *   防止"别人拿到你的 userId 就能改你密码"——改密码前先验证身份
 *   （本项目整体没有认证，但涉及密码变更这种操作还是保留原密码校验，
 *    属于最小的自我保护，代码也就多 3 行）。
 *
 * 注意：本项目密码是明文存储（兼容旧库），所以直接字符串比较即可。
 */
public class UpdatePasswordRequest {

    /** 原密码 */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度需在 6~32 个字符之间")
    private String newPassword;

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
