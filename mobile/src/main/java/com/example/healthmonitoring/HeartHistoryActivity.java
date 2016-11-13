package com.example.healthmonitoring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HeartHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private List<HeartData> heartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_history);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        intializeData();
        intializeAdapter();
    }

    private void intializeAdapter() {
        adapter = new RecyclerAdapter(heartData);
        recyclerView.setAdapter(adapter);
    }

    private void intializeData() {
        heartData = new ArrayList<>();
        heartData.add(new HeartData("8:30pm", "8/29/2016", "120"));
        heartData.add(new HeartData("7:30pm", "8/29/2016", "70"));
        heartData.add(new HeartData("6:30pm", "8/29/2016", "60"));
        heartData.add(new HeartData("5:30pm", "8/29/2016", "65"));
        heartData.add(new HeartData("4:30pm", "8/29/2016", "69"));
        heartData.add(new HeartData("3:30pm", "8/29/2016", "60"));
        heartData.add(new HeartData("2:30pm", "8/29/2016", "70"));
        heartData.add(new HeartData("1:30pm", "8/29/2016", "72"));
        heartData.add(new HeartData("12:30pm", "8/29/2016", "60"));
        heartData.add(new HeartData("11:30pm", "8/29/2016", "70"));
        heartData.add(new HeartData("10:30pm", "8/29/2016", "80"));
        heartData.add(new HeartData("9:30pm", "8/29/2016", "60"));
        heartData.add(new HeartData("8:30pm", "8/29/2016", "80"));
        heartData.add(new HeartData("7:30pm", "8/29/2016", "70"));
        heartData.add(new HeartData("6:30pm", "8/29/2016", "50"));
    }
}
