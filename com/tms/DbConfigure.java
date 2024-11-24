package com.tms;

import java.sql.*;
import java.util.Properties;

public class DbConfigure {

    /*public static void main(String[] args) {
        try {
            Connection connection = DbConfigure.getConnection();
            System.out.println("Connection successful: " + connection);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }*/

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/ticket_booking";
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "root");
        return DriverManager.getConnection(url, props);
    }

}
