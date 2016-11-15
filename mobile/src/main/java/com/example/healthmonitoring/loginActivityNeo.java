package com.example.healthmonitoring;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginActivityNeo  extends AppCompatActivity   {
    private Connection conn;
    final static String sqlUser = "SELECT ID,Username,Password FROM healthApp.Logins WHERE Username = ?;";
    String TAG = "Neo";
    String ID;
    public static final String MY_PREFS_NAME = "neoPref";
    @Override
    protected void onCreate   (Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_neo);
        Button button =  (Button) findViewById(R.id.neoButton);
        BackgroundTask task = new BackgroundTask(this);
        task.execute();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent mainIntent = new Intent(loginActivityNeo.this, MainActivity.class);
                    startActivity(mainIntent);

            }
        });

    }
    private class BackgroundTask extends AsyncTask<String,Void,String>
    {

        private Context context;
        public BackgroundTask(Context context)
        {
            this.context=context;
        }
        @Override
        protected String doInBackground(String... params)
        {
            String user = "Stoney";
            String password ="FL";

            //String sql = "Select * From healthApp.Logins";
            try {
                conn = SQLConnection.doInBackground();
                PreparedStatement prepare = conn.prepareStatement(sqlUser);
                prepare.setString(1, user);
                ResultSet rs = prepare.executeQuery();
//                SharedPreferences prefs =
//                        context.getSharedPreferences(MY_PREFS_NAME,
//                                Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();

                while(rs.next())
                {
                    ID = String.valueOf(rs.getInt("ID"));
                    Log.d(TAG, String.valueOf(rs.getInt("ID")));
                    Log.d(TAG,rs.getString("Username"));
                    Log.d(TAG,rs.getString("Password"));

                }
                editor.putString("name", "Stoney");
                editor.putString("ID", ID);
                editor.commit();

            }
            catch(SQLException e)
            {
                Log.d(TAG, e.getMessage());
            }
            return sqlUser;
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.d("neoPost", "Preference received in background: " + result);        }
    }



}
