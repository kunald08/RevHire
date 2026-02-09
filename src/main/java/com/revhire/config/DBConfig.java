package com.revhire.config;

/**
 * Centralized database configuration constants.
 * Update these values to match your MySQL setup.
 */
public class DBConfig {

    public static final String URL  = "jdbc:mysql://localhost:3306/revhire_db?useSSL=false&serverTimezone=UTC";
    public static final String USER = "kunal";
    public static final String PASS = "password123";

    private DBConfig() {} // prevent instantiation
}
