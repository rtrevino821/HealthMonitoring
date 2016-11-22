package com.example.healthmonitoring;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rtrev on 11/21/2016.
 */

public class RecyclerAdapterPatientAlertsDoctor extends RecyclerView.Adapter<RecyclerAdapterPatientAlertsDoctor.ViewHolder> {

    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemPatientName;
        public TextView itemDate;
        public TextView itemThreshold;
        public TextView itemHeartRate;
        public TextView itemPatientId;

        public ViewHolder(View itemView) {
            super(itemView);
            itemPatientName = (TextView) itemView.findViewById(R.id.tv_Patient_Name);
            itemDate = (TextView) itemView.findViewById(R.id.tv_Date);
            itemThreshold = (TextView) itemView.findViewById(R.id.tv_Current_Threshold);
            itemPatientId = (TextView) itemView.findViewById(R.id.tv_Patient_Id);
            itemHeartRate = (TextView) itemView.findViewById(R.id.tv_Heart_Rate);

        }
    }

    List<PatientAlert> patientAlert;

    RecyclerAdapterPatientAlertsDoctor(List<PatientAlert> patientAlert){
        this.patientAlert = patientAlert;
    }


    public RecyclerAdapterPatientAlertsDoctor(Context context) {
    }



    @Override
    public RecyclerAdapterPatientAlertsDoctor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_alerts_doctors, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(v);
        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterPatientAlertsDoctor.ViewHolder viewHolder, int position) {
        viewHolder.itemPatientName.setText(patientAlert.get(position).name);
        viewHolder.itemPatientId.setText("Patient ID: " + patientAlert.get(position).patientID);
        viewHolder.itemThreshold.setText(patientAlert.get(position).threshold);
        viewHolder.itemDate.setText(patientAlert.get(position).date);
        viewHolder.itemHeartRate.setText(patientAlert.get(position).heartRate);
    }

    @Override
    public int getItemCount() {
        return patientAlert.size();
    }


}