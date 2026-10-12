package hiya.common.model;

/**
 * 用户主表实体类，对应数据库hiya_user表。
 * 存储用户的基本账号信息。
 */
public class User {
    private Long userId;        // 用户ID，主键自增
    private String userName;    // 用户自定义名称，登录时使用
    private String password;    // 密码
    private Integer isBanned;   // 是否被封禁，0未封禁，1已封禁
    private String unbanTime;   // 解封时间
    private String createTime;  // 账号创建时间
    private String updateTime;  // 数据最后更新时间
    private Integer isDeleted;  // 是否被删除，0未删除，1已删除

    // 以下为getter和setter方法
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getIsBanned() { return isBanned; }
    public void setIsBanned(Integer isBanned) { this.isBanned = isBanned; }
    public String getUnbanTime() { return unbanTime; }
    public void setUnbanTime(String unbanTime) { this.unbanTime = unbanTime; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}