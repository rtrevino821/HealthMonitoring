package com.example.healthmonitoring;


/**
 * Created by Steven J on 11/10/2016.
 */


import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnection {

    public static Connection doInBackground() {


        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://104.196.134.4/healthApp?user=root&password=Ateamhealth";



            String userName = "root";
            String password = "Ateamhealth";
            conn = DriverManager.getConnection(url, userName, password);


            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
