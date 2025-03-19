package com.example.YouTubeDL;

import java.sql.*;

import org.springframework.beans.factory.annotation.Value;


/**
 * Use singleton design pattern
 */
public class PostgreSQL {

    @Value("${database.url}")
    private String url;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    private static PostgreSQL instance = null;
    private Connection conn;

    /**
     * 
     * @throws SQLException
     */
    private PostgreSQL() throws SQLException {
        try ( Connection conn = DriverManager.getConnection(url, username, password) ) {
            this.conn = conn;
        }
    }

    public static PostgreSQL getInstance() throws SQLException {
        if (instance == null) {
            instance = new PostgreSQL();
        }
        return instance;
    }

    public static void addVideo() {

    }
}
