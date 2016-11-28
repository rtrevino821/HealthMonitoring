package com.example.healthmonitoring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    public void onBindViewHolder(final RecyclerAdapterDoctor.ViewHolder viewHolder, final int position) {
        viewHolder.itemPatientName.setText(patientDoctors.get(position).name);
        viewHolder.itemLastVisit.setText("Patient ID: " + patientDoctors.get(position).patientID);
        viewHolder.itemThreshold.setText(patientDoctors.get(position).threshold);

        viewHolder.itemThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                    final String[] m_Text = {""};
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Please enter new Threshold");
                    //(R.layout.activity_change_threshold);
                    final EditText input = new EditText(view.getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text[0] = input.getText().toString();
                            Log.d("TAG", m_Text[0]);

                            Intent i = new Intent(view.getContext(), ChangeThreshold.class);
                            i.putExtra("patientThreshold", m_Text[0]);
                            i.putExtra("patientId", patientDoctors.get(position).patientID);
                            view.getContext().startActivity(i);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();



                //Intent i = new Intent(view.getContext(), ChangeThreshold.class);
               // i.putExtra("patientThreshold", patientDoctors.get(position).threshold);

                //view.getContext().startActivity(i);
            }
            //   Toast.makeText(view.getContext(), "Recycle Click" + position, Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    public int getItemCount() {
        return patientDoctors.size();
    }

}