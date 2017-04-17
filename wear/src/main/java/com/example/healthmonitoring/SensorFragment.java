package com.example.healthmonitoring;

/**
 * Created by stevenjoy on 2/25/17.
 */
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SensorFragment extends Fragment implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 250;
    private static final float ROTATION_THRESHOLD = 2.0f;
    private static final int ROTATION_WAIT_TIME_MS = 100;

    private View mView;
    private TextView mTextTitle;
    private TextView mTextValues;
    private TextView lightSensor;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mSensorType;
    private long mShakeTime = 0;
    private long mRotationTime = 0;
    private FragmentManager fragmentManager;
    private Fragment switchFragment;

    public static SensorFragment newInstance(int sensorType) {
        SensorFragment f = new SensorFragment();

        // Supply sensorType as an argument
        Bundle args = new Bundle();
        args.putInt("sensorType", sensorType);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            mSensorType = args.getInt("sensorType");
        }

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(mSensorType);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        final List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor type : deviceSensors) {
            Log.d("sensors", type.getStringType());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.sensor, container, false);

        mTextTitle = (TextView) mView.findViewById(R.id.text_title);
        if(mSensor.getStringType().contains("rotation"))
            mTextTitle.setText("Game Rotation\nSensor");
        else if (mSensor.getStringType().contains("gyroscope"))
            mTextTitle.setText("Gyroscope Sensor");
        else if (mSensor.getStringType().contains("light"))
            mTextTitle.setText("Light Sensor");
        else if (mSensor.getStringType().contains("accelerometer"))
            mTextTitle.setText("Accelerometer");
        else mTextTitle.setText(mSensor.getStringType());
        mTextValues = (TextView) mView.findViewById(R.id.text_values);
        lightSensor = (TextView) mView.findViewById(R.id.bright_values);

        return mView;

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // If sensor is unreliable, then just return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }
        else {
            mTextValues.setText(
                    "x = " + Float.toString(event.values[0])+ " m/s^2" + "\n" +
                            "y = " + Float.toString(event.values[1])+ " m/s^2" + "\n" +
                            "z = " + Float.toString(event.values[2])+ " m/s^2" + "\n"
            );

            if (event.sensor.getType()==Sensor.TYPE_LIGHT) {
                mTextValues.setText(
                        "x = " + Float.toString(event.values[0]) + " lx" + "\n");
                detectLight(event);
            }
        }

       // if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
           // detectShake(event);
      //  }
         if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            detectRotation(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void detectLight(SensorEvent event) {

        float lightValue = event.values[0];

        if (lightValue > 800) {
            lightSensor.setText("Too Bright!");
        }else {
                lightSensor.setText("");
            }
        }



//    private void detectShake(SensorEvent event) {
//        long now = System.currentTimeMillis();
//
//        if((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
//            mShakeTime = now;
//
//            double gX = event.values[0] / SensorManager.GRAVITY_EARTH;
//            double gY = event.values[1] / SensorManager.GRAVITY_EARTH;
//            double gZ = event.values[2] / SensorManager.GRAVITY_EARTH;
//
//            // gForce will be close to 1 when there is no movement
//            double gForce = Math.sqrt(gX*gX + gY*gY + gZ*gZ);
//
//            // Change background color if gForce exceeds threshold;
//            // otherwise, reset the color
//            if(gForce > SHAKE_THRESHOLD) {
//                mView.setBackgroundColor(Color.rgb(0, 100, 0));
//            }
//            else {
//            //    mView.setBackgroundColor(Color.BLACK);
//            }
//        }
//    }


    private void detectRotation(SensorEvent event) {
        long now = System.currentTimeMillis();

        if((now - mRotationTime) > ROTATION_WAIT_TIME_MS) {
            mRotationTime = now;

            // Change background color if rate of rotation around any
            // axis and in any direction exceeds threshold;
            // otherwise, reset the color
            if(Math.abs(event.values[0]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[1]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[2]) > ROTATION_THRESHOLD) {
                mView.setBackgroundColor(Color.rgb(0, 100, 0));
            }
            else {
              //  mView.setBackgroundColor(Color.BLACK);
            }
        }
    }
}