package com.bloodlink.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Creates JDBC connections. Settings come from environment variables, never source code. */
public final class DatabaseConnection {
    private DatabaseConnection() {}

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? def : v;
    }

    public static Connection getConnection() throws SQLException {
        String url = env("BLOODLINK_DB_URL", "jdbc:mysql://localhost:3306/bloodlink_db");
        String user = env("BLOODLINK_DB_USER", "root");
        String password = env("BLOODLINK_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
}
