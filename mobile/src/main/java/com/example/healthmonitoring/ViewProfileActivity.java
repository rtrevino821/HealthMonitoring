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
            String sqlHeartData = "SELECT * FROM healthApp.Patient WHERE Id = ?";
            try {
                Connection conn = SQLConnection.doInBackground();
                PreparedStatement prepare = conn.prepareStatement(sqlHeartData);

                String id = getPatientId();

                prepare.setString(1, id);
                Log.d("Patient ID: ", id);
                ResultSet rs = prepare.executeQuery();

                while (rs.next()) {
                    textData.add(rs.getString("F_Name")); //First Name
                    textData.add(rs.getString("L_Name")); //Last Name
                    textData.add(rs.getString("Age")); //Age
                    textData.add(rs.getString("Gender")); //Gender
                    textData.add(rs.getString("Phone")); //Phone
                    textData.add(rs.getString("Emer_Contact")); //Emer Contact
                    textData.add(rs.getString("HR_Limits")); //HR_Limits
                    textData.add(rs.getString("Address") + " " + rs.getString("City") + ", " + rs.getString("State")); //Address
                }
                rs.close();
                return true;

            } catch (SQLException e) {
                Log.d("SQL Error", e.getMessage());
            }

            //Login Failed
            Log.d("Error", "asynctask ouside false");
            return false;

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