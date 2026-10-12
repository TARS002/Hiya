package com.hiya.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求参数（DTO：Data Transfer Object，数据传输对象）。
 *
 * 为什么要有 DTO？
 *   - 前端传来的 JSON 字段和数据库实体字段往往不完全一致（比如注册不需要传 userId）；
 *   - 直接用实体接收请求参数会暴露数据库结构，也不安全（攻击者可能伪造 id 覆盖数据）；
 *   - DTO 可以加校验注解（@NotBlank 等），在进入 Service 前就把脏数据挡在门外。
 *
 * 校验注解说明：
 *   - @NotBlank：字符串不能为 null、不能为空串、不能全是空格
 *   - @Size：长度范围
 *   校验由 Spring 的 Validator 执行，前提是 Controller 参数上写了 @Valid（后面会看到）。
 *   校验失败会抛 MethodArgumentNotValidException，由 GlobalExceptionHandler 统一处理。
 */
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度需在 2~20 个字符之间")
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度需在 6~32 个字符之间")
    private String password;

    // getter / setter：Jackson 反序列化（JSON→Java）需要 setter，序列化（Java→JSON）需要 getter
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
