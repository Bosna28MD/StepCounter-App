package com.example.fp3_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class AdapterRecycleView_FriendAdd extends RecyclerView.Adapter<AdapterRecycleView_FriendAdd.ViewHolder> {

    Context context;
    FriendAddFragment friendAddFragment;
    String uid;
    ArrayList<FriendListClass> friendList_Arr;

    public AdapterRecycleView_FriendAdd(Context context, FriendAddFragment friendAddFragment, String uid) {
        this.context = context;
        this.friendAddFragment = friendAddFragment;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friendadd_recycleview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtEmail.setText(friendList_Arr.get(position).getEmail());
        holder.txtName.setText(friendList_Arr.get(position).getUsername());

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddFriend(position);
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
        Button btnAdd;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            txtEmail=itemView.findViewById(R.id.txtEmail);
            txtName=itemView.findViewById(R.id.txtUserName);
            btnAdd=itemView.findViewById(R.id.btnAdd);
        }

    }


    public void btnAddFriend(int position){
        FirebaseDatabase.getInstance().getReference().child("friends")
                .child( friendList_Arr.get(position).getId_friendList() ).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String uid_user1=snapshot.child("uid_user1").getValue(String.class);
                            String uid_user2=snapshot.child("uid_user2").getValue(String.class);

                            FirebaseDatabase.getInstance().getReference().child("friends")
                                    .child(friendList_Arr.get(position).getId_friendList())
                                    .setValue(new FriendsDB_Branch(uid_user1,uid_user2,"-1",AdapterRecycleView_FriendAdd.this.uid)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //User Added
                                            }
                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    /*
        FirebaseDatabase.getInstance().getReference().child("friends").child(friendList_Arr.get(position).getId_friendList())
                .setValue(new FriendListClass(id1,id2,"-1",id_user_current)).addOnCompleteListener
        */
    }



}
