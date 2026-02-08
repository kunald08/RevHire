package com.revhire.dao;

import com.revhire.model.User;

public interface UserDao {
    boolean register(User user);
    User login(String email, String password);
    boolean emailExists(String email);
}
