package com.example.healthmonitoring;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.BreakIterator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class MainActivity extends WearableActivity implements SensorEventListener, DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Wearable/ MainActivity";
    private TextView mTextView;
    private TextView stepCount;
    private TextView activityLevel;
    private ImageButton btnStart;
    private ImageButton btnPause;
    private Button btnHRHistory;
    //private Button buttonAccel;
    private GoogleApiClient mGoogleApiClient;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private TeleportClient mTeleportClient;
    TeleportClient.OnSyncDataItemTask mOnSyncDataItemTask;
    TeleportClient.OnGetMessageTask mMessageTask;
    private CountDownTimer timer;
    private int mHeartRate;
    Node mNode; // the connected device to send the message to
    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";
    private boolean mResolvingError=false;
    private boolean timeLeft = false;
    private boolean firstStep = false;
    int testCount = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //message passing
        mTeleportClient = new TeleportClient(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.heartRateText);
                stepCount = (TextView) stub.findViewById(R.id.stepCountResultsTV);
                activityLevel = (TextView) stub.findViewById(R.id.activityLevelResultTV);
//                btnStart = (ImageButton) stub.findViewById(R.id.btnStart);
//                btnPause = (ImageButton) stub.findViewById(R.id.btnPause);
                  Button buttonAccel= (Button) findViewById(R.id.button1);

//                /** ADDED TRYING TO LOAD ACCELEROMETER CLASS*/
                buttonAccel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       setContentView(R.layout.round_activity2);

                        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
                        pager.setAdapter(new SensorFragmentPagerAdapter(getFragmentManager()));

                        DotsPageIndicator indicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
                        indicator.setPager(pager);
//                        Intent mainIntent = new Intent(MainActivity.this, AccelerometerActivity.class);
//                        startActivity(mainIntent);
                    }
                });


            }
        });
//
//                btnStart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        btnStart.setVisibility(ImageButton.GONE);
//                        btnPause.setVisibility(ImageButton.VISIBLE);
//                        mTextView.setText("Please wait...");
//                        startMeasure();
//                    }
//                });
//
//                btnPause.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        btnPause.setVisibility(ImageButton.GONE);
//                        btnStart.setVisibility(ImageButton.VISIBLE);
//                        mTextView.setText("--");
//                        stopMeasure();
//                    }
//                });
//            }
//        });



        setAmbientEnabled();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        initTimer();
        startMeasure();
        //exampleFunction();

        Log.d("tag on create", "how many runs");

        final List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor type : deviceSensors){
            Log.d("ALLsensors",type.getStringType());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
        mTeleportClient.connect();
    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mGoogleApiClient.disconnect();
//        mTeleportClient.disconnect();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    @Override //ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        resolveNode();
        Log.d(TAG, "Google API Client was connected");
    }

    @Override //ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Connection to Google API client was suspended");
    }

    private void initTimer()
    {
        timer = new CountDownTimer(20500, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d(TAG," Timer done");
                //let's reset the task (otherwise it will be executed only once)
                sendMessage();
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                         //   Thread.sleep(500); // As I am using LENGTH_LONG in Toast
                            MainActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.run();
//                syncDataItem(mHeartRate);
//                mTeleportClient.setOnSyncDataItemTask(mOnSyncDataItemTask);

            }
        };

    }



        public void exampleFunction() {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(new Runnable() {
            public void run() {
                stopMeasure();
            }
        }, 10, TimeUnit.SECONDS);
        exec.shutdown();
    }

    private void stopMeasure() {
        Log.d("Sensor Status:"," Stopped!");
        mSensorManager.unregisterListener(this);
        timer.cancel();
    }

    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        boolean stepSensorRegistered = mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
        Log.d("Step Sensor Status:", " Sensor registered: " + (stepSensorRegistered ? "yes" : "no"));
        timer.start();
    }

    // Create a data map and put data in it
    public void logHeartRate(int heartRate, String timestamp) {
        Log.d("LogHeartRate", "Recieved: " + heartRate + " bpm @ " + timestamp);

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/heart-rate"); //path to object

        putDataMapRequest.getDataMap().putInt("heart-rate", heartRate);
        putDataMapRequest.getDataMap().putString("timestamp", timestamp);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, request);

/*Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        if(!dataItemResult.getStatus().isSuccess()){

                        } else {

                        }
                    }
                });*/

    }

    @Override
    public void onSensorChanged (SensorEvent event){
        Sensor sensor = event.sensor;
        float mHeartRateFloat = event.values[0];
        Log.d("Event.Value Length:", String.valueOf(event.values.length));
        float[] values = event.values;
        int value = -1;

//        if (!firstStep && values.length > 0) {
//            testCount = (int) event.values[0];
//            Log.d("Event Init Step Count", String.valueOf(testCount));
//            firstStep = true;
//        }

        if (values.length > 0) {
            value = (int) (values[0]);
        }

        mHeartRate = Math.round(mHeartRateFloat);

        if(mTextView != null)
        {
            if(sensor.getType() == Sensor.TYPE_HEART_RATE) {
                mTextView.setText(Integer.toString(mHeartRate));
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                logHeartRate(mHeartRate, date);
            } else if(sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                //steps++;
                stepCount.setText(Integer.toString(value));
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                logSteps(value, date);
            }

        }
    }

    private void logSteps(int steps, String timestamp) {

        if(steps < 2000)
            activityLevel.setText("Low");
        if(steps >= 2000 && steps <= 5000)
            activityLevel.setText("Good");
        if(steps > 5000)
            activityLevel.setText("Excellent");

        Log.d("logSteps", "Recieved: " + steps + " steps @ " + timestamp);

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/step-count"); //path to object

        putDataMapRequest.getDataMap().putInt("step-count", steps);
        putDataMapRequest.getDataMap().putString("step-timestamp", timestamp);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        super.onResume();
        mSensorManager.registerListener(this, mStepCounterSensor,

                SensorManager.SENSOR_DELAY_FASTEST);

//        mSensorManager.registerListener(this, mStepDetectorSensor,
//                SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onDataChanged (DataEventBuffer dataEventBuffer){

    }


    //Task that shows the path of a received message
    public class ShowToastFromOnGetMessageTask extends TeleportClient.OnGetMessageTask {

        @Override
        protected void onPostExecute(String  path) {

//            if (path.equals("stop")){
//                mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
//
//                stopMeasure();
//                Toast.makeText(getApplicationContext(),"Message - "+path,Toast.LENGTH_SHORT).show();
//                Log.d(TAG, path);
//                btnPause.setVisibility(ImageButton.GONE);
//                btnStart.setVisibility(ImageButton.VISIBLE);
//                mTextView.setText("--");
//                //let's reset the task (otherwise it will be executed only once)
//                //mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
//
//            }
//            else if (path.equals("start")){
//                mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
//                Log.d(TAG, path);
//                startMeasure();
//
//                Toast.makeText(getApplicationContext(),"Message - "+path,Toast.LENGTH_SHORT).show();
//                btnStart.setVisibility(ImageButton.GONE);
//                btnPause.setVisibility(ImageButton.VISIBLE);
//                mTextView.setText("Please wait...");
//                //let's reset the task (otherwise it will be executed only once)
//                //mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
//            }

        }

        }


    private void sendMessage() {

        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD_WEAR_PATH, null).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            //Improve your code
        }

    }//end of method

    /*
    * Resolve the node = the connected device to send the message to
    */
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
            }
        });
    }


    }
