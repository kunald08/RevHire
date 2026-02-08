package com.revhire.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.revhire.config.DBConfig;

public class DBUtil {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    DBConfig.URL,
                    DBConfig.USER,
                    DBConfig.PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException("❌ Database Connection Failed", e);
        }
    }
}
