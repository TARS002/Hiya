package com.hiya.repository;

import com.hiya.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 会话成员数据访问层。
 * 对chat_member表操作
 */
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    /**
     * 查询指定用户加入的所有会话（返回完整成员记录，含角色、加入时间等）。
     * 也就是查询哪些会话中有该用户。
     * 派生查询：findBy + 属性名 userId。
     * 
     * @param userId 要查询的用户的userId。
     */
    List<ChatMember> findByUserId(Long userId);

    /**
     * 查询指定会话的所有成员记录。
     * 
     * @param chatId 要查询的会话的chatId
     */
    List<ChatMember> findByChatId(Long chatId);

    /** 
     * 判断某用户是否已是某会话成员。
     * 
     * @param chatId 要指定的chatId
     * @param userId 要查询的userId
     * @return 返回boolean型数据
     */
    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    /**
     * 查某用户在指定会话中的成员记录。判断这个人是不是在该群里。
     * 
     * @param chatId 我要查这个人
     * @param userId 在不在这个群里
     * @return 因为可能数据会查不到所以选择返回的是用Optional包装的对象。Optional是一个盒子里面可能装<T>也可能不装，这样调用该函数的就不会收到null，就会更整洁。
     */
    Optional<ChatMember> findByChatIdAndUserId(Long chatId, Long userId);

    /**
     * 查询指定用户所在的所有会话的ID。
     * 
     * @param userId 要查这个用户所在的会话列表
     * @return 返回包含chatId的List
     */
    @Query("SELECT cm.chatId FROM ChatMember AS cm WHERE cm.userId = :userId")
    List<Long> findChatIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询指定会话的所有成员用户ID。
     * 
     * @param chatId 会话的ID
     * @return 返回一个List，包含所有查到的chatId
     */
    @Query("SELECT cm.userId FROM ChatMember cm WHERE cm.chatId = :chatId")
    List<Long> findUserIdsByChatId(@Param("chatId") Long chatId);
}
