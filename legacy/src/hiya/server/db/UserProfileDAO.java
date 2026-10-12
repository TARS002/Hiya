package hiya.server.db;

import hiya.common.model.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户资料数据访问对象，封装对hiya_user_profile表的查询操作。
 */
public class UserProfileDAO {

    /**
     * 根据用户ID查询用户资料。
     * @param userId 用户ID
     * @return 查询到的UserProfile对象，未找到返回null
     */
    public UserProfile findByUserId(Long userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT * FROM hiya_user_profile WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserProfile profile = new UserProfile();
                profile.setUserProfileId(rs.getLong("user_profile_id")); // 读取资料ID
                profile.setUserId(rs.getLong("user_id"));                 // 读取用户ID
                profile.setNickName(rs.getString("nick_name"));           // 读取昵称
                profile.setAvatarUrl(rs.getString("avatar_url"));         // 读取头像URL
                profile.setSignature(rs.getString("signature"));           // 读取个性签名
                profile.setGender(rs.getObject("gender") != null ? rs.getInt("gender") : null); // 读取性别
                profile.setBirthday(rs.getString("birthday"));            // 读取生日
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
