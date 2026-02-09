package com.revhire.dao;

import com.revhire.model.Notification;
import java.util.List;

/**
 * Data access contract for notification operations.
 */
public interface NotificationDao {

    boolean createNotification(int userId, String message);

    List<Notification> getUserNotifications(int userId);

    int getUnreadCount(int userId);

    boolean markAsRead(int notificationId);

    boolean markAllAsRead(int userId);
}
