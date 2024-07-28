package com.example.fp3_android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class RegistrationLeaderBoard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_leader_board, container, false);
    }

    Button btnToggle1,btnToggle2,btnRegisterLeaderBoard;
    TextView txt_time,txt_registerAlready;
    Handler handler=new Handler();

    //ValueEventListener valueEventListener;
    public volatile boolean shouldStop=false;
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_time=view.findViewById(R.id.txt_time);
        txt_registerAlready=view.findViewById(R.id.txt_RegisteredAlready);
        btnRegisterLeaderBoard=view.findViewById(R.id.btn_RegisterLeadBord);

        txt_registerAlready.setVisibility(View.GONE);
        btnRegisterLeaderBoard.setVisibility(View.GONE);

        MaterialButtonToggleGroup toggleGroup=view.findViewById(R.id.togglebtnGroup1);
        btnToggle1=view.findViewById(R.id.toggleBtn1);
        btnToggle2=view.findViewById(R.id.toggleBtn2);


        toggleGroup.check(btnToggle1.getId());
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    if(checkedId==btnToggle1.getId()){
                        //Fragment SingLeaderBord
                    }else if(checkedId==btnToggle2.getId()){
                        replaceFragment(new LeaderBoard());
                    }
                }else{
                    toggleGroup.check(btnToggle1.getId());
                }
            }
        });


        checkIfUserRegisteredLeaderBord();
        btnRegisterLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerToLeaderBord();
            }
        });

        //btnRegisterLeaderBoard.setVisibility(View.GONE);




    }


    public void createThreadTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    LocalTime localTime_startDay = LocalTime.of(0,0,0);

                    //LocalDateTime localDateTime2=LocalDateTime.of()
                    LocalDateTime localDateTime_Now=LocalDateTime.now(); //Time-Date Now
                    LocalDateTime localDate_nextMonday=LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)),localTime_startDay);

                    long minutes= ChronoUnit.MINUTES.between(localDateTime_Now, localDate_nextMonday)%60;
                    long hours=ChronoUnit.HOURS.between(localDateTime_Now, localDate_nextMonday)%24;
                    long seconds=ChronoUnit.SECONDS.between(localDateTime_Now, localDate_nextMonday)%60;
                    long days=ChronoUnit.DAYS.between(localDateTime_Now, localDate_nextMonday);

                    Log.d("Date_Time123","Difference: "+days+" "+hours+":"+minutes+":"+seconds);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            txt_time.setText(days+" days - Time: "+hours+":"+minutes+":"+seconds);
                        }
                    });

                    if(!shouldStop){
                        handler.postDelayed(this,1000);
                    }
                }

                //handler.postDelayed(this,1000);

            }
        }).start();

    }


    @Override
    public void onStart() {
        super.onStart();
        set_False_shouldStop();
        createThreadTime();
        //Toast.makeText(getContext(), "Start123", Toast.LENGTH_SHORT).show();
        /*new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        */
    }


    @Override
    public void onPause() {
        super.onPause();
        set_True_shouldStop();
        //Toast.makeText(getContext(), "Pause123", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        set_True_shouldStop();
        //Toast.makeText(getContext(), "Destroy123", Toast.LENGTH_SHORT).show();

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    public void set_True_shouldStop(){
        shouldStop=true;
    }
    public void set_False_shouldStop(){
        shouldStop=false;
    }

    public void registerToLeaderBord(){
        String uid_currentUser= FirebaseAuth.getInstance().getUid().toString();
        long timeMiliseconds_nextMonday=returnMiliseconds_nextMonday();

        FirebaseDatabase.getInstance().getReference().child("leader_bord").child(String.valueOf(timeMiliseconds_nextMonday))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int new_key=((int)snapshot.getChildrenCount()+1);
                            FirebaseDatabase.getInstance().getReference().child("leader_bord").child(String.valueOf(timeMiliseconds_nextMonday))
                                    .child(String.valueOf(new_key)).setValue(uid_currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //User register leaderbord
                                                txt_registerAlready.setVisibility(View.VISIBLE);
                                                btnRegisterLeaderBoard.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                        }else{
                            FirebaseDatabase.getInstance().getReference().child("leader_bord").child(String.valueOf(timeMiliseconds_nextMonday))
                                    .child("1").setValue(uid_currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //User register leaderbord
                                                txt_registerAlready.setVisibility(View.VISIBLE);
                                                btnRegisterLeaderBoard.setVisibility(View.GONE);
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



    public void checkIfUserRegisteredLeaderBord(){
        String uid_currentUser= FirebaseAuth.getInstance().getUid().toString();
        long timeMiliseconds_nextMonday=returnMiliseconds_nextMonday();

        //Log.d("RegistrationLeaderBoard123","TimeMiliseconds:"+timeMiliseconds_nextMonday);

        FirebaseDatabase.getInstance().getReference().child("leader_bord").child(String.valueOf(timeMiliseconds_nextMonday))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            boolean checkExist=false;
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                //Log.d("RegistrationLeaderBoard123",dataSnapshot.getValue(String.class));
                                if(dataSnapshot.getValue(String.class).equals(uid_currentUser)){
                                    checkExist=true;
                                    break;
                                }
                            }

                            if(checkExist){
                                txt_registerAlready.setVisibility(View.VISIBLE);
                                btnRegisterLeaderBoard.setVisibility(View.GONE);
                            }else{
                                txt_registerAlready.setVisibility(View.GONE);
                                btnRegisterLeaderBoard.setVisibility(View.VISIBLE);
                            }

                        }else{
                            txt_registerAlready.setVisibility(View.GONE);
                            btnRegisterLeaderBoard.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //FirebaseDatabase.getInstance().getReference().child("").addValueEventListener(valueEventListener);
    }


    public long returnMiliseconds_nextMonday(){
        LocalTime localTime_startDay = LocalTime.of(0,0,0);

        LocalDateTime localDate_nextMonday=LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)),localTime_startDay);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime_local_time = localDate_nextMonday.atZone(zoneId);
        long timeMiliseconds_nextMonday=zonedDateTime_local_time.toInstant().toEpochMilli();

        return timeMiliseconds_nextMonday;
    }





}

