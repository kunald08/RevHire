package com.revhire.dao;

import com.revhire.model.User;

/**
 * Data access contract for user operations.
 */
public interface UserDao {

    boolean register(User user);

    User login(String email, String password);

    User getUserById(int userId);

    boolean updateProfile(User user);

    boolean changePassword(int userId, String oldPassword, String newPassword);

    boolean resetPassword(String email, String question, String answer, String newPassword);
}
