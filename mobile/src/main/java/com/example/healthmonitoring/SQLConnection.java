package com.example.healthmonitoring;
import android.content.Context;

import java.sql.*;

public class SQLConnection {

    protected Connection doInBackground(Context... contexts) {


        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://104.196.134.4/healthApp?zeroDateTimeBehavior=convertToNull";

            String userName = "root";
            String password = "Ateamhealth";
            conn = DriverManager.getConnection(url, userName, password);


            return conn;
        } catch (Exception e) {
            return null;
        }
    }
}