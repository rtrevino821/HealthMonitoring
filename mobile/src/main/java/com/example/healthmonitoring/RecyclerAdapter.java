package com.example.healthmonitoring;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rtrev on 10/23/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;
   /* private ArrayList<String> mData;

    private AdapterView.OnItemClickListener mOnItemClickListener;*/


    public class ViewHolder extends RecyclerView.ViewHolder{
       // View container;
        public int currentItem;
        public TextView itemReading;
        public TextView itemDate;
        public TextView itemTime;
        ;
        public ViewHolder(View itemView) {
            super(itemView);

          //  container = itemView;
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
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder,final int position) {
        Log.d("OnBind", heartData.get(position).timestamp);
        Log.d("OnBind", heartData.get(position).heartRate);
        Log.d("OnBind", heartData.get(position).date);
        viewHolder.itemTime.setText(heartData.get(position).timestamp);
        viewHolder.itemReading.setText(heartData.get(position).heartRate);
        viewHolder.itemDate.setText(heartData.get(position).date);

    }

    @Override
    public int getItemCount() {
        return heartData.size();
    }


}