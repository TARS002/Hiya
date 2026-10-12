package hiya.common.model;

/**
 * 聊天室成员实体类，对应数据库hiya_chat_member表。
 * 记录用户与聊天会话的归属关系及角色信息。
 */
public class ChatMember {
    private Long id;         // 记录ID，主键自增
    private Long chatId;     // 所属聊天会话ID
    private Long userId;     // 成员用户ID
    private Integer role;    // 角色，0群员，1绿标，2群主
    private String joinTime; // 加入时间
    private Integer isMuted; // 是否被禁言，0否，1是

    // 以下为getter和setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public String getJoinTime() { return joinTime; }
    public void setJoinTime(String joinTime) { this.joinTime = joinTime; }
    public Integer getIsMuted() { return isMuted; }
    public void setIsMuted(Integer isMuted) { this.isMuted = isMuted; }
}