package com.example.healthmonitoring;

/**
 * Created by rtrev on 11/10/2016.
 */

class PatientAlert {
    String name;
    String patientID;
    String threshold;
    String heartRate;
    String date;

    PatientAlert(String name, String patientID, String threshold, String heartRate, String date) {
        this.name = name;
        this.patientID = patientID;
        this.threshold = threshold;
        this.heartRate = heartRate;
        this.date = date;
    }
}