package com.example.healthmonitoring;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity {

    TextView firstName;
    TextView lastName;
    TextView dateOfBirth;
    TextView gender;
    TextView phone;
    TextView emergencyContact;
    TextView hrLimit;
    TextView address;
    private List<String> textData = new ArrayList<>();

    private BackgroundTask task;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeTextViews();

        task = new BackgroundTask(context);
        task.execute();

    }

    private void initializeTextViews() {
        firstName = (TextView) findViewById(R.id.tvFirstName);
        lastName = (TextView) findViewById(R.id.tvLastName);
        dateOfBirth = (TextView) findViewById(R.id.tvDateOfBirth);
        gender = (TextView) findViewById(R.id.tvGender);
        phone = (TextView) findViewById(R.id.tvPhone);
        emergencyContact = (TextView) findViewById(R.id.tvEmergencyContact);
        hrLimit = (TextView) findViewById(R.id.tvHeartRateThreshold);
        address = (TextView) findViewById(R.id.tvAddress);
    }

    public String getPatientId(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("ID", "");
        if(!name.equalsIgnoreCase(""))
        {
            return name;
        }else{
            return null;
        }
    }

    private class BackgroundTask extends AsyncTask<String,Void,Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            URL url2 = null;
            try {
                Log.d("url",getPatientId());
                url2 = new URL("https://q3igdv3op1.execute-api.us-east-1.amazonaws.com/prod/getPatientInfo?username=none&id=" + getPatientId());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            StringBuilder result2 = null;

            try {
                HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(url2.openStream()));
                result2 = new StringBuilder();
                String line2;
                while ((line2 = reader2.readLine()) != null) {
                    result2.append(line2);
                }
                String resultString2 = result2.toString();
                //Log.d("TAG",resultString2);
                JSONObject patientItem = new JSONObject(resultString2);
                JSONObject patientInfo = patientItem.getJSONObject("Item");

                // Log.d("Tag", member.toString());
                ObjectMapper mapper = new ObjectMapper();

                Patient patient = mapper.readValue(patientInfo.toString(), Patient.class);

                textData.add(patient.getF_name()); //First Name
                textData.add(patient.getL_name()); //Last Name
                textData.add(patient.getAge()); //Age
                textData.add(patient.getGender()); //Gender
                textData.add(patient.getPhone()); //Phone
                textData.add(patient.getEmer_contact()); //Emer Contact
                textData.add(patient.getHr_limits()); //HR_Limits
                textData.add(patient.getAddress() + " " + patient.getCity() + ", " + patient.getState());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e1) {

                e1.printStackTrace();

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                Log.d("OnPostExecute", "True");
                setTextViews();
            } else {
                Log.d("OnPostExecute", "False");
            }
        }
    }

    private void setTextViews() {
        Log.d("SetTextViews", textData.get(6));
        firstName.setText(textData.get(0));
        lastName.setText(textData.get(1));
        dateOfBirth.setText(textData.get(2));
        gender.setText(textData.get(3));
        phone.setText(textData.get(4));
        emergencyContact.setText(textData.get(5));
        hrLimit.setText(textData.get(6));
        address.setText(textData.get(7));
    }

}