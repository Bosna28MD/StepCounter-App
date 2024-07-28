package com.example.fp3_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRecycleView_Calendar extends RecyclerView.Adapter<AdapterRecycleView_Calendar.ViewHolder> {

    Context context;
    CalendarFragment fragment;
    ArrayList<ScheduleEventClass> calendar_arr;


    public AdapterRecycleView_Calendar(Context context, CalendarFragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_recycleview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtEmail.setText(calendar_arr.get(position).getEmail());
        holder.txtDate.setText(calendar_arr.get(position).getDate());
        holder.txtHour.setText(calendar_arr.get(position).getHour());
        holder.txtAdress.setText(calendar_arr.get(position).getAdress());
    }

    @Override
    public int getItemCount() {
        return calendar_arr.size();
    }


    public void setCalendar_arr(ArrayList<ScheduleEventClass> calendar_arr) {
        this.calendar_arr = calendar_arr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout parent;
        TextView txtEmail,txtDate,txtHour,txtAdress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            txtEmail=itemView.findViewById(R.id.txtEmail);
            txtDate=itemView.findViewById(R.id.txtDate);
            txtHour=itemView.findViewById(R.id.txtHour);
            txtAdress=itemView.findViewById(R.id.txtAdress);

        }
    }


}
