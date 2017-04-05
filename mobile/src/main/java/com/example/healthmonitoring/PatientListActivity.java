package com.example.healthmonitoring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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

public class PatientListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private List<PatientDoctor> patientDoctors;
    private BackgroundTask task;
    private Context context;
    public  final String UPDARTETHRESHOLD = "UPDARTETHRESHOLD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_doctor);

        context = this;

        //Toast.makeText(context, "OnCreate", Toast.LENGTH_SHORT).show();


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

          intializeData();
        //intializeAdapter();
//        recyclerView.invalidate();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(UPDARTETHRESHOLD));

    }




    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //intializeData();
            //intializeAdapter();

            if(intent.getStringExtra("itemPosition") != null)
            {
                Log.d("UnoDos","broadcast receive");

                final String pos = intent.getStringExtra("itemPosition");
                final String threshold = intent.getStringExtra("threshold");
                final String patient = intent.getStringExtra("patientName");
                final String ID = intent.getStringExtra("ID");

                patientDoctors.remove(Integer.parseInt(pos));
                patientDoctors.add(Integer.parseInt(pos), (new PatientDoctor(patient, ID,threshold)));
                //intializeAdapter();

                Log.d("UnoDos","position: " + pos );

            }

        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(this, "OnRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "OnPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();
        //intializeData();
       // intializeAdapter();
    }

    private void intializeData() {
        patientDoctors = new ArrayList<>();
        task = new BackgroundTask(context);
        task.execute();

    }

    private void intializeAdapter() {
        adapter = new RecyclerAdapterDoctor(patientDoctors);
        recyclerView.setAdapter(adapter);
    }

    private class BackgroundTask extends AsyncTask<String,Void,Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("https://q3igdv3op1.execute-api.us-east-1.amazonaws.com/prod/getAllPatients");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection urlConnection2 = (HttpURLConnection) url.openConnection();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder result = new StringBuilder();
                String line2;
                while ((line2 = reader2.readLine()) != null) {
                    result.append(line2);
                }
                String resultString = result.toString();
                //Log.d("TAG",resultString2);
                JSONObject patientItems = new JSONObject(resultString);
                JSONArray patientInfoArray = patientItems.getJSONArray("Items");
                String countString = patientItems.getString("Count");
                int count = Integer.parseInt(countString);
                for(int i = 0;i < count;i++){
                    JSONObject patient = (JSONObject) patientInfoArray.get(i);
                    patientDoctors.add(new PatientDoctor(patient.getString("l_name")+", " + patient.getString("f_name"),patient.getString("id"),patient.getString("hr_limits")));
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
            /*
            String sqlPatientQuery = "SELECT * FROM healthApp.Patient join healthApp.Logins\n" +
                    "on Logins.ID = Patient.id\n" +
                    "Where Admin <> 'Y'";
            try {
                Connection conn = SQLConnection.doInBackground();
                PreparedStatement prepare = conn.prepareStatement(sqlPatientQuery);

                ResultSet rs = prepare.executeQuery();

                while (rs.next()) {
                    patientDoctors.add(new PatientDoctor(rs.getString("L_Name")+", "+rs.getString("F_Name"), rs.getString("Id"), rs.getString("HR_Limits")));                }
                rs.close();
                return true;

            } catch (SQLException e) {
                Log.d("SQL Error", e.getMessage());
            }

            //Login Failed
            Log.d("Error", "asynctask ouside false");
            return false;
        */
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                Log.d("OnPostExecute", "True");
                intializeAdapter();
            } else {
                Log.d("OnPostExecute", "False");
            }
        }
    }


}
