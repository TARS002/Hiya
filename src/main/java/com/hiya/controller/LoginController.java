package com.hiya.controller;

import com.hiya.common.Result;
import com.hiya.dto.LoginRequest;
import com.hiya.dto.LoginResponse;
import com.hiya.dto.UpdatePasswordRequest;
import com.hiya.dto.UpdateProfileRequest;
import com.hiya.dto.UserInfoVO;
import com.hiya.entity.User;
import com.hiya.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录 / 用户信息控制器。
 *
 * 接口一览：
 *   POST /api/register?            注册（在 RegisterController）
 *   POST /api/login                登录 → 返回 userId
 *   GET  /api/me?userId=1          查我自己的账号信息
 *   GET  /api/me/info?userId=1     查我自己的账号 + 资料
 *   PUT  /api/me?userId=1          改我自己的资料
 *   PUT  /api/me/password?userId=1 改我自己的密码
 *   GET  /api/info/2               查别人的账号 + 资料
 *
 * 【身份怎么传】直接用 URL 参数 userId，比如 /api/me?userId=1。
 * 谁需要知道"你是谁"，谁就在方法上加 @RequestParam("userId") Long userId。
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /** 登录：POST /api/login，body {"userName":"xxx","password":"xxx"} */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        return Result.ok(new LoginResponse(user.getUserId(), user.getUserName()));
    }

    /** 查我自己的账号信息：GET /api/me?userId=1 */
    @GetMapping("/me")
    public Result<User> me(@RequestParam("userId") Long userId) {
        return Result.ok(userService.getById(userId));
    }

    /** 查我自己的账号 + 资料：GET /api/me/info?userId=1 */
    @GetMapping("/me/info")
    public Result<UserInfoVO> myInfo(@RequestParam("userId") Long userId) {
        return Result.ok(userService.getUserInfo(userId));
    }

    /** 改我自己的资料：PUT /api/me?userId=1，body {"nickName":"新昵称",...} */
    @PutMapping("/me")
    public Result<UserInfoVO> updateMyProfile(@RequestParam("userId") Long userId,
                                              @Valid @RequestBody UpdateProfileRequest request) {
        return Result.ok(userService.updateProfile(userId, request));
    }

    /** 改我自己的密码：PUT /api/me/password?userId=1，body {"oldPassword":"..","newPassword":".."} */
    @PutMapping("/me/password")
    public Result<Void> updateMyPassword(@RequestParam("userId") Long userId,
                                         @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(userId, request);
        return Result.ok();
    }

    /** 查别人的资料：GET /api/info/2 */
    @GetMapping("/info/{targetUserId}")
    public Result<UserInfoVO> info(@PathVariable("targetUserId") Long targetUserId) {
        return Result.ok(userService.getUserInfo(targetUserId));
    }
}
