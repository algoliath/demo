package com.example.demo.util.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.example.demo.util.database.ConnectionConst.*;

public class DBConnectionUtils {

    public static Connection getConnection(){
         try{
             Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             return connection;
         } catch (SQLException e) {
             throw new IllegalStateException(e);
         }
    }
}
