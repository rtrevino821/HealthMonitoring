package com.example.healthmonitoring;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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
                        try {
                            upDateThreshold(patientId, threshold);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
        }

        finish();


    }


    public void upDateThreshold(String patientId, String threshold) throws IOException {

        URL url = new URL("https://q3igdv3op1.execute-api.us-east-1.amazonaws.com/prod/getPatientInfo?id=" + patientId + "&hr=" + threshold);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
        out.close();

        /*tring sql = "update healthApp.Patient set HR_Limits = " + threshold + " where Id = " + patientId;
        try {
            Connection conn = SQLConnection.doInBackground();
            PreparedStatement prepare = conn.prepareStatement(sql);

            prepare.execute();
            prepare.close();

        } catch (SQLException e) {
            Log.d("SQL Error", e.getMessage());
        }
*/
    }

}
