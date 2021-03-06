package com.example.healthmonitoring;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

//import static android.support.v4.widget.EdgeEffectCompatIcs.finish;
//import static java.security.AccessController.getContext;

/**
 * Created by rtrev on 11/21/2016.
 */

public class RecyclerAdapterPatientAlertsDoctor extends RecyclerView.Adapter<RecyclerAdapterPatientAlertsDoctor.ViewHolder> {

    static Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemPatientName;
        public TextView itemDate;
        public TextView itemThreshold;
        public TextView itemHeartRate;
        public TextView itemPatientEmail;
        public View container;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            itemPatientName = (TextView) itemView.findViewById(R.id.tv_Patient_Name);
            itemDate = (TextView) itemView.findViewById(R.id.tv_Date);
            itemThreshold = (TextView) itemView.findViewById(R.id.tv_Current_Threshold);
            itemPatientEmail = (TextView) itemView.findViewById(R.id.tv_Patient_Id);
            itemHeartRate = (TextView) itemView.findViewById(R.id.tv_Heart_Rate);


        }
    }

    List<PatientAlert> patientAlert;

    RecyclerAdapterPatientAlertsDoctor(List<PatientAlert> patientAlert){
        this.patientAlert = patientAlert;
    }


    public RecyclerAdapterPatientAlertsDoctor(Context context) {
    }


    /*public void userItemClick(int pos) {
        Toast.makeText(RecyclerAdapterPatientAlertsDoctor.this, "Clicked User : " + .get(pos).name, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public RecyclerAdapterPatientAlertsDoctor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_alerts_doctors, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(v);
        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterPatientAlertsDoctor.ViewHolder viewHolder, final int position) {
        viewHolder.itemPatientName.setText(patientAlert.get(position).name);
        viewHolder.itemPatientEmail.setText(patientAlert.get(position).username);
        viewHolder.itemThreshold.setText(patientAlert.get(position).threshold);
        viewHolder.itemDate.setText(patientAlert.get(position).date);
        viewHolder.itemHeartRate.setText(patientAlert.get(position).heartRate);

        String email = "Stevenjoy99@yahoo.com";

        viewHolder.itemPatientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent i = new Intent (view.getContext(), EmailClass.class);
                        i.putExtra("patientEmail",patientAlert.get(position).username);
                        i.putExtra("patientName",patientAlert.get(position).name);
                        i.putExtra("patientHeartRate",patientAlert.get(position).heartRate);
                        i.putExtra("date", patientAlert.get(position).date);
                        view.getContext().startActivity(i);
            }
                 //   Toast.makeText(view.getContext(), "Recycle Click" + position, Toast.LENGTH_SHORT).show();

                });



        viewHolder.itemPatientName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "Recycle Click" + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }




    @Override
    public int getItemCount() {
        return patientAlert.size();
    }


}