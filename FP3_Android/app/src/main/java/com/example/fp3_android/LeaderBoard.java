package com.example.fp3_android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;

interface CheckUserRegistLeaderBoardCallBack{
    void checkUserRegister(boolean checkRegister);
}

public class LeaderBoard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leader_board, container, false);
    }

    Button btnToggle1,btnToggle2;
    RecyclerView recyclerView;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=view.findViewById(R.id.parent_recycle_view_leaderBord);

        MaterialButtonToggleGroup toggleGroup=view.findViewById(R.id.togglebtnGroup1);
        btnToggle1=view.findViewById(R.id.toggleBtn1);
        btnToggle2=view.findViewById(R.id.toggleBtn2);

        toggleGroup.check(btnToggle2.getId());
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    if(checkedId==btnToggle1.getId()){
                        replaceFragment(new RegistrationLeaderBoard());
                    }else if(checkedId==btnToggle2.getId()){
                        //LeaderBord Fragment
                    }
                }else{
                    toggleGroup.check(btnToggle2.getId());
                }
            }
        });


        showLeaderBordUsers();

    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


    public void showLeaderBordUsers(){
        long previousMonday=returnMiliseconds_previousMonday();
        long nextMonday=returnMiliseconds_nextMonday();
        Log.d("LeaderBoard123","Previous Monday Miliseconds:"+String.valueOf(previousMonday));
        //FirebaseDatabase.getInstance().getReference().child("leader_bord").child(String.valueOf(previousMonday))

        String uid_currentUser=FirebaseAuth.getInstance().getUid().toString();


        FirebaseDatabase.getInstance().getReference().child("leader_bord").child(String.valueOf(previousMonday))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            ArrayList<LeaderBordClass> arr_LeaderBord=new ArrayList<>();

                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                arr_LeaderBord.add( new LeaderBordClass(dataSnapshot.getValue(String.class).toString()) );
                            }



                            for(int i=0;i<arr_LeaderBord.size();i++){
                                final int i_global=i;
                                FirebaseDatabase.getInstance().getReference().child("steps_db").child( arr_LeaderBord.get(i).getUid() )
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    int steps_tot=0;
                                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                        long steps_miliseconds_db=Long.parseLong(dataSnapshot.getKey().toString());
                                                        if(previousMonday<steps_miliseconds_db &&  nextMonday>steps_miliseconds_db ){
                                                            steps_tot+= Integer.valueOf( dataSnapshot.child("steps").getValue(String.class) );
                                                        }

                                                    }
                                                    arr_LeaderBord.get(i_global).setSteps(String.valueOf(steps_tot));
                                                    //Log.d("LeaderBoard123","User Uid:"+arr_LeaderBord.get(i_global).getUid()+" total_steps:"+steps_tot);
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(arr_LeaderBord.get(i_global).getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if(snapshot.exists()){
                                                                        arr_LeaderBord.get(i_global).setEmail(snapshot.child("email").getValue(String.class));
                                                                        //Log.d("LeaderBoard123","Email:"+arr_LeaderBord.get(i_global).getEmail()+" UID:"+arr_LeaderBord.get(i_global).getUid()+" Steps:"+arr_LeaderBord.get(i_global).getSteps());
                                                                        if( (i_global+1)==arr_LeaderBord.size()){
                                                                            Collections.sort(arr_LeaderBord, new StepComparatorClass());

                                                                            for(int i=0;i<arr_LeaderBord.size();i++){
                                                                                arr_LeaderBord.get(i).setPosition(String.valueOf(i+1));
                                                                                //Log.d("LeaderBoard123","Position:"+arr_LeaderBord.get(i).getPosition()+" Email:"+arr_LeaderBord.get(i).getEmail()+" UID:"+arr_LeaderBord.get(i).getUid()+" Steps:"+arr_LeaderBord.get(i).getSteps());
                                                                            }
                                                                            AdapterRecycleView_LeaderBoard adapter=new AdapterRecycleView_LeaderBoard(getContext(),LeaderBoard.this);
                                                                            adapter.setArr_LeaderBord(arr_LeaderBord);
                                                                            recyclerView.setAdapter(adapter);
                                                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                            //Log.d("LeaderBoard123","Send");
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });


                                                }else{
                                                    arr_LeaderBord.get(i_global).setSteps(String.valueOf(0));//steps=0
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(arr_LeaderBord.get(i_global).getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if(snapshot.exists()){
                                                                        arr_LeaderBord.get(i_global).setEmail(snapshot.child("email").getValue(String.class));
                                                                        if( (i_global+1)==arr_LeaderBord.size()){
                                                                            Collections.sort(arr_LeaderBord, new StepComparatorClass());

                                                                            for(int i=0;i<arr_LeaderBord.size();i++){
                                                                                arr_LeaderBord.get(i).setPosition(String.valueOf(i+1));
                                                                                //Log.d("LeaderBoard123","Position:"+arr_LeaderBord.get(i).getPosition()+" Email:"+arr_LeaderBord.get(i).getEmail()+" UID:"+arr_LeaderBord.get(i).getUid()+" Steps:"+arr_LeaderBord.get(i).getSteps());
                                                                            }
                                                                            AdapterRecycleView_LeaderBoard adapter=new AdapterRecycleView_LeaderBoard(getContext(),LeaderBoard.this);
                                                                            adapter.setArr_LeaderBord(arr_LeaderBord);
                                                                            recyclerView.setAdapter(adapter);
                                                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                            //Log.d("LeaderBoard123","Send");
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }



                        }else{
                            Log.d("LeaderBoard123","Nobody registered for this leaderbord");
                            //Nobody registered for this LeaderBord
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        /*
        checkUserRegisterLedBoard(new CheckUserRegistLeaderBoardCallBack() {
            @Override
            public void checkUserRegister(boolean checkRegister) {
                if(checkRegister){
                    FirebaseDatabase.getInstance().getReference().child("steps_db").child(uid_currentUser)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        int steps=0;
                                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            //dataSnapshot.getValue(String.class);
                                            //Log.d("LeaderBoard123","LeaderBoard miliseconds:"+dataSnapshot.getKey().toString());
                                            long steps_miliseconds_db=Long.parseLong(dataSnapshot.getKey().toString());
                                            if(steps_miliseconds_db>previousMonday && steps_miliseconds_db<nextMonday ){
                                                Log.d("LeaderBoard123","Steps:"+dataSnapshot.child("steps").getValue(String.class));
                                                steps+=Integer.valueOf(dataSnapshot.child("steps").getValue(String.class));
                                                Log.d("LeaderBoard123","Key:"+dataSnapshot.getKey().toString()+" Steps:"+dataSnapshot.child("steps").getValue(String.class));
                                            }
                                        }
                                        Log.d("LeaderBoard123","Total-Steps:"+steps);

                                    }else{
                                        //User have 0 steps made
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }else{
                    //User Not registered to this weekly LeaaderBord
                }
            }
        },uid_currentUser);
        */

    }



    public void checkUserRegisterLedBoard(CheckUserRegistLeaderBoardCallBack callBack,String uid_user){
        long previousMonday=returnMiliseconds_previousMonday();
        //String uid_currentUser=FirebaseAuth.getInstance().getUid().toString();
        FirebaseDatabase.getInstance().getReference().child("leader_bord").child(String.valueOf(previousMonday))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            boolean checkExist=false;
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                if(dataSnapshot.getValue(String.class).equals(uid_user)){ //check if user Registered to this LeaderBord
                                    checkExist=true;
                                    break;
                                }
                            }

                            if(checkExist){
                                callBack.checkUserRegister(true);
                            }else{
                                callBack.checkUserRegister(false);
                            }


                        }else{
                            callBack.checkUserRegister(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



    public long returnMiliseconds_nextMonday(){
        LocalTime localTime_startDay = LocalTime.of(0,0,0);

        LocalDateTime localDate_nextMonday=LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)),localTime_startDay);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime_local_time = localDate_nextMonday.atZone(zoneId);
        long timeMiliseconds_nextMonday=zonedDateTime_local_time.toInstant().toEpochMilli();

        return timeMiliseconds_nextMonday;
    }


    public long returnMiliseconds_previousMonday(){
        LocalTime localTime_startDay = LocalTime.of(0,0,0);
        LocalDateTime localDate_previousMonday=null;
        if(LocalDateTime.now().getDayOfWeek()==DayOfWeek.MONDAY ){
            localDate_previousMonday=LocalDateTime.of(LocalDateTime.now().toLocalDate(),localTime_startDay);
        }else{
            localDate_previousMonday=LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)),localTime_startDay);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime_local_time = localDate_previousMonday.atZone(zoneId);
        long timeMiliseconds_previousMonday=zonedDateTime_local_time.toInstant().toEpochMilli();

        return timeMiliseconds_previousMonday;
    }



}