package com.example.healthmonitoring;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.skyfishjy.library.RippleBackground;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import static com.example.healthmonitoring.MyService.ACTION_TEXT_CHANGED;
import static com.example.healthmonitoring.R.id.tv_Heart_Rate;
import static java.lang.Integer.parseInt;

public class CheckPulseActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {
    private GoogleApiClient mGoogleApiClient;
    Button getHR;
    Button stopHR;
    String patientIdValue = "09876";
    private TextView bpm;
    private String TAG = "CheckPulseActivity";
    private TextView tvHeartRate;
    private TeleportClient mTeleportClient;
    //private RippleBackground rippleBackground;
    int count = 0;

    private String userID;
    private String userHeartRate="00";
    private int binary;
    private java.sql.Timestamp timeStamp;
    private BackgroundTask task;
    private Context context;


    RippleBackground rippleBackground = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.check_my_pulse);
        setContentView(R.layout.activity_check_pulse);
        context=this;

        //initialize text views
        tvHeartRate = (TextView) findViewById(tv_Heart_Rate);
        stopHR = (Button) findViewById(R.id.btn_check_my_pulse_stop);
        getHR = (Button) findViewById(R.id.btn_check_my_pulse);
        bpm = (TextView) findViewById(R.id.tv_bpm);
        //init teleport  API
        mTeleportClient = new TeleportClient(this);


        //Ripple effect Background
        rippleBackground=(RippleBackground)findViewById(R.id.content);

        // Custom animation on image
        ImageView myView = (ImageView) findViewById(R.id.img_pulse);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(myView, "alpha",  1f, .2f);
        fadeOut.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(myView, "alpha", .2f, 1f);
        fadeIn.setDuration(500);
        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn).after(fadeOut);

        tvHeartRate = (TextView) findViewById(tv_Heart_Rate);
//        stopHR = (Button) findViewById(R.patientIdValue.btn_check_my_pulse_stop);
//        stopHR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopHR.setVisibility(View.GONE);
//                getHR.setVisibility(View.VISIBLE);
//                rippleBackground.stopRippleAnimation();
//            }
//        });
        getHR = (Button) findViewById(R.id.btn_check_my_pulse);
        getHR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHR.setVisibility(View.GONE);
//                stopHR.setVisibility(View.VISIBLE);
                rippleBackground.startRippleAnimation();
           //     startMeasure();
                initTimer();

            }
        });

        mAnimationSet.start();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ACTION_TEXT_CHANGED));
        //mTeleportClient.setOnSyncDataItemTask(new ShowToastOnSyncDataItemTask());

        getPatientId();
    }

    private void initTimer() {
        startMeasure();

        new CountDownTimer(17000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "seconds remaining: " + millisUntilFinished / 1000);
                Log.d("final HR",(String)tvHeartRate.getText());

            }

            public void onFinish() {
                // mTextField.setText("Done");
                Log.d("tag HR", String.valueOf(tvHeartRate.getText()));
                //userHeartRate = (String) tvHeartRate.getText();
                rippleBackground.stopRippleAnimation();
                task = new BackgroundTask(context);
                task.execute();
                userHeartRate = (String) tvHeartRate.getText();
            }
        }.start();

    }

    public String getPatientId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("ID", "");
        if (!name.equalsIgnoreCase("")) {
            return name;
        } else {
            return null;
        }
    }

    public String getPatientEmergencyContact() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String emergencyContact = preferences.getString("EmergencyContact", "");
        if (!emergencyContact.equalsIgnoreCase("")) {
            return emergencyContact;
        } else {
            return null;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    @Override //ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Google API Client was connected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override //ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //if (!mResolvingError) {
        mGoogleApiClient.connect();
        mTeleportClient.connect();
        //}
    }

    /**
     *  Receives heartrate my MyService
     *  saves the heartrate in a sharedpreference
     */

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("visible") != null)
            {
                    Log.d(TAG,"Button apppear");
                   getHR.setVisibility(View.VISIBLE);
                   rippleBackground.stopRippleAnimation();
            }

            String content = intent.getStringExtra("content");
            tvHeartRate.setText(content);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Log.d("shared TAG",content);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("HeartRate",content);
            editor.commit();
        }
    };

    //Sends a message to wearable
    private void startMeasure() {
        mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
        Log.d(TAG, "StartPulse");

        mTeleportClient.sendMessage("startActivity", null);
            Log.d(TAG, "StartPulse");
    }


    public class ShowToastFromOnGetMessageTask extends TeleportClient.OnGetMessageTask {

        @Override
        protected void onPostExecute(String path) {

            mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
            //stopMeasure();
        }
    }

    private void stopMeasure() {
        getHR.setVisibility(View.VISIBLE);
        rippleBackground.stopRippleAnimation();
    }


    //Async task makes connection
    class BackgroundTask extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            //get values for vars to insert
            getTimeStamp();
           // getSharedPreference(context);
            userID=getPatientId();

            checkThreshold();
            if (binary==1) {
                sendSMS("2396826170","Patient " + getPatientId() +" heart rate is " + userHeartRate + " Bpm " );
                if(getPatientEmergencyContact() != null)
                    sendSMS(getPatientEmergencyContact(),"Patient " + getPatientId() +" heart rate is " + userHeartRate + " Bpm " );
            }

            try {
                Connection conn = SQLConnection.doInBackground();
                String SQL = "INSERT INTO healthApp.HeartRateData" +
                        "(`Id`,`HeartRate`,`TimeStamp`,`Flag`)" +
                        "VALUES (?, ?, ?, ?);";
                Log.d("tag userId", userID);
                PreparedStatement prepare = conn.prepareStatement(SQL);
                prepare.setString(1, userID);
                prepare.setString(2, userHeartRate);
                prepare.setString(3, timeStamp.toString());
                prepare.setInt(4, binary);

                prepare.executeUpdate();
                return true;


            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }

            Log.d(TAG, "asynctask ouside false");
            return true;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d(TAG, "Good Job");
                //retrieveMessage("button");
            } else
                Log.d(TAG, "Good Effort");
        }

    }

    private void getTimeStamp() {
        java.util.Date utilDate = new java.util.Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        cal.set(Calendar.MILLISECOND, 0);
        timeStamp = new java.sql.Timestamp(utilDate.getTime());
        Log.d(TAG, timeStamp.toString());
    }




    //gets ID and HeartRate
    /*private void getSharedPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userID = prefs.getString("ID", null);
        Log.d(TAG, userID);
        String heartData = (prefs.getString("HeartRate", null));
        userHeartRate = Integer.parseInt(heartData);
        Log.d("TAG from shared prefer", heartData);
    }*/

    //sets flag
    private int checkThreshold() {

        int HRLimit = 0;
        String id = getPatientId();
        Log.d("this patients ID is", id);
        binary = 0;
        try {
            Connection conn = SQLConnection.doInBackground();
            String SQL = "SELECT HR_Limits FROM healthApp.Patient WHERE Id = ?";

            PreparedStatement prepare = conn.prepareStatement(SQL);
            prepare.setString(1, id);
            ResultSet rs = prepare.executeQuery();

            while (rs.next()) {
                HRLimit = rs.getInt("HR_Limits");
                Log.d("heartRateLimit", String.valueOf(HRLimit));
            }

            if (HRLimit < parseInt(userHeartRate)) {

                binary = 1;

            } else {
                binary = 0;
            }


        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return binary;
    }

    /*private void retrieveMessage(String message) {
        Intent intent = new Intent();
        intent.setAction(AFTER_INSERT);
        intent.putExtra("visible", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }*/

    private void sendSMS(String phoneNumber, String message) {
        Log.d("Phone Number", phoneNumber);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }



}
