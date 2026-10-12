package com.hiya.dto;

import com.hiya.entity.User;
import com.hiya.entity.UserProfile;

/**
 * 用户信息聚合 VO（View Object，视图对象）：
 * 把"账号信息 + 资料信息"合并成一个对象返回给前端。
 * 对应旧系统的 GET_USER_INFO 指令：data 里同时放 user 和 profile。
 *
 * 为什么不直接返回两个字段的 Map？
 *   Map 没有类型约束，前端拿到的结构不清晰，代码里也容易写错 key。
 *   用 VO 类承载，字段名、类型一目了然（这也是 DTO/VO 存在的意义）。
 */
public class UserInfoVO {

    /**
     * 用户账号信息
     */
    private User user;

    /**
     * 用户资料信息（可能为 null：旧系统不保证每个用户都有资料）
     */
    private UserProfile profile;

    public UserInfoVO() {
    }

    public UserInfoVO(User user, UserProfile profile) {
        this.user = user;
        this.profile = profile;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }
}
