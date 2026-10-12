package com.hiya.repository;

import com.hiya.entity.UserLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 登录日志数据访问层。访问表user_login_log。
 */
public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {
}
