package com.example.healthmonitoring;

/**
 * Created by rtrev on 11/10/2016.
 */

class PatientDoctor {
    String name;
    String patientID;
    String threshold;

    PatientDoctor(String name, String patientID, String threshold) {
        this.name = name;
        this.patientID = patientID;
        this.threshold = threshold;
    }
}
