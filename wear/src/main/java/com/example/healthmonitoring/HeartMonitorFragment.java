package com.example.healthmonitoring;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HeartMonitorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HeartMonitorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeartMonitorFragment extends Fragment  implements SensorEventListener, DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "Wearable/ MainActivity";
    private TextView mTextView;
    private TextView stepCountTV;
    private ImageButton btnStart;
    private ImageButton btnPause;
    private Button btnHRHistory;
    private Button buttonAccel;
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

    private OnFragmentInteractionListener mListener;

    public HeartMonitorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HeartMonitorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HeartMonitorFragment newInstance(String param1, String param2) {
        HeartMonitorFragment fragment = new HeartMonitorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mSensorManager  = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View mView = inflater.inflate(R.layout.sensor, container, false);




        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_heart_monitor, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void onStart() {
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
    public void onPause() {
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
        timer = new CountDownTimer(13500, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d(TAG," Timer done");
                //let's reset the task (otherwise it will be executed only once)
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            //   Thread.sleep(500); // As I am using LENGTH_LONG in Toast
                            //MainActivity.this.finish();
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
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
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
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        mHeartRate = Math.round(mHeartRateFloat);

        if(mTextView != null)
        {
            if(sensor.getType() == Sensor.TYPE_HEART_RATE) {
                mTextView.setText(Integer.toString(mHeartRate));
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());

                logHeartRate(mHeartRate, date);
            } else if(sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                logSteps(value);
            }

        }
    }

    private void logSteps(int steps) {
        //textView.setText("Steps : " + steps);
    }

    @Override
    public void onAccuracyChanged (Sensor sensor,int accuracy){

    }

    @Override
    public void onDataChanged (DataEventBuffer dataEventBuffer){

    }

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
