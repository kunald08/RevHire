package com.revhire.service;

import com.revhire.dao.UserDao;
import com.revhire.dao.impl.UserDaoImpl;
import com.revhire.model.User;

public class AuthService {

    private UserDao userDao = new UserDaoImpl();

    public boolean register(User user) {
        if (userDao.emailExists(user.getEmail())) {
            System.out.println("❌ Email already registered");
            return false;
        }
        return userDao.register(user);
    }

    public User login(String email, String password) {
        return userDao.login(email, password);
    }
}
