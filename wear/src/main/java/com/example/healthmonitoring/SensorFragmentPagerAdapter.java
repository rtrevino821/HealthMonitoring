package com.example.healthmonitoring;

import android.app.FragmentManager;
import android.support.wearable.view.GridPagerAdapter;

/**
 * Created by stevenjoy on 2/25/17.
 */
import android.app.FragmentManager;
import android.support.wearable.view.GridPagerAdapter;

/**
 * Created by stevenjoy on 2/22/17.
 */
import android.app.Fragment;
import android.app.FragmentManager;
import android.hardware.Sensor;
import android.support.wearable.view.FragmentGridPagerAdapter;

public class SensorFragmentPagerAdapter extends FragmentGridPagerAdapter {

    private int[] sensorTypes = {
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_GAME_ROTATION_VECTOR,
            Sensor.TYPE_LIGHT
    };

    public SensorFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getFragment(int row, int column) {
        return SensorFragment.newInstance(sensorTypes[column]);
    }

    @Override
    public int getRowCount() {
        return 1; // fix to 1 row
    }

    @Override
    public int getColumnCount(int row) {
        return sensorTypes.length;
    }

}
