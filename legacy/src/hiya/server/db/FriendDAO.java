package hiya.server.db;

import hiya.common.model.Friend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 好友关系数据访问对象，封装对hiya_friend表的增改查操作。
 */
public class FriendDAO {

    /**
     * 查询指定用户的所有未删除好友关系列表。
     * @param userId 用户ID
     * @return 该用户的好友关系列表
     */
    public List<Friend> findFriendsByUserId(Long userId) {
        List<Friend> friends = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT * FROM hiya_friend WHERE user_id = ? AND is_deleted = 0";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Friend friend = new Friend();
                friend.setId(rs.getLong("id"));                  // 读取关系ID
                friend.setUserId(rs.getLong("user_id"));         // 读取用户ID
                friend.setFriendId(rs.getLong("friend_id"));     // 读取好友ID
                friend.setStatus(rs.getInt("status"));           // 读取好友状态
                friend.setRemark(rs.getString("remark"));        // 读取备注
                friend.setGroupTag(rs.getString("group_tag"));   // 读取分组标签
                friend.setCreateTime(rs.getString("create_time")); // 读取创建时间
                friend.setIsDeleted(rs.getInt("is_deleted"));     // 读取删除状态
                friends.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return friends;
    }

    /**
     * 插入一条好友关系（双向插入，甲加乙、乙加甲）。
     * @param userId 用户A的ID
     * @param friendId 用户B的ID
     * @return 插入成功返回true，失败返回false
     */
    public boolean insertFriend(Long userId, Long friendId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBManager.getConnection();
            // 关闭自动提交，以便在一个事务中插入两条记录
            conn.setAutoCommit(false);
            String sql = "INSERT INTO hiya_friend (user_id, friend_id, status, create_time, update_time, is_deleted) VALUES (?, ?, 0, NOW(), NOW(), 0)";
            // 插入 A -> B 的好友关系
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.executeUpdate();
            // 插入 B -> A 的好友关系
            ps.setLong(1, friendId);
            ps.setLong(2, userId);
            ps.executeUpdate();
            // 两条都插入成功，提交事务
            conn.commit();
            return true;
        } catch (SQLException e) {
            // 发生异常，回滚事务
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            closeResources(null, ps, conn);
        }
    }

    /**
     * 逻辑删除好友关系（将is_deleted设为1），双向删除。
     * @param userId 用户A的ID
     * @param friendId 用户B的ID
     * @return 删除成功返回true
     */
    public boolean deleteFriend(Long userId, Long friendId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE hiya_friend SET is_deleted = 1, update_time = NOW() WHERE user_id = ? AND friend_id = ? AND is_deleted = 0";
            // 删除 A -> B 的关系
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.executeUpdate();
            // 删除 B -> A 的关系
            ps.setLong(1, friendId);
            ps.setLong(2, userId);
            ps.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            closeResources(null, ps, conn);
        }
    }

    /**
     * 检查两个用户之间是否已存在有效的好友关系。
     * @param userId 用户A的ID
     * @param friendId 用户B的ID
     * @return 存在返回true，不存在返回false
     */
    public boolean isFriend(Long userId, Long friendId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT id FROM hiya_friend WHERE user_id = ? AND friend_id = ? AND is_deleted = 0 LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            rs = ps.executeQuery();
            return rs.next(); // 有记录说明已是好友
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}