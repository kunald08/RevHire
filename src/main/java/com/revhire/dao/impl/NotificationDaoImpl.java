package com.revhire.dao.impl;

import com.revhire.dao.NotificationDao;
import com.revhire.model.Notification;
import com.revhire.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDaoImpl implements NotificationDao {

    @Override
    public boolean createNotification(int userId, String message) {

        String sql = "INSERT INTO notifications (user_id, message) VALUES (?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, message);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Notification> getUserNotifications(int userId) {

        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id=?";

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
                list.add(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
