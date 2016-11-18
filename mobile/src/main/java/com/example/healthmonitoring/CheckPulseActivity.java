package com.example.healthmonitoring;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import com.skyfishjy.library.RippleBackground;

import static com.example.healthmonitoring.MyService.ACTION_TEXT_CHANGED;

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


    final RippleBackground rippleBackground = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.check_my_pulse);
        setContentView(R.layout.activity_check_pulse);

        //initialize text views
        tvHeartRate = (TextView) findViewById(R.id.tv_Heart_Rate);
        stopHR = (Button) findViewById(R.id.btn_check_my_pulse_stop);
        getHR = (Button) findViewById(R.id.btn_check_my_pulse);
        bpm = (TextView) findViewById(R.id.tv_bpm);

        //message passing
        mTeleportClient = new TeleportClient(this);

        //Ripple effect Background
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);

        // Custom animation on image
        ImageView myView = (ImageView) findViewById(R.id.img_pulse);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(myView, "alpha",  1f, .2f);
        fadeOut.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(myView, "alpha", .2f, 1f);
        fadeIn.setDuration(500);
        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn).after(fadeOut);

        tvHeartRate = (TextView) findViewById(R.id.tv_Heart_Rate);
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
                startMeasure();
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

    public void getPatientId(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("ID", "");
        if(!name.equalsIgnoreCase(""))
        {
            bpm.setText(name);
        }

        /*SharedPreferences prefs = getSharedPreferences("patientIdReference", MODE_PRIVATE);
        String restoredText = prefs.getString("patientID", null);
        if (restoredText != null) {
            patientIdValue = prefs.getString("patientID", "0");//"No name defined" is the default value.
            bpm.setText(patientIdValue);
        }*/

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



    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra("content");
            tvHeartRate.setText(content);
        }
    };

    private void startMeasure() {
            mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
            Log.d(TAG, "StartPulse");

        mTeleportClient.sendMessage("startActivity", null);
    }


    public class ShowToastFromOnGetMessageTask extends TeleportClient.OnGetMessageTask {

        @Override
        protected void onPostExecute(String path) {

            if (path.equals("Finished")) {

                Log.d(TAG, "Finished");

                Toast.makeText(getApplicationContext(), "Message - " + path, Toast.LENGTH_SHORT).show();
                //Insert HeartRate in DB
                //int heartRateInteger = Integer.parseInt(tvHeartRate.getText().toString());

                //stopMeasure();


            }
            mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
            //stopMeasure();
        }
    }

    private void stopMeasure() {
        getHR.setVisibility(View.VISIBLE);
        rippleBackground.stopRippleAnimation();
    }


    //Task to show the String from DataMap with key "string" when a DataItem is synced
    public class ShowToastOnSyncDataItemTask extends TeleportClient.OnSyncDataItemTask {

        protected void onPostExecute(DataMap dataMap) {

            String s = dataMap.getString("heartData");

            Toast.makeText(getApplicationContext(),"DataItem - "+s,Toast.LENGTH_SHORT).show();

            mTeleportClient.setOnSyncDataItemTask(new ShowToastOnSyncDataItemTask());
        }
    }

}
