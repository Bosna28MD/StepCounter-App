package com.example.fp3_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRecyclerView_StepsWeeklyReport extends RecyclerView.Adapter<AdapterRecyclerView_StepsWeeklyReport.ViewHolder> {

    Context context;
    StepsWeeklyReportFragment fragment;
    ArrayList<StepsWeeklyReport_Class> arr_stepsWeekly=null;


    public AdapterRecyclerView_StepsWeeklyReport(Context context, StepsWeeklyReportFragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stepsweeklyreport_recyclerview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtPosition.setText(arr_stepsWeekly.get(position).getPosition());
        holder.txtDay.setText("Day: "+arr_stepsWeekly.get(position).getDay());
        holder.txtSteps.setText("Steps: "+arr_stepsWeekly.get(position).getSteps());
    }

    @Override
    public int getItemCount() {
        return arr_stepsWeekly.size();
    }

    public void setArr_stepsWeekly(ArrayList<StepsWeeklyReport_Class> arr_stepsWeekly) {
        this.arr_stepsWeekly = arr_stepsWeekly;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout parent;
        TextView txtPosition,txtDay,txtSteps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            txtPosition=itemView.findViewById(R.id.txtPosition);
            txtDay=itemView.findViewById(R.id.txtDay);
            txtSteps=itemView.findViewById(R.id.txtSteps);
        }
    }

}
