package com.hiya.service.impl;

import com.hiya.common.BusinessException;
import com.hiya.common.ResultCode;
import com.hiya.dto.LoginRequest;
import com.hiya.dto.RegisterRequest;
import com.hiya.dto.UpdatePasswordRequest;
import com.hiya.dto.UpdateProfileRequest;
import com.hiya.dto.UserInfoVO;
import com.hiya.entity.User;
import com.hiya.entity.UserLoginLog;
import com.hiya.entity.UserProfile;
import com.hiya.repository.UserLoginLogRepository;
import com.hiya.repository.UserProfileRepository;
import com.hiya.repository.UserRepository;
import com.hiya.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Service
 * 告诉 Spring 把本类注册为一个业务组件 Bean。
 * Spring 启动时扫描到它，创建实例放进容器，并在需要的地方自动注入（依赖注入，DI）。
 *
 * 依赖注入的三种方式（推荐构造器注入）：
 *   1. 构造器注入（本类使用）：final 字段 + 构造器，Spring 创建 Bean 时自动把依赖传进来。
 *      优点：依赖一目了然、字段不可变、方便单元测试。
 *   2. 字段注入 @Autowired：写法省事，但依赖隐蔽、不利于测试，不推荐。
 *   3. Setter 注入：较少使用。
 *   Spring 4.3 之后，类只有一个构造器时可以省略 @Autowired（本类就省略了）。
 */
@Service
/**
 * 用户接口实现类
 */
public class UserServiceImpl implements UserService {

    /** 用户账号表访问（hiya_user） */
    private final UserRepository userRepository;

    /** 用户资料表访问（hiya_user_profile） */
    private final UserProfileRepository userProfileRepository;

    /** 登录日志表访问（hiya_user_login_log） */
    private final UserLoginLogRepository userLoginLogRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserProfileRepository userProfileRepository,
                           UserLoginLogRepository userLoginLogRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userLoginLogRepository = userLoginLogRepository;
    }

    // ==================== 注册 / 登录 ====================

    /** 请求注册注册 */
    @Override
    public User register(RegisterRequest request) {
        // 1、业务校验：用户名是否已被注册（isPresent 表示 Optional 里有值）
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            // 抛业务异常 → 由 GlobalExceptionHandler 统一转成 Result 返回前端
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 2、组装实体（userId 由数据库自增生成，不需要我们设置）
        User user = new User();
        user.setUserName(request.getUserName());
        // 旧库密码是明文存储（旧版登录 SQL 直接比较 password 字段），这里保持明文以便旧数据可登录
        user.setPassword(request.getPassword());
        user.setIsBanned(0);   // 新用户默认未封禁
        user.setIsDeleted(0);  // 新用户默认未删除
        // unban_time 列 NOT NULL，插入时必须给值（新用户没有封禁，用当前时间占位即可）
        user.setUnbanTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 3、save()：没有主键的对象执行 INSERT，返回的实体带上了数据库生成的主键
        return userRepository.save(user);
    }

    /** 请求某用户登录 */
    @Override
    public User login(LoginRequest request) {
        // 1. 按用户名查用户，查不到就报"用户名或密码错误"
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new BusinessException(ResultCode.LOGIN_FAILED));

        // 2. 比对密码（明文）
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        // 3. 写一条登录日志
        UserLoginLog log = new UserLoginLog();
        log.setUserId(user.getUserId());
        log.setLoginTime(LocalDateTime.now());
        userLoginLogRepository.save(log);

        return user;
    }

    // ==================== 查询用户数据 ====================

    /**    (non-Javadoc)
     * 查找某位用户的基本信息（user表）。
     * @see com.hiya.service.UserService#getById(java.lang.Long)
     */
    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }

    /**    (non-Javadoc)
     * 查询某位用户的次要信息userProfile（userProfile表）。
     * 
     * @param targetUserId 要查询的用户的userID。
     * @see com.hiya.service.UserService#getUserInfo(java.lang.Long)
     */
    @Override
    public UserInfoVO getUserInfo(Long targetUserId) {
        // 1. 查账号信息（查不到 → "用户不存在"）
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 2. 查资料信息（可能没有 → profile 为 null）
        UserProfile profile = userProfileRepository.findByUserId(targetUserId).orElse(null);

        // 3. 合并返回 {user, profile}
        return new UserInfoVO(user, profile);
    }

    // ==================== 修改自己的数据 ====================

    /**    (non-Javadoc)
     * 更新用户的信息
     * 
     * @param userId 要更改信息的用户的userId
     * @param request 更改后的信息
     * @see com.hiya.service.UserService#updateProfile(java.lang.Long, com.hiya.dto.UpdateProfileRequest)
     */
    @Override
    public UserInfoVO updateProfile(Long userId, UpdateProfileRequest request) {
        // 1. 账号必须存在（同时拿到返回用的 user 对象）
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 2. 取资料行；没有就新建一条（很多老用户可能从来没填过资料）
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile created = new UserProfile();
                    created.setUserId(userId); // 关联到当前用户
                    return created;
                });

        // 3. 只更新"传了值"的字段：null 表示这项不改
        //    （前端只想改昵称时，不用把头像、签名都带上）
        if (request.getNickName() != null) {
            profile.setNickName(request.getNickName());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getSignature() != null) {
            profile.setSignature(request.getSignature());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getBirthday() != null) {
            profile.setBirthday(request.getBirthday());
        }

        // 4. 保存：没有主键 → INSERT；有主键 → UPDATE
        UserProfile saved = userProfileRepository.save(profile);

        // 5. 顺便刷新账号表的 update_time（表示"这条用户数据动过"）
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        return new UserInfoVO(user, saved);
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // 1. 取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 2. 验证原密码（本项目没有认证，但改密码这种操作保留最小校验）
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "原密码错误");
        }

        // 3. 更新密码与更新时间
        user.setPassword(request.getNewPassword());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user); // 有主键 → UPDATE
    }
}
