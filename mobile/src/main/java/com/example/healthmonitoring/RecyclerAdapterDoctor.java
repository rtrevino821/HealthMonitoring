package com.example.healthmonitoring;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rtrev on 10/23/2016.
 */
public class RecyclerAdapterDoctor extends RecyclerView.Adapter<RecyclerAdapterDoctor.ViewHolder> {

    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemPatientName;
        public TextView itemLastVisit;
        public TextView itemThreshold;

        public ViewHolder(View itemView) {
            super(itemView);
            itemPatientName = (TextView) itemView.findViewById(R.id.tv_Patient_Name);
            itemLastVisit = (TextView) itemView.findViewById(R.id.tv_Last_Visit);
            itemThreshold = (TextView) itemView.findViewById(R.id.tv_Current_Threshold);

        }
    }

    List<PatientDoctor> patientDoctors;

    RecyclerAdapterDoctor(List<PatientDoctor> patientDoctors){
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
    public void onBindViewHolder(RecyclerAdapterDoctor.ViewHolder viewHolder, int position) {
        viewHolder.itemPatientName.setText(patientDoctors.get(position).name);
        viewHolder.itemLastVisit.setText("Last Visit: " + patientDoctors.get(position).lastVisit);
        viewHolder.itemThreshold.setText(patientDoctors.get(position).threshold);
    }

    @Override
    public int getItemCount() {
        return patientDoctors.size();
    }


}