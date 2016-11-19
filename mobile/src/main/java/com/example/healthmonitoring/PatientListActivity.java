package com.example.healthmonitoring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private List<PatientDoctor> patientDoctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_doctor);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        intializeData();
        intializeAdapter();
    }

    private void intializeData() {
        patientDoctors = new ArrayList<>();
        patientDoctors.add(new PatientDoctor("Trevino, Rodolfo", "8/29/2016", "120"));
        patientDoctors.add(new PatientDoctor("Wayne, Bruce", "5/9/2016", "190"));
        patientDoctors.add(new PatientDoctor("Jones, Mike", "8/2/2010", "80"));
        patientDoctors.add(new PatientDoctor("Brady, Tom", "10/22/2016", "140"));
        patientDoctors.add(new PatientDoctor("Trump, Dump", "11/9/2016", "60"));
        patientDoctors.add(new PatientDoctor("Tyson, Mike", "4/3/2016", "150"));
        patientDoctors.add(new PatientDoctor("Jolie, Angelina", "8/29/2016", "90"));
        patientDoctors.add(new PatientDoctor("Marino, Dan", "6/2/2012", "140"));
        patientDoctors.add(new PatientDoctor("Vergara, Sophia", "5/29/2014", "110"));
        patientDoctors.add(new PatientDoctor("Madison, Billy", "1/6/2013", "90"));
    }

    private void intializeAdapter() {
        adapter = new RecyclerAdapterDoctor(patientDoctors);
        recyclerView.setAdapter(adapter);
    }
}
