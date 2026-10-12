package com.hiya.repository;

import com.hiya.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 聊天消息数据访问层。访问表message
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 查询指定会话的所有未撤回消息，按发送时间升序。
     * 
     * @param chatId 
     * @param isDeleted
     * @return List列表，包括Message型数据
     */
    List<Message> findByChatIdAndIsDeletedOrderBySendTimeAsc(Long chatId, Integer isDeleted);
}
