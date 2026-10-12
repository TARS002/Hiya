package com.hiya.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求参数。校验规则比注册宽松：用户名、密码非空即可，
 * 具体"用户名是否存在、密码对不对"属于业务校验，放在 Service 层做。
 */
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
