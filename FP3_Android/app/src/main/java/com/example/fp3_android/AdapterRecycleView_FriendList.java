package com.example.fp3_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterRecycleView_FriendList extends RecyclerView.Adapter<AdapterRecycleView_FriendList.ViewHolder> {

    Context context;
    FriendListFragment friendListFragment;
    String uid;
    ArrayList<FriendListClass> friendList_Arr;

    public AdapterRecycleView_FriendList(Context context, FriendListFragment friendListFragment, String uid) {
        this.context = context;
        this.friendListFragment = friendListFragment;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friendlist_recycleview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.txtEmail.setText("Email...");
        holder.txtEmail.setText(friendList_Arr.get(position).getEmail());
        holder.txtName.setText(friendList_Arr.get(position).getUsername());

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRemoveFriend(position);
            }
        });

        holder.btnInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Info User", Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("username",holder.txtName.getText().toString());
                bundle.putString("email",holder.txtEmail.getText().toString());

                ScheduleEventFragment scheduleEventFragment=new ScheduleEventFragment();
                scheduleEventFragment.setArguments(bundle);
                friendListFragment.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,scheduleEventFragment).commit();
            }
        });




    }


    @Override
    public int getItemCount() {
        return friendList_Arr.size();
    }

    public void setFriendList_Arr(ArrayList<FriendListClass> friendList_Arr) {
        this.friendList_Arr = friendList_Arr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout parent;
        TextView txtEmail,txtName;
        ImageButton btnRemove,btnInfoUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            txtEmail=itemView.findViewById(R.id.txtEmail);
            txtName=itemView.findViewById(R.id.txtUserName);
            btnRemove=itemView.findViewById(R.id.btnRemoveFriend);
            btnInfoUser=itemView.findViewById(R.id.btnViewInfoUser);
        }

    }



    public void btnRemoveFriend(int position){
        FirebaseDatabase.getInstance().getReference().child("friends")
                .child( friendList_Arr.get(position).getId_friendList() ).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            String uid_user1=snapshot.child("uid_user1").getValue(String.class);
                            String uid_user2=snapshot.child("uid_user2").getValue(String.class);
                            //String user_send_request=snapshot.child("user_send_request").getValue(String.class);

                            FirebaseDatabase.getInstance().getReference().child("friends")
                                    .child( friendList_Arr.get(position).getId_friendList() )
                                    .setValue(new FriendsDB_Branch(uid_user1,uid_user2,"0",""))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //User accepted request
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}
