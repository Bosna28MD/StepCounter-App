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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterRecycleView_FriendRequest  extends RecyclerView.Adapter<AdapterRecycleView_FriendRequest.ViewHolder> {

    Context context;
    FriendRequestFragment friendRequestFragment;
    String uid;
    ArrayList<FriendListClass> friendList_Arr;

    public AdapterRecycleView_FriendRequest(Context context, FriendRequestFragment friendRequestFragment, String uid) {
        this.context = context;
        this.friendRequestFragment = friendRequestFragment;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friendrequest_recycleview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtEmail.setText(friendList_Arr.get(position).getEmail());
        holder.txtName.setText(friendList_Arr.get(position).getUsername());

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAcceptRequest(position);
            }
        });

        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDeclineRequest(position);
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
        ImageButton btnAccept,btnDecline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            txtEmail=itemView.findViewById(R.id.txtEmail);
            txtName=itemView.findViewById(R.id.txtUserName);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnDecline=itemView.findViewById(R.id.btnDecline);
        }

    }



    public void btnAcceptRequest(int position){
        FirebaseDatabase.getInstance().getReference().child("friends")
                .child( friendList_Arr.get(position).getId_friendList() ).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            String uid_user1=snapshot.child("uid_user1").getValue(String.class);
                            String uid_user2=snapshot.child("uid_user2").getValue(String.class);
                            String user_send_request=snapshot.child("user_send_request").getValue(String.class);

                            FirebaseDatabase.getInstance().getReference().child("friends")
                                    .child( friendList_Arr.get(position).getId_friendList() )
                                    .setValue(new FriendsDB_Branch(uid_user1,uid_user2,"1",user_send_request))
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



    public void btnDeclineRequest(int position){
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
