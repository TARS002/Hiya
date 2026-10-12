package hiya.server.db;

import hiya.common.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息数据访问对象，封装对hiya_message表的插入和查询操作。
 */
public class MessageDAO {

    /**
     * 插入一条新的聊天消息。
     * @param senderId 发送者ID
     * @param chatId 聊天室ID
     * @param msgType 消息类型，1文本
     * @param content 消息内容
     * @return 插入成功返回完整的Message对象（含自增ID和发送时间），失败返回null
     */
    public Message insert(Long senderId, Long chatId, int msgType, String content) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            // 使用RETURN_GENERATED_KEYS获取自增的消息ID
            String sql = "INSERT INTO hiya_message (sender_id, chat_id, msg_type, content, send_time, is_deleted) VALUES (?, ?, ?, ?, NOW(), 0)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, senderId);   // 设置发送者
            ps.setLong(2, chatId);     // 设置聊天室
            ps.setInt(3, msgType);     // 设置消息类型
            ps.setString(4, content);  // 设置消息内容
            int rows = ps.executeUpdate();
            if (rows > 0) {
                // 获取自增的消息ID
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getLong(1));         // 读取自增ID
                    msg.setSenderId(senderId);
                    msg.setChatId(chatId);
                    msg.setMsgType(msgType);
                    msg.setContent(content);
                    msg.setSendTime(String.valueOf(Timestamp.valueOf(java.time.LocalDateTime.now())));
                    msg.setIsDeleted(0);
                    return msg;
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
     * 查询指定聊天室的所有未撤回消息，按发送时间升序排列。
     * @param chatId 聊天室ID
     * @return 该聊天室的聊天记录列表
     */
    public List<Message> findByChatId(Long chatId) {
        List<Message> messages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT * FROM hiya_message WHERE chat_id = ? AND is_deleted = 0 ORDER BY send_time ASC";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, chatId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getLong("id"));               // 读取消息ID
                msg.setSenderId(rs.getLong("sender_id"));   // 读取发送者ID
                msg.setChatId(rs.getLong("chat_id"));       // 读取聊天室ID
                msg.setMsgType(rs.getInt("msg_type"));      // 读取消息类型
                msg.setContent(rs.getString("content"));    // 读取消息内容
                msg.setSendTime(rs.getString("send_time")); // 读取发送时间
                msg.setIsDeleted(rs.getInt("is_deleted"));  // 读取撤回状态
                messages.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return messages;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}