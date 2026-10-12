package com.hiya.repository;

import com.hiya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User 的数据访问层。访问表"user"。
 *
 * Spring Data JPA 的核心玩法：接口继承 JpaRepository<实体类型, 主键类型> 后，
 * 框架会自动：
 *   1. 提供现成的 CRUD 方法：save() / findById() / findAll() / deleteById() 等；
 *   2. 根据方法名自动生成 SQL（叫"方法名派生查询"）：
 *      - findByUserName(String userName)
 *        → 自动翻译成 SELECT * FROM hiya_user WHERE user_name = ?
 *      - 命名规则：findBy + 实体属性名（首字母大写），多个条件用 And / Or 连接
 *   3. 运行时通过动态代理生成实现类并注册为 Bean —— 所以这里只需要写接口，
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查用户（用户名唯一，所以结果最多一条）
     */
    Optional<User> findByUserName(String userName);
}
