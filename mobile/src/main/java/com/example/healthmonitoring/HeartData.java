package com.example.healthmonitoring;

/**
 * Created by rtrev on 11/12/2016.
 */

class HeartData {

    String timestamp;
    String date;
    String heartRate;


    HeartData(String timestamp, String date, String heartRate){
        this.timestamp = timestamp;
        this.date = date;
        this.heartRate = heartRate;
    }

}