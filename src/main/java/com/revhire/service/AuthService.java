package com.revhire.service;

import com.revhire.dao.UserDao;
import com.revhire.dao.impl.UserDaoImpl;
import com.revhire.model.User;

/**
 * Handles user authentication — register, login, password management, profile.
 */
public class AuthService {

    private final UserDao userDao = new UserDaoImpl();

    public boolean register(User user) {
        return userDao.register(user);
    }

    public User login(String email, String password) {
        return userDao.login(email, password);
    }

    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public boolean updateProfile(User user) {
        return userDao.updateProfile(user);
    }

    public boolean changePassword(int userId, String oldPwd, String newPwd) {
        return userDao.changePassword(userId, oldPwd, newPwd);
    }

    public boolean forgotPassword(String email, String question, String answer, String newPassword) {
        return userDao.resetPassword(email, question, answer, newPassword);
    }
}
