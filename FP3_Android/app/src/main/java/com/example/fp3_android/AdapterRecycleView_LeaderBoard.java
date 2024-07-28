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

public class AdapterRecycleView_LeaderBoard extends RecyclerView.Adapter<AdapterRecycleView_LeaderBoard.ViewHolder> {


    Context context;
    LeaderBoard fragment;

    ArrayList<LeaderBordClass> arr_LeaderBord=null;


    public AdapterRecycleView_LeaderBoard(Context context, LeaderBoard fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderbord_recycleview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtPosition.setText(arr_LeaderBord.get(position).getPosition());
        holder.txtEmail.setText(arr_LeaderBord.get(position).getEmail());
        holder.txtSteps.setText("Steps: "+arr_LeaderBord.get(position).getSteps());
    }

    @Override
    public int getItemCount() {
        return arr_LeaderBord.size();
    }


    public void setArr_LeaderBord(ArrayList<LeaderBordClass> arr_LeaderBord) {
        this.arr_LeaderBord = arr_LeaderBord;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout parent;
        TextView txtPosition,txtEmail,txtSteps;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            txtPosition=itemView.findViewById(R.id.txtPosition);
            txtEmail=itemView.findViewById(R.id.txtEmail);
            txtSteps=itemView.findViewById(R.id.txtSteps);

        }
    }


}
