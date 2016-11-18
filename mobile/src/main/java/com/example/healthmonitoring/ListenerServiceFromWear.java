package com.example.healthmonitoring;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;


/**
 * Gets message sent from phone and insets to to database
 */

public class ListenerServiceFromWear   extends WearableListenerService {

    private final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";
    private final String AFTER_INSERT= "com.example.healthmonitoring.AFTER_INSERT";
    private String TAG = "/ListenerService";
    private String userID;
    private int userHeartRate;
    private int binary;
    private java.sql.Timestamp timeStamp;
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
              /*
         * Receive the message from wear
         */
        if (messageEvent.getPath().equals(HELLO_WORLD_WEAR_PATH)) {
            BackgroundTask task = new BackgroundTask(this);
            task.execute();
        }
    }

    //Async task makes connection
    class BackgroundTask extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //String user = "Stoney";
            //String password = "FL";

            //get values for vars to insert
            getTimeStamp();
            getSharedPreference(context);
            checkThreshold();

            try {
                Connection conn = SQLConnection.doInBackground();
                String SQL = "INSERT INTO healthApp.HeartRateData" +
                        "(`Id`,`HeartRate`,`TimeStamp`,`Flag`)" +
                        "VALUES (?, ?, ?, ?);";
                PreparedStatement prepare = conn.prepareStatement(SQL);
                prepare.setString(1,userID);
                prepare.setInt(2,userHeartRate);
                prepare.setString(3, timeStamp.toString());
                prepare.setInt(4,binary);

                prepare.executeUpdate();
                return true;

            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }

            //Login Failed
            Log.d(TAG, "asynctask ouside false");
            return true;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                Log.d(TAG, "Good Job");
                //retrieveMessage("button");
            }
            else
                Log.d(TAG, "Good Effort");
        }

    }

    private void getTimeStamp()
    {
        java.util.Date utilDate = new java.util.Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        cal.set(Calendar.MILLISECOND, 0);
        timeStamp = new java.sql.Timestamp(utilDate.getTime());
        Log.d(TAG, timeStamp.toString());
    }

    //gets ID and HeartRate
    private void getSharedPreference(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userID = prefs.getString("ID", null);
        Log.d(TAG,userID);
        String heartData = (prefs.getString("HeartRate", null));
        userHeartRate = Integer.parseInt(heartData);
        Log.d(TAG, heartData);
    }
    //sets flag
    private void checkThreshold()
    {
        //0
        binary = 0;

        //1
    }
    private void retrieveMessage(String message) {
        Intent intent = new Intent();
        intent.setAction(AFTER_INSERT);
        intent.putExtra("visible", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}



