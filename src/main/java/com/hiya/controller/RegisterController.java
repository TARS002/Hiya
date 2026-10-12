package com.hiya.controller;

import com.hiya.common.Result;
import com.hiya.dto.RegisterRequest;
import com.hiya.entity.User;
import com.hiya.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册控制器（入口：POST /api/register）。
 *
 * 【类上的 @RequestMapping("/api") 有什么用？】
 *   它是"类级别路径前缀"：本类所有方法的路径都会自动加上 /api。
 *   所以方法上写 "/register"，实际对外就是 /api/register。
 *   好处：所有业务接口统一带 /api 前缀，和页面（/test.html）区分开——
 *   这也呼应了"页面 URL 与接口 URL 要分开"的设计习惯。
 *
 * 【不需要身份】注册时当然还没有 userId，所以它在拦截器白名单里（见 WebMvcConfig）。
 *
 * Controller 的职责永远是三件事：收参数 → 调 Service → 包成 Result 返回。
 * 真正的业务规则（用户名是否重复、默认状态怎么设置）在 UserServiceImpl.register 里。
 */
@RestController
@RequestMapping("/api")
public class RegisterController {

    private final UserService userService;

    // 构造器注入：Spring 自动把 UserService 的实现类传进来（原理见 UserServiceImpl 类注释）
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册：POST /api/register
     * 请求体 {"userName":"xxx","password":"xxx"}
     *
     * @Valid 会执行 RegisterRequest 字段上的校验注解（用户名长度、密码长度等），
     * 校验失败抛异常 → common/GlobalExceptionHandler 统一转成 400 返回。
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        // 返回的 User 里 password 字段不会出现在 JSON 中（实体上标了 @JsonProperty(WRITE_ONLY)）
        return Result.ok(userService.register(request));
    }
}
