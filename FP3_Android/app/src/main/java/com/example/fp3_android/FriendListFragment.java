package com.example.fp3_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_list, container, false);
    }

    RecyclerView recyclerViewFriendList;
    ArrayList<FriendListClass> arr_friendList;
    ValueEventListener valueEventListener;
    private FirebaseAuth mAuth;



    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        arr_friendList=new ArrayList<>();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        /*if(currentUser==null){
            getActivity().startActivity(new Intent(getContext(),Login_Create_Activity.class));
            getActivity().finish();
            return;
        }*/

        recyclerViewFriendList=view.findViewById(R.id.parent_recycle_view_friendlist);
        MaterialButtonToggleGroup toggleGroup=view.findViewById(R.id.togglebtnGroup1);

        Button btnToggle1=view.findViewById(R.id.toggleBtn1);
        Button btnToggle2=view.findViewById(R.id.toggleBtn2);
        Button btnToggle3=view.findViewById(R.id.toggleBtn3);
        toggleGroup.check(btnToggle1.getId());

        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    //Toast.makeText(getContext(), , Toast.LENGTH_SHORT).show();
                    if(checkedId==btnToggle1.getId()){
                        //Empty
                    }else if(checkedId==btnToggle2.getId()){
                        //Toast.makeText(getContext(), "Button2", Toast.LENGTH_SHORT).show();
                        FriendAddFragment fragment_addFriend=new FriendAddFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment_addFriend).commit();

                    }else if(checkedId==btnToggle3.getId()){
                        //Toast.makeText(getContext(), "Button3", Toast.LENGTH_SHORT).show();
                        FriendRequestFragment notificationFragment=new FriendRequestFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,notificationFragment).commit();
                    }
                }else{
                    toggleGroup.check(btnToggle1.getId());
                }
            }
        });


        showFriendList();



    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference().child("friends").removeEventListener(valueEventListener);
    }


    public  void showFriendList(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            return;
        }
        String uid_usercurrent=currentUser.getUid().toString();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arr_friendList.clear();

                if(snapshot.exists()){
                    for (DataSnapshot childSnapshot : snapshot.getChildren()){
                        if( !(childSnapshot.child("type_friend").getValue(String.class).equals("1"))) {
                            continue;
                        }

                        String uid1=childSnapshot.child("uid_user1").getValue(String.class);
                        String uid2=childSnapshot.child("uid_user2").getValue(String.class);
                        if(uid_usercurrent.equals(uid1)  || uid_usercurrent.equals(uid2) ){
                            if(uid_usercurrent.equals(uid1)){
                                arr_friendList.add(new FriendListClass(childSnapshot.getKey().toString(),uid2));
                            }else{
                                arr_friendList.add(new FriendListClass(childSnapshot.getKey().toString(),uid1));
                            }
                        }

                    }



                    for(int i=0;i<arr_friendList.size();i++){
                        final int i_global=i;
                        FirebaseDatabase.getInstance().getReference().child("users").child(arr_friendList.get(i_global).getUid_userfriend()).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String name_user=snapshot.child("username").getValue(String.class);
                                            String email_user=snapshot.child("email").getValue(String.class);

                                            Log.d("Friend_ListFragment123","UserName: "+name_user+" Email: "+email_user);
                                            //String birthday_user=snapshot.child("dateOfBirthday").getValue(String.class);

                                            arr_friendList.get(i_global).setUsername(name_user);
                                            arr_friendList.get(i_global).setEmail(email_user);
                                            //arr_friendList.get(i_global).setBirthday(birthday_user);


                                            if(i_global+1==arr_friendList.size()){
                                                AdapterRecycleView_FriendList adapter=new AdapterRecycleView_FriendList(getContext(),FriendListFragment.this,uid_usercurrent);
                                                adapter.setFriendList_Arr(arr_friendList);
                                                recyclerViewFriendList.setAdapter(adapter);
                                                recyclerViewFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }

                    if(arr_friendList.size()==0){
                        AdapterRecycleView_FriendList adapter=new AdapterRecycleView_FriendList(getContext(),FriendListFragment.this,uid_usercurrent);
                        adapter.setFriendList_Arr(arr_friendList);
                        recyclerViewFriendList.setAdapter(adapter);
                        recyclerViewFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        FirebaseDatabase.getInstance().getReference().child("friends").addValueEventListener(valueEventListener);


    }



}