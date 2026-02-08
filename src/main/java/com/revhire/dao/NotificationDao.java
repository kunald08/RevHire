package com.revhire.dao;

import com.revhire.model.Notification;
import java.util.List;

public interface NotificationDao {

    boolean createNotification(int userId, String message);

    List<Notification> getUserNotifications(int userId);
}
