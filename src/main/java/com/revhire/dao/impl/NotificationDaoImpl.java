package com.revhire.dao.impl;

import com.revhire.dao.NotificationDao;
import com.revhire.model.Notification;
import com.revhire.util.DBUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDaoImpl implements NotificationDao {

    private static final Logger logger = LogManager.getLogger(NotificationDaoImpl.class);

    @Override
    public boolean createNotification(int userId, String message) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, message);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Create notification failed userId={}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public List<Notification> getUserNotifications(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(userId);
                n.setMessage(rs.getString("message"));
                n.setRead(rs.getBoolean("is_read"));
                Timestamp ts = rs.getTimestamp("created_at");
                n.setCreatedAt(ts != null ? ts.toString().substring(0, 16) : null);
                list.add(n);
            }

        } catch (SQLException e) {
            logger.error("Get notifications failed userId={}: {}", userId, e.getMessage());
        }
        return list;
    }

    @Override
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            logger.error("Unread count failed userId={}: {}", userId, e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Mark-as-read failed notifId={}: {}", notificationId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() >= 0;

        } catch (SQLException e) {
            logger.error("Mark-all-as-read failed userId={}: {}", userId, e.getMessage());
            return false;
        }
    }
}
