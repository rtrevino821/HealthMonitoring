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
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;

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

    List<HeartData> heartData;

    RecyclerAdapter(List<HeartData> heartData){
        this.heartData = heartData;
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
        viewHolder.itemTime.setText(heartData.get(position).timestamp);
        viewHolder.itemReading.setText(heartData.get(position).heartRate);
        viewHolder.itemDate.setText(heartData.get(position).date);

    }

    @Override
    public int getItemCount() {
        return heartData.size();
    }


}