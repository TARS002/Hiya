package com.hiya.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 好友关系实体：对应数据库 hiya_friend 表。
 * 记录两个用户之间的好友关系。
 *
 * 旧系统的关键设计：
 *   1. 【双向两行】加好友时插入 (A→B) 和 (B→A) 两条记录，
 *      查询好友列表 = 查 user_id = 我 的未删除记录；
 *   2. 【逻辑删除】删好友把 is_deleted 置 1（双向），不物理删除；
 *   3. status：0正常好友，1拉黑（真实数据里 1↔2 就是拉黑状态）。
 *
 * 数据库层面 (user_id, friend_id) 有 UNIQUE 约束（ralationship）。
 */
@Table(name = "hiya_friend")
@Entity
public class Friend {

    /**
     * 好友关系ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 当前用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 好友的用户ID
     */
    @Column(name = "friend_id")
    private Long friendId;

    /**
     * 好友状态：0正常好友，1拉黑
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 好友备注名
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 好友分组标签
     */
    @Column(name = "group_tag")
    private String groupTag;

    /**
     * 关系创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间（数据库自动维护）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否已删除：0未删除，1已删除（逻辑删除）
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getFriendId() { return friendId; }
    public void setFriendId(Long friendId) { this.friendId = friendId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getGroupTag() { return groupTag; }
    public void setGroupTag(String groupTag) { this.groupTag = groupTag; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
