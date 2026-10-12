package hiya.server.db;

import hiya.common.model.Chat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天会话数据访问对象，封装对hiya_chat表的增查操作。
 */
public class ChatDAO {

    /**
     * 根据聊天室ID查询会话信息。
     * @param chatId 聊天室ID
     * @return 查询到的Chat对象，未找到返回null
     */
    public Chat findById(Long chatId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT * FROM hiya_chat WHERE chat_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, chatId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractChat(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * 插入一条新的聊天会话记录，并返回自增生成的chat_id。
     * 使用RETURN_GENERATED_KEYS获取自增主键值，解决创建私聊后不知道chat_id的问题。
     * @param type 会话类型，1私聊，2群聊
     * @param chatTitle 会话标题，私聊时可为null
     * @return 插入成功返回自增的chat_id，失败返回null
     */
    public Long insert(Integer type, String chatTitle) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            // 准备INSERT语句，指定RETURN_GENERATED_KEYS以获取自增主键
            String sql = "INSERT INTO hiya_chat (type, chat_title, create_time, update_time) VALUES (?, ?, NOW(), NOW())";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, type);          // 设置会话类型
            ps.setString(2, chatTitle);  // 设置会话标题
            // 执行插入
            int rows = ps.executeUpdate();
            if (rows > 0) {
                // 获取自增生成的主键
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1); // 返回自增的chat_id
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * 检查两个用户之间是否已存在私聊会话。
     * 通过hiya_chat_member表关联查询，找到type=1且同时包含两个用户的会话。
     * @param userA 用户A的ID
     * @param userB 用户B的ID
     * @return 如果存在私聊返回该Chat对象，不存在返回null
     */
    public Chat findPrivateChatBetween(Long userA, Long userB) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            // 通过子查询找到同时包含userA和userB的chat_id，且该会话type=1（私聊）
            String sql = "SELECT c.* FROM hiya_chat c WHERE c.type = 1 AND c.chat_id IN " +
                    "(SELECT cm1.chat_id FROM hiya_chat_member cm1 JOIN hiya_chat_member cm2 " +
                    "ON cm1.chat_id = cm2.chat_id WHERE cm1.user_id = ? AND cm2.user_id = ?)";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userA);
            ps.setLong(2, userB);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractChat(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * 从ResultSet中提取Chat对象字段。
     */
    private Chat extractChat(ResultSet rs) throws SQLException {
        Chat chat = new Chat();
        chat.setChatId(rs.getLong("chat_id"));       // 读取会话ID
        chat.setType(rs.getInt("type"));              // 读取会话类型
        chat.setChatTitle(rs.getString("chat_title")); // 读取会话标题
        chat.setCreateTime(rs.getString("create_time")); // 读取创建时间
        chat.setUpdateTime(rs.getString("update_time")); // 读取更新时间
        return chat;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}