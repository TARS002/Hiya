package hiya.server.db;

import hiya.common.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户数据访问对象，封装对hiya_user表的增删改查操作。
 */
public class UserDAO {

    /**
     * 根据用户名和密码查询用户，用于登录验证。
     * @param userName 用户名
     * @param password 密码
     * @return 查询到的User对象，未找到返回null
     */
    public User findByUserNameAndPassword(String userName, String password) {
        Connection conn = null;       // 数据库连接
        PreparedStatement ps = null;  // 预编译SQL语句
        ResultSet rs = null;          // 查询结果集
        try {
            // 获取数据库连接
            conn = DBManager.getConnection();
            // 准备SQL查询语句，查询未删除且未封禁的用户
            String sql = "SELECT * FROM hiya_user WHERE user_name = ? AND password = ? AND is_deleted = 0 AND is_banned = 0";
            ps = conn.prepareStatement(sql);
            // 设置查询参数
            ps.setString(1, userName);
            ps.setString(2, password);
            // 执行查询
            rs = ps.executeQuery();
            // 如果查到结果，封装为User对象
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            // SQL异常，打印信息
            e.printStackTrace();
        } finally {
            // 依次关闭结果集、语句、连接
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * 根据用户ID查询用户信息。
     * @param userId 用户ID
     * @return 查询到的User对象，未找到返回null
     */
    public User findById(Long userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT * FROM hiya_user WHERE user_id = ? AND is_deleted = 0";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * 从ResultSet中提取User对象字段。
     * @param rs 查询结果集
     * @return 封装好的User对象
     * @throws SQLException 如果读取字段失败
     */
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));         // 读取用户ID
        user.setUserName(rs.getString("user_name"));    // 读取用户名
        user.setPassword(rs.getString("password"));     // 读取密码
        user.setIsBanned(rs.getInt("is_banned"));       // 读取封禁状态
        user.setUnbanTime(rs.getString("unban_time"));  // 读取解封时间
        user.setCreateTime(rs.getString("create_time"));// 读取创建时间
        user.setUpdateTime(rs.getString("update_time"));// 读取更新时间
        user.setIsDeleted(rs.getInt("is_deleted"));     // 读取删除状态
        return user;
    }

    /**
     * 记录用户登录日志，插入hiya_user_login_log表。
     * @param userId 登录的用户ID
     */
    public void insertLoginLog(Long userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBManager.getConnection();
            String sql = "INSERT INTO hiya_user_login_log (user_id) VALUES (?)";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            // 执行插入
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }
    }

    /**
     * 关闭数据库资源。按顺序关闭ResultSet、PreparedStatement、Connection。
     * @param rs 结果集
     * @param ps 预编译语句
     * @param conn 数据库连接
     */
    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}