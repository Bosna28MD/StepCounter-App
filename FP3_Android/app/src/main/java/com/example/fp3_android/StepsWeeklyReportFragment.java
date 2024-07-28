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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;


public class StepsWeeklyReportFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_steps_weekly_report, container, false);
    }


    Button btn1_stepsHome,btn2_stepsReportWeekly;
    RecyclerView recyclerView;
    ImageButton imgBtn_left,imgBtn_right;
    TextView txtDate;
    LocalDateTime localDateTime=null;
    long milisec_current;
    ZoneId zoneId=ZoneId.systemDefault();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView=view.findViewById(R.id.parent_recycle_view_stepsWeekly);
        MaterialButtonToggleGroup toggleGroup=view.findViewById(R.id.togglebtnGroup1);
        btn1_stepsHome=view.findViewById(R.id.toggleBtn1);
        btn2_stepsReportWeekly=view.findViewById(R.id.toggleBtn2);
        imgBtn_left=view.findViewById(R.id.imgBtn_left);
        imgBtn_right=view.findViewById(R.id.imgBtn_right);
        txtDate=view.findViewById(R.id.text_Date);

        toggleGroup.check(btn2_stepsReportWeekly.getId());
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    if(checkedId==btn1_stepsHome.getId()){
                        replaceFragment(new HomeFragment());
                    }else if(checkedId==btn2_stepsReportWeekly.getId()){
                        //StepsWeeklyReportFragment
                    }
                }else{
                   toggleGroup.check(btn2_stepsReportWeekly.getId());
                }
            }
        });

        imgBtn_right.setVisibility(View.GONE);


        /*ZoneId zoneId=ZoneId.systemDefault();
        ZonedDateTime zonedDateTime=localDateTime.atZone(zoneId);
        Instant instant=zonedDateTime.toInstant();
        long milisec=instant.toEpochMilli();
        */
        //ZoneId zoneId=ZoneId.systemDefault();

        localDateTime=LocalDateTime.now();
        ZonedDateTime zonedDateTime=localDateTime.atZone(zoneId);
        milisec_current=zonedDateTime.toInstant().toEpochMilli();


        initializeStepsReportWeekly(milisec_current);
        setText_Date(milisec_current);
        try {
            /*
            long timeMiliseconds_previousMonday=returnMiliseconds_previousMonday2(milisec_current);
            long timeMiliseconds_nextMonday=returnMiliseconds_nextMonday2(milisec_current);

            Instant instant_first = Instant.ofEpochMilli(timeMiliseconds_previousMonday);
            LocalDateTime localDateTime_first = LocalDateTime.ofInstant(instant_first, ZoneId.systemDefault());

            Instant instant_last = Instant.ofEpochMilli(timeMiliseconds_nextMonday);
            LocalDateTime localDateTime_next = LocalDateTime.ofInstant(instant_last, ZoneId.systemDefault()).minusDays(1);

            txtDate.setText("Date: "+localDateTime_first.getDayOfMonth()+"/"+localDateTime_first.getMonthValue()+"/"+localDateTime_first.getYear());
            txtDate.setText(txtDate.getText()+"  -  "+localDateTime_next.getDayOfMonth()+"/"+localDateTime_next.getMonthValue()+"/"+localDateTime_next.getYear());
            */
        }catch (Exception e){
            Log.d("StepsWeeklyFragment123",e.getMessage());
        }

        //Log.d("StepsWeeklyFragment123","Date:"+localDateTime.getDayOfMonth()+"/"+localDateTime.getMonthValue()+"/"+localDateTime.getYear());

        imgBtn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //LocalDateTime localDateTime=LocalDateTime.now();

                LocalDateTime new_localDateTime=localDateTime.plusWeeks(1);
                ZonedDateTime zonedDateTime_new=new_localDateTime.atZone(zoneId);
                long milisec_new=zonedDateTime_new.toInstant().toEpochMilli();

                /*
                ZonedDateTime zonedDateTime_old=localDateTime.atZone(zoneId);
                long milisc_old=zonedDateTime_old.toInstant().toEpochMilli();
                */

                initializeStepsReportWeekly(milisec_new);
                localDateTime=new_localDateTime;
                //txtDate.setText("Date: "+localDateTime.getDayOfMonth()+"/"+localDateTime.getMonth()+"/"+localDateTime.getYear());
                //txtDate.setText("Date: "+String.valueOf(localDateTime.getDayOfMonth())+"/"+String.valueOf(localDateTime.getMonthValue())+"/"+String.valueOf(localDateTime.getYear()));
                setText_Date(milisec_new);

                if(milisec_new==milisec_current){
                    imgBtn_right.setVisibility(View.GONE);
                }

            }
        });


        imgBtn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LocalDateTime new_localDateTime=localDateTime.minusWeeks(1);
                ZonedDateTime zonedDateTime_new=new_localDateTime.atZone(zoneId);
                long milisec_new=zonedDateTime_new.toInstant().toEpochMilli();

                initializeStepsReportWeekly(milisec_new);
                localDateTime=new_localDateTime;
                //txtDate.setText("Date: "+localDateTime.getDayOfMonth()+"/"+localDateTime.getMonth()+"/"+localDateTime.getYear());
                //txtDate.setText("Date: "+String.valueOf(localDateTime.getDayOfMonth())+"/"+String.valueOf(localDateTime.getMonthValue())+"/"+String.valueOf(localDateTime.getYear()));
                setText_Date(milisec_new);

                if(imgBtn_right.getVisibility()==View.GONE && milisec_new<milisec_current){
                    imgBtn_right.setVisibility(View.VISIBLE);
                }

            }
        });


    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


    public void initializeStepsReportWeekly(long millisec){
        //long timeMiliseconds_previousMonday=returnMiliseconds_previousMonday();
        //long timeMiliseconds_nextMonday=returnMiliseconds_nextMonday();
        long timeMiliseconds_previousMonday=returnMiliseconds_previousMonday2(millisec);
        long timeMiliseconds_nextMonday=returnMiliseconds_nextMonday2(millisec);

        //Log.d("StepsWeeklyFragment123","Prev:"+timeMiliseconds_previousMonday+" Next:"+timeMiliseconds_nextMonday);
        ArrayList<StepsWeeklyReport_Class> arr_stepsWeekly=new ArrayList<>();
        String uid_currentUser= FirebaseAuth.getInstance().getUid().toString();
        //Log.d("StepsWeeklyFragment123","Time_PreviousMonday= "+timeMiliseconds_previousMonday+" Time_NextMonday= "+timeMiliseconds_nextMonday);

        FirebaseDatabase.getInstance().getReference().child("steps_db").child(uid_currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int i=1;
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        long miliseconds_db=Long.parseLong(dataSnapshot.getKey().toString());
                        if(miliseconds_db>timeMiliseconds_previousMonday && miliseconds_db<timeMiliseconds_nextMonday){
                            String day=getDayOfWeek(miliseconds_db);
                            arr_stepsWeekly.add(new StepsWeeklyReport_Class(String.valueOf(i),day,dataSnapshot.child("steps").getValue(String.class)));
                            //Log.d("StepsWeeklyFragment123","Time-"+i+" : "+miliseconds_db+" "+day+" "+dataSnapshot.child("steps").getValue(String.class));
                            i++;
                        }

                    }
                    AdapterRecyclerView_StepsWeeklyReport adapter=new AdapterRecyclerView_StepsWeeklyReport(getContext(),StepsWeeklyReportFragment.this);
                    adapter.setArr_stepsWeekly(arr_stepsWeekly);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                }else{
                    Toast.makeText(getContext(), "No data(steps) this week ", Toast.LENGTH_SHORT).show();
                    //User doesn't no steps initialized in db
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public long returnMiliseconds_previousMonday(){
        LocalTime localTime_startDay = LocalTime.of(0,0,0);
        LocalDateTime localDate_previousMonday=null;
        if(LocalDateTime.now().getDayOfWeek()== DayOfWeek.MONDAY ){
            localDate_previousMonday=LocalDateTime.of(LocalDateTime.now().toLocalDate(),localTime_startDay);
        }else{
            localDate_previousMonday=LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)),localTime_startDay);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime_local_time = localDate_previousMonday.atZone(zoneId);
        long timeMiliseconds_previousMonday=zonedDateTime_local_time.toInstant().toEpochMilli();

        return timeMiliseconds_previousMonday;
    }



    public long returnMiliseconds_nextMonday(){
        LocalTime localTime_startDay = LocalTime.of(0,0,0);

        LocalDateTime localDate_nextMonday=LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)),localTime_startDay);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime_local_time = localDate_nextMonday.atZone(zoneId);
        long timeMiliseconds_nextMonday=zonedDateTime_local_time.toInstant().toEpochMilli();

        return timeMiliseconds_nextMonday;
    }




    public static long returnMiliseconds_previousMonday2(long millis){
        LocalTime localTime_startDay = LocalTime.of(0,0,0);
        //LocalDateTime localDateTime=LocalDateTime.
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime localDateTime_required = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDateTime localDate_previousMonday=null;
        if(localDateTime_required.getDayOfWeek()== DayOfWeek.MONDAY ){
            localDate_previousMonday=LocalDateTime.of(localDateTime_required.toLocalDate(),localTime_startDay);
        }else{
            //LocalDate localDate_required=instant.atZone(ZoneId.systemDefault()).toLocalDate();
            localDate_previousMonday=LocalDateTime.of(localDateTime_required.toLocalDate().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)),localTime_startDay);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime_local_time = localDate_previousMonday.atZone(zoneId);
        long timeMiliseconds_previousMonday=zonedDateTime_local_time.toInstant().toEpochMilli();

        return timeMiliseconds_previousMonday;
    }




    public static long returnMiliseconds_nextMonday2(long millis){
        LocalTime localTime_startDay = LocalTime.of(0,0,0);

        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime localDateTime_required = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        LocalDateTime localDate_nextMonday=LocalDateTime.of(localDateTime_required.toLocalDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY)),localTime_startDay);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime_local_time = localDate_nextMonday.atZone(zoneId);
        long timeMiliseconds_nextMonday=zonedDateTime_local_time.toInstant().toEpochMilli();

        return timeMiliseconds_nextMonday;
    }





    public static String getDayOfWeek(long milliseconds) {
        // Convert milliseconds to an Instant
        Instant instant = Instant.ofEpochMilli(milliseconds);

        // Define the timezone you want to use (UTC, local, etc.)
        ZoneId zoneId = ZoneId.systemDefault();

        // Convert Instant to ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);

        // Get the day of the week and format it
        String dayOfWeek = zonedDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return dayOfWeek;
    }


    public void setText_Date(long milisec){

        long timeMiliseconds_previousMonday=returnMiliseconds_previousMonday2(milisec);
        long timeMiliseconds_nextMonday=returnMiliseconds_nextMonday2(milisec);

        Instant instant_first = Instant.ofEpochMilli(timeMiliseconds_previousMonday);
        LocalDateTime localDateTime_first = LocalDateTime.ofInstant(instant_first, ZoneId.systemDefault());

        Instant instant_last = Instant.ofEpochMilli(timeMiliseconds_nextMonday);
        LocalDateTime localDateTime_next = LocalDateTime.ofInstant(instant_last, ZoneId.systemDefault()).minusDays(1);

        txtDate.setText("Date: "+localDateTime_first.getDayOfMonth()+"/"+localDateTime_first.getMonthValue()+"/"+localDateTime_first.getYear());
        txtDate.setText(txtDate.getText()+"  -  "+localDateTime_next.getDayOfMonth()+"/"+localDateTime_next.getMonthValue()+"/"+localDateTime_next.getYear());

    }




}