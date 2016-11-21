package com.example.healthmonitoring;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        intializeData();
        intializeAdapter();
    }

    private void intializeData() {
        patientDoctors = new ArrayList<>();
        task = new BackgroundTask(context);
        task.execute();

        /*patientDoctors.add(new PatientDoctor("Trevino, Rodolfo", "8/29/2016", "120"));
        patientDoctors.add(new PatientDoctor("Wayne, Bruce", "5/9/2016", "190"));
        patientDoctors.add(new PatientDoctor("Jones, Mike", "8/2/2010", "80"));
        patientDoctors.add(new PatientDoctor("Brady, Tom", "10/22/2016", "140"));
        patientDoctors.add(new PatientDoctor("Trump, Dump", "11/9/2016", "60"));
        patientDoctors.add(new PatientDoctor("Tyson, Mike", "4/3/2016", "150"));
        patientDoctors.add(new PatientDoctor("Jolie, Angelina", "8/29/2016", "90"));
        patientDoctors.add(new PatientDoctor("Marino, Dan", "6/2/2012", "140"));
        patientDoctors.add(new PatientDoctor("Vergara, Sophia", "5/29/2014", "110"));
        patientDoctors.add(new PatientDoctor("Madison, Billy", "1/6/2013", "90"));*/
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
            String sqlPatientQuery = "SELECT * FROM healthApp.Patient";
            try {
                Connection conn = SQLConnection.doInBackground();
                PreparedStatement prepare = conn.prepareStatement(sqlPatientQuery);

                ResultSet rs = prepare.executeQuery();

                while (rs.next()) {
                    patientDoctors.add(new PatientDoctor(rs.getString(3)+", "+rs.getString(2), rs.getString(1), rs.getString(7)));
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
                intializeAdapter();
            } else {
                Log.d("OnPostExecute", "False");
            }
        }
    }
}
