package com.example.healthmonitoring;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.example.healthmonitoring.R.id.tv_Current_Threshold;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by rtrev on 10/23/2016.
 */
public class RecyclerAdapterDoctor extends RecyclerView.Adapter<RecyclerAdapterDoctor.ViewHolder> {
    public  final String UPDARTETHRESHOLD = "UPDARTETHRESHOLD";

    Context context;
    String threshold;
    String patientId;
    int itemPosition;
    ViewHolder recView;
    private BackgroundTask task;
    String patientName;
    String ID;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemPatientName;
        public TextView itemLastVisit;
        public TextView itemThreshold;

        public ViewHolder(View itemView) {
            super(itemView);
            itemPatientName = (TextView) itemView.findViewById(R.id.tv_Patient_Name);
            itemLastVisit = (TextView) itemView.findViewById(R.id.tv_Last_Visit);
            itemThreshold = (TextView) itemView.findViewById(tv_Current_Threshold);

        }
    }

    List<PatientDoctor> patientDoctors;

    RecyclerAdapterDoctor(List<PatientDoctor> patientDoctors) {
        this.patientDoctors = patientDoctors;
    }


    public RecyclerAdapterDoctor(Context context) {
    }


    @Override
    public RecyclerAdapterDoctor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_doctor, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(v);
        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapterDoctor.ViewHolder viewHolder, final int position) {
        viewHolder.itemPatientName.setText(patientDoctors.get(position).name);
        viewHolder.itemLastVisit.setText("Patient ID: " + patientDoctors.get(position).patientID);
        viewHolder.itemThreshold.setText(patientDoctors.get(position).threshold);


        viewHolder.itemThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final String[] m_Text = {""};

                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.threshold_dialog);

                final EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.text);
                text.setText("Adjust Threshold");

                final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
                np.setMaxValue(200);
                np.setMinValue(50);

                Button ok = (Button) dialog.findViewById(R.id.dialogButtonOK);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_Text[0] = String.valueOf(np.getValue());
                        threshold = m_Text[0];
                        Log.d("TAG",threshold);
                        patientId = patientDoctors.get(position).patientID;
                        Log.d("TAG",patientId);
                        itemPosition = position;
                        patientName = (patientDoctors.get(position).name);


//                        Runnable runnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    upDateThreshold(patientId,threshold);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        };
//                        Thread thread = new Thread(runnable);
//                        thread.start();
//
//                        try {
//                            thread.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        viewHolder.itemThreshold.setText("20");

                        task = new BackgroundTask(context);
                        task.execute();




//                        Intent i = new Intent(view.getContext(), ChangeThreshold.class);
//                        i.putExtra("patientThreshold", m_Text[0]);
//                        i.putExtra("patientId", patientDoctors.get(position).patientID);
//                        view.getContext().startActivity(i);
                        notifyItemChanged(itemPosition);
                        notifyDataSetChanged();
                        String value = "" + np.getValue();
                        Log.d("dialog value is ", "" + np.getValue());

                        viewHolder.itemThreshold.setText(value);
                        dialog.dismiss();

                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

        });

    }
    public void upDateThreshold(String patientId, String threshold)throws IOException {
        Log.d("TAG","Making PUT Request");
        URL url = new URL("https://q3igdv3op1.execute-api.us-east-1.amazonaws.com/prod/getPatientInfo?id=" + patientId + "&hr=" + threshold);

        HttpURLConnection conn = ( HttpURLConnection ) url.openConnection();

        conn.setRequestMethod( "PUT" );
        conn.setDoOutput( false);
        conn.connect();



        /*
        String sql = "update healthApp.Patient set HR_Limits = " + threshold + " where Id = " + patientId;
        try {
            Connection conn = SQLConnection.doInBackground();
            PreparedStatement prepare = conn.prepareStatement(sql);

            prepare.execute();
            prepare.close();
            notifyItemChanged(itemPosition);
            notifyDataSetChanged();


        } catch (SQLException e) {
            Log.d("SQL Error", e.getMessage());
        }
*/
    }




    @Override
    public int getItemCount() {
        return patientDoctors.size();
    }
    class BackgroundTask extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Log.d("TAG","Making PUT Request");
            URL url = null;
            try {
                url = new URL("https://q3igdv3op1.execute-api.us-east-1.amazonaws.com/prod/getPatientInfo?id=" + patientId);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(
                        conn.getOutputStream());
                out.write(threshold.toString());
                Log.d("TAG","YEA");
                out.close();
                conn.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return true;
/*
            try {

                Connection conn = SQLConnection.doInBackground();

                String sql = "update healthApp.Patient set HR_Limits = " + threshold + " where Id = " + patientId;
;                PreparedStatement prepare = conn.prepareStatement(sql);

                prepare.executeUpdate();
                prepare.close();

                return true;


            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }

            Log.d(TAG, "asynctask ouside false");
            return true;
*/
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d(TAG, "Good Job");
                patientDoctors.get(itemPosition).threshold = threshold;
                notifyItemChanged(itemPosition);
                retrieveMessage(String.valueOf(itemPosition),threshold, patientName);


                //Toast.makeText(context, "Update Threshold to " + threshold + " for patient " + patientId, Toast.LENGTH_SHORT).show();


                //retrieveMessage("button");
            } else
                Log.d(TAG, "Good Effort");
        }

        private void retrieveMessage(String pos, String threshold, String patientName) {
            Intent intent = new Intent();
            intent.setAction(UPDARTETHRESHOLD);
            intent.putExtra("itemPosition", pos);
            intent.putExtra("threshold", threshold);
            intent.putExtra("patientName", patientName);
            intent.putExtra("ID", patientId);

            Log.d("UnoDos","broadcast sent");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }



}