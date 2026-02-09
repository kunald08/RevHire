package com.revhire.util;

import com.revhire.config.DBConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for obtaining JDBC connections.
 */
public class DBUtil {

    private static final Logger logger = LogManager.getLogger(DBUtil.class);

    private DBUtil() {} // prevent instantiation

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DBConfig.URL, DBConfig.USER, DBConfig.PASS);
        } catch (SQLException e) {
            logger.fatal("Database connection failed: {}", e.getMessage());
            throw new RuntimeException("Database Connection Failed", e);
        }
    }
}
