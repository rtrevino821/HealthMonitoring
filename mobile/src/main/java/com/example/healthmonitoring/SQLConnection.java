package com.example.healthmonitoring;

import java.sql.*;

public class SQLConnection {

    public static Connection doInBackground() {


        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://104.196.134.4/healthApp?account=root&password=Ateamhealth";

            String userName = "root";
            String password = "Ateamhealth";
            conn = DriverManager.getConnection(url, userName, password);


            return conn;
        } catch (Exception e) {
            return null;
        }
    }
}
