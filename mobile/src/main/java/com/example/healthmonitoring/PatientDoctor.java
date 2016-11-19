package com.example.healthmonitoring;

/**
 * Created by rtrev on 11/10/2016.
 */

class PatientDoctor {
    String name;
    String lastVisit;
    String threshold;

    PatientDoctor(String name, String lastVisit, String threshold) {
        this.name = name;
        this.lastVisit = lastVisit;
        this.threshold = threshold;
    }
}
