package com.example.healthmonitoring;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

    public static final String ACTION_TEXT_CHANGED = "com.example.healthmonitoring.TEXT_CHANGED";
    private String TAG = "MyServiceActivity";
    private int heartRate;



    @Override
    public void onDataChanged(DataEventBuffer dataEvents){

        for(DataEvent dataEvent : dataEvents){
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if(item.getUri().getPath().compareTo("/heart-rate") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Log.d(TAG, "Heart Rate!!!!!!!!!!!: " + dataMap.getInt("heart-rate"));
                    heartRate = dataMap.getInt("heart-rate");
                    Log.d(TAG, "TimeLog!!!!!!!!: " + dataMap.getString("timestamp"));
                    retrieveMessage(Integer.toString(heartRate));
                }
                /*DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals("/heart-rate")) {
                    int heartRate = dataMap.getInt("heart-rate");
                    long time = dataMap.getLong("timestamp");



                    System.out.println("STEPS!!!!!!!: " + heartRate);
                    System.out.println("Time!!!!!!!: " + time);
                }*/
            }
        }

    }

    private void retrieveMessage(String message) {
        Intent intent = new Intent();
        intent.setAction(ACTION_TEXT_CHANGED);
        intent.putExtra("content", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
