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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    }

    private class BackgroundTask extends AsyncTask<String,Void,Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try{
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                URL url2 = new URL("https://q3igdv3op1.execute-api.us-east-1.amazonaws.com/prod/heartRateData?id=" + preferences.getString("ID","") );
                StringBuilder result2 = null;

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

                // Log.d("Tag", member.toString());
                ObjectMapper mapper = new ObjectMapper();

                JSONArray heartDataArray = patientItem.getJSONArray("Items");

                for(int i =0;i<heartDataArray.length();i++){
                    JSONObject data = (JSONObject) heartDataArray.get(i);

                    heartData.add(new HeartData(data.getString("timeStamp"),data.getString("timeStamp"),data.getString("heartRate")));
                }
            }catch(IOException e){

            }catch(JSONException e1){
                e1.printStackTrace();
            }

            /*
            String sqlHeartData = "SELECT * FROM healthApp.HeartRateData WHERE Id = ? ORDER BY TimeStamp desc";
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
        */
            return true;
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
