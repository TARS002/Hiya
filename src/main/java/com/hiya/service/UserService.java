package com.hiya.service;

import com.hiya.dto.LoginRequest;
import com.hiya.dto.RegisterRequest;
import com.hiya.dto.UpdatePasswordRequest;
import com.hiya.dto.UpdateProfileRequest;
import com.hiya.dto.UserInfoVO;
import com.hiya.entity.User;

/**
 * 用户业务接口
 *
 *  - 注册：register
 *  - 登录：login
 *  - 查询自己的数据：getById / getUserInfo
 *  - 查询别人的数据：getUserInfo（同一个方法，传别人的 ID 即可）
 *  - 修改自己的数据：updateProfile（资料）/ updatePassword（密码）
 */
public interface UserService {

    /** 注册新用户，返回带主键的完整用户实体 */
    User register(RegisterRequest request);

    /** 登录校验（含封禁/注销校验 + 写登录日志），成功返回用户实体 */
    User login(LoginRequest request);

    /** 根据 ID 查用户（账号信息），查不到抛业务异常 */
    User getById(Long userId);

    /** 查询用户完整信息：账号信息 + 资料信息合并返回（自己或别人的都可以查） */
    UserInfoVO getUserInfo(Long targetUserId);

    /**
     * 修改自己的资料（昵称/头像/签名/性别/生日）。
     * 只更新请求里传了值的字段；用户还没有资料记录时自动创建一条。
     * @return 修改后的完整信息
     */
    UserInfoVO updateProfile(Long userId, UpdateProfileRequest request);

    /** 修改自己的密码（需要验证原密码） */
    void updatePassword(Long userId, UpdatePasswordRequest request);
}
