package com.example.healthmonitoring;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rtrev on 10/23/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;

    private String[] reading = {"120", "80", "69", "78", "95"};
    private String[] dates = {"Monday, Nov. 20", "Tuesday, Nov.21", "Wednesday, Nov. 22", "Thursday, Nov. 23", "Friday, Nov. 24"};
    private String[] times = {"12:00 pm", "8:00 pm", "4:30 pm", "2:39 pm", "9:30 am"};

    public class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public TextView itemReading;
        public TextView itemDate;
        public TextView itemTime;

        public ViewHolder(View itemView) {
            super(itemView);
            itemReading = (TextView) itemView.findViewById(R.id.tv_History_Pulse);
            itemDate = (TextView) itemView.findViewById(R.id.tv_History_Date);
            itemTime = (TextView) itemView.findViewById(R.id.tv_History_Time);

        }
    }


    public RecyclerAdapter(Context context) {
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(v);
        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {
        viewHolder.itemTime.setText(times[position]);
        viewHolder.itemReading.setText(reading[position]);
        viewHolder.itemDate.setText(dates[position]);

    }

    @Override
    public int getItemCount() {
        return reading.length;
    }


}