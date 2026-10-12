package hiya.common.model;

/**
 * 聊天消息实体类，对应数据库hiya_message表。
 * 存储聊天会话中的每一条消息。
 */
public class Message {
    private Long id;        // 消息ID，主键自增
    private Long senderId;  // 发送者用户ID
    private Long chatId;    // 所属聊天会话ID
    private Integer msgType;// 消息类型，1文本，2图片，3文件，4语音，5系统指令
    private String content; // 消息内容
    private String sendTime;// 发送时间
    private Integer isDeleted; // 是否被撤回，0未撤回，1已撤回

    // 以下为getter和setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Integer getMsgType() { return msgType; }
    public void setMsgType(Integer msgType) { this.msgType = msgType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSendTime() { return sendTime; }
    public void setSendTime(String sendTime) { this.sendTime = sendTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}