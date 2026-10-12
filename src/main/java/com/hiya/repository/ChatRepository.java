package com.hiya.repository;

import com.hiya.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 聊天会话数据访问层。
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * 查找两个用户之间已存在的私聊会话（type=1）。
     */
    @Query("SELECT c FROM Chat c WHERE c.type = 1 AND c.chatId IN " +
            "(SELECT cm1.chatId FROM ChatMember cm1, ChatMember cm2 " +
            " WHERE cm1.chatId = cm2.chatId AND cm1.userId = :userA AND cm2.userId = :userB)")
    Optional<Chat> findPrivateChatBetween(@Param("userA") Long userA, @Param("userB") Long userB);
}
