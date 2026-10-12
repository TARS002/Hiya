package hiya.common.model;

/**
 * 好友关系实体类，对应数据库hiya_friend表。
 * 记录两个用户之间的好友关系。
 */
public class Friend {
    private Long id;        // 好友关系ID，主键自增
    private Long userId;    // 当前用户ID
    private Long friendId;  // 好友的用户ID
    private Integer status; // 好友状态，0正常好友，1拉黑
    private String remark;  // 好友备注名
    private String groupTag;// 好友分组标签
    private String createTime; // 关系创建时间
    private Integer isDeleted; // 是否已删除，0未删除，1已删除

    // 以下为getter和setter方法
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
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}