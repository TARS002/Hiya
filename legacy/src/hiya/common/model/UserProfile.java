package hiya.common.model;

/**
 * 用户资料实体类，对应数据库hiya_user_profile表。
 * 存储用户的个性化展示信息，如昵称、头像、签名等。
 */
public class UserProfile {
    private Long userProfileId; // 资料ID，主键自增
    private Long userId;        // 关联的用户ID，外键指向hiya_user
    private String nickName;    // Hiya昵称
    private String avatarUrl;   // 头像URL地址
    private String signature;   // 个性签名
    private Integer gender;     // 性别，0不便透露，1男，2女
    private String birthday;    // 生日

    // 以下为getter和setter方法
    public Long getUserProfileId() { return userProfileId; }
    public void setUserProfileId(Long userProfileId) { this.userProfileId = userProfileId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
}