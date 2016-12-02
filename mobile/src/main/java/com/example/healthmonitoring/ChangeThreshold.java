package com.example.healthmonitoring;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by steven j on 11/26/2016.
 */
public class ChangeThreshold extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {

                final String patientId = extras.getString("patientId");
                final String threshold = extras.getString("patientThreshold");
            Toast.makeText(this, "Update Threshold to " + threshold + " for patient " + patientId, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        upDateThreshold(patientId, threshold);
                    }
                }).start();
        }

        finish();


    }


    public void upDateThreshold(String patientId, String threshold) {

        String sql = "update healthApp.Patient set HR_Limits = " + threshold + " where Id = " + patientId;
        try {
            Connection conn = SQLConnection.doInBackground();
            PreparedStatement prepare = conn.prepareStatement(sql);

            prepare.execute();
            prepare.close();

        } catch (SQLException e) {
            Log.d("SQL Error", e.getMessage());
        }

    }

}
