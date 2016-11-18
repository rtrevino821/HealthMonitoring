package com.example.healthmonitoring;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeartHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private List<HeartData> heartData;
    private BackgroundTask task;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_history);

        context = this;

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        intializeData();
        intializeAdapter();
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

    private void intializeAdapter() {
        adapter = new RecyclerAdapter(heartData);
        recyclerView.setAdapter(adapter);
    }

    private void intializeData() {
        heartData = new ArrayList<>();
        task = new BackgroundTask(context);
        task.execute();

/*        heartData.add(new HeartData("8:30pm", "8/29/2016", "120"));
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
        heartData.add(new HeartData("6:30pm", "8/29/2016", "50"));*/
    }

    private class BackgroundTask extends AsyncTask<String,Void,Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String sqlHeartData = "SELECT * FROM healthApp.HeartRateData WHERE Id = ?";
            try {
                Connection conn = SQLConnection.doInBackground();
                PreparedStatement prepare = conn.prepareStatement(sqlHeartData);

                String id = getPatientId();

                prepare.setString(1, id);
                Log.d("Patient ID: ", id);
                ResultSet rs = prepare.executeQuery();

                while (rs.next()) {
                    String datePrint= null;
                    String timePrint = null;

                    String timeStamp = String.valueOf(rs.getTimestamp(3));
                    SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {
                        Date parseDate = d.parse(timeStamp);
                        SimpleDateFormat print = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat printTime = new SimpleDateFormat("HH:mm");
                        datePrint = print.format(parseDate);
                        timePrint = printTime.format(parseDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.d("rs.next: ", datePrint + " & " + timePrint);
                    heartData.add(new HeartData(timePrint, datePrint, String.valueOf(rs.getInt(2))));
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
