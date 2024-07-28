package com.example.fp3_android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterRecycleView_ScheduleEventRequest extends RecyclerView.Adapter<AdapterRecycleView_ScheduleEventRequest.ViewHolder> {

    Context context;
    ScheduleEventRequest fragment;

    ArrayList<ScheduleEventClass> schdeluEventReq_arr;

    public AdapterRecycleView_ScheduleEventRequest(Context context, ScheduleEventRequest fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragmentscheduler_equest_recycleview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtEmail.setText(schdeluEventReq_arr.get(position).getEmail());
        holder.txtDate.setText(schdeluEventReq_arr.get(position).getDate());
        holder.txtHour.setText( schdeluEventReq_arr.get(position).getHour() );
        holder.txtAdress.setText(schdeluEventReq_arr.get(position).getAdress());

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Accept", Toast.LENGTH_SHORT).show();
                //Log.d("AdapterRecycleView_ScheduleReuqestEvent123","Key:"+schdeluEventReq_arr.get(position).getKey());
                FirebaseDatabase.getInstance().getReference().child("events").child(schdeluEventReq_arr.get(position).getKey())
                        .child("type_request").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Success
                                }
                            }
                        });
            }
        });


        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Decline", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("events").child(schdeluEventReq_arr.get(position).getKey())
                        .child("type_request").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Success
                                }
                            }
                        });
            }
        });


    }

    @Override
    public int getItemCount() {
        return schdeluEventReq_arr.size();
    }

    public void setSchdeluEventReq_arr(ArrayList<ScheduleEventClass> schdeluEventReq_arr) {
        this.schdeluEventReq_arr = schdeluEventReq_arr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout parent;
        TextView txtEmail,txtAdress,txtDate,txtHour;
        ImageButton btnAccept,btnDecline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            txtEmail=itemView.findViewById(R.id.txtEmail);
            txtDate=itemView.findViewById(R.id.txtDate);
            txtHour=itemView.findViewById(R.id.txtHour);
            txtAdress=itemView.findViewById(R.id.txtAdress);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnDecline=itemView.findViewById(R.id.btnDecline);
        }
    }





}
