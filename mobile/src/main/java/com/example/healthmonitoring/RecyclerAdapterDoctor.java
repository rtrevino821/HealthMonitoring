package com.example.healthmonitoring;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
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

                        Intent i = new Intent(view.getContext(), ChangeThreshold.class);
                        i.putExtra("patientThreshold", m_Text[0]);
                        i.putExtra("patientId", patientDoctors.get(position).patientID);
                        view.getContext().startActivity(i);
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

    @Override
    public int getItemCount() {
        return patientDoctors.size();
    }

}