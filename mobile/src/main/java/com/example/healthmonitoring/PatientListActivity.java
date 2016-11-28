package com.example.healthmonitoring;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_doctor);

        context = this;

        Toast.makeText(context, "OnCreate", Toast.LENGTH_SHORT).show();


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        //intializeData();
        //intializeAdapter();

    }

    //notifyDataSetChanged();

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "OnRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "OnPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();
        intializeData();
        intializeAdapter();
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
