package com.hiya.repository;

import com.hiya.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 好友关系数据访问层。对应表friend。
 */
public interface FriendRepository extends JpaRepository<Friend, Long> {


    /**
     * 查询某用户的所有未删除好友关系。
     * 
     * @param userId 查询对象userId
     * @param isDeleted 要查询删除的或者没删的，1表示已经删除，0表示没删
     * @return
     */
    List<Friend> findByUserIdAndIsDeleted(Long userId, Integer isDeleted);

    /**
     * 查询一对"用户→好友"的有效关系。
     * 
     * @param userId 用户的userId
     * @param friendId 好友的userId
     * @param isDeleted 是否删除，1删除，0没删除
     * @return 由Optional包装的Friend型数据
     */
    Optional<Friend> findByUserIdAndFriendIdAndIsDeleted(Long userId, Long friendId, Integer isDeleted);

    /**
     * 判断两个用户之间是否已是好友（存在有效记录）
     * 
     * @param userId 该用户的userId
     * @param friendId 与另一个用户userId
     * @param isDeleted 是否还是好友
     * @return boolean型数据
     */
    boolean existsByUserIdAndFriendIdAndIsDeleted(Long userId, Long friendId, Integer isDeleted);
}
