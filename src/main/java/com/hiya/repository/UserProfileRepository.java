package com.hiya.repository;

import com.hiya.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户资料数据访问层。访问表user_profile。
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * 根据用户ID查资料（一个用户最多一条资料）
     * @param userId
     * @return 由Optional包装的UserProfile数据。
     */
    Optional<UserProfile> findByUserId(Long userId);
}
