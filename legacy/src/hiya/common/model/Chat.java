package hiya.common.model;

/**
 * 聊天会话实体类，对应数据库hiya_chat表。
 * 每条记录代表一个聊天会话（私聊或群聊）。
 */
public class Chat {
    private Long chatId;      // 会话ID，主键自增
    private Integer type;     // 会话类型，1表示私聊，2表示群聊
    private String chatTitle; // 会话标题，群聊时为群名，私聊时可为空
    private String createTime;// 会话创建时间
    private String updateTime;// 会话最后更新时间

    // 以下为getter和setter方法
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getChatTitle() { return chatTitle; }
    public void setChatTitle(String chatTitle) { this.chatTitle = chatTitle; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}