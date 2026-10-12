package hiya.server.db;

import hiya.common.model.ChatMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天室成员数据访问对象，封装对hiya_chat_member表的增查操作。
 */
public class ChatMemberDAO {

    /**
     * 向聊天室中添加一个成员。
     * @param chatId 聊天室ID
     * @param userId 用户ID
     * @param role 角色，0群员，1绿标，2群主
     * @return 插入成功返回true，失败（如已存在）返回false
     */
    public boolean insertMember(Long chatId, Long userId, int role) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBManager.getConnection();
            String sql = "INSERT INTO hiya_chat_member (chat_id, user_id, role, join_time, update_time, is_muted, mute_until) VALUES (?, ?, ?, NOW(), NOW(), 0, NOW())";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, chatId);  // 设置聊天室ID
            ps.setLong(2, userId);  // 设置用户ID
            ps.setInt(3, role);     // 设置角色
            // 执行插入
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // 如果是因为UNIQUE约束冲突（用户已在聊天室中），不算严重错误
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    /**
     * 查询指定用户所在的所有聊天室的chat_id列表。
     * @param userId 用户ID
     * @return 该用户加入的所有聊天室ID列表
     */
    public List<Long> findChatIdsByUserId(Long userId) {
        List<Long> chatIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT chat_id FROM hiya_chat_member WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            // 遍历结果集，将每个chat_id加入列表
            while (rs.next()) {
                chatIds.add(rs.getLong("chat_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return chatIds;
    }

    /**
     * 查询指定聊天室中的所有成员user_id列表。
     * @param chatId 聊天室ID
     * @return 该聊天室中所有成员的用户ID列表
     */
    public List<Long> findUserIdsByChatId(Long chatId) {
        List<Long> userIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT user_id FROM hiya_chat_member WHERE chat_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, chatId);
            rs = ps.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getLong("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return userIds;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}