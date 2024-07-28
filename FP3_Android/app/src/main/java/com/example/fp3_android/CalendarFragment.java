package com.example.fp3_android;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/*
interface GetUserDataCallBack2{
    void findUserInfo(ArrayList<ArrayList<String>> arr_userUid);
}
*/



public class CalendarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    RecyclerView recyclerView;
    ValueEventListener valueEventListener;
    ArrayList<ScheduleEventClass> scheduleEvent_arr;
    boolean changeFragment=false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scheduleEvent_arr=new ArrayList<>();

        recyclerView=view.findViewById(R.id.parent_recycle_view_Calendar);

        MaterialButtonToggleGroup toggleGroup=view.findViewById(R.id.togglebtnGroup1);

        Button btnToggle1=view.findViewById(R.id.toggleBtn1);
        Button btnToggle2=view.findViewById(R.id.toggleBtn2);
        toggleGroup.check(btnToggle1.getId());


        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){

                    if(checkedId==btnToggle1.getId()){
                        //Empty
                    }else if(checkedId==btnToggle2.getId() && changeFragment){
                        //Toast.makeText(getContext(), "Button2", Toast.LENGTH_SHORT).show();
                        ScheduleEventRequest scheduleEventRequest=new ScheduleEventRequest();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,scheduleEventRequest).commit();

                    }

                }else{
                    toggleGroup.check(btnToggle1.getId());
                }
            }
        });

        try {
            CheckDB_EventPastDateAndShowCalendar(); //Check and Prind Calendar Inside this method
        }catch (Exception e){
            Log.d("CalendarFragment123",e.getMessage());
        }

        //show_EventsRequests();


    }




    public void getDB_ScheduleEventsRequest(GetUserDataCallBack callBack){
        FirebaseDatabase.getInstance().getReference().child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ArrayList<String>>  uidUserReq_arr=new ArrayList<>();

                if(snapshot.exists()){
                    String uid_currentUser= FirebaseAuth.getInstance().getUid().toString();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        //Log.d("SchedduleEventRequest123",dataSnapshot.getKey().toString());
                        String uid_request=dataSnapshot.child("uid_user1_request").getValue(String.class);
                        String uid_receive=dataSnapshot.child("uid_user2_receive").getValue(String.class);
                        String type_request=dataSnapshot.child("type_request").getValue(String.class);
                        String time_miliseconds_event=dataSnapshot.child("time_miliseconds_event").getValue(String.class); //time_miliseconds_event
                        String adress_event=dataSnapshot.child("adress_event").getValue(String.class);
                        if( ( uid_currentUser.equals(uid_receive) || uid_currentUser.equals(uid_request) )  && type_request.equals("1") ){
                            ArrayList<String> arr_val=new ArrayList<>();
                            arr_val.add(dataSnapshot.getKey().toString());      //position-0: Key
                            if(uid_currentUser.equals(uid_receive)){
                                arr_val.add(uid_request);                       //position-1: uid
                            }else{
                                arr_val.add(uid_receive);                       //position-1: uid
                            }
                            arr_val.add(time_miliseconds_event);                //position-2: time_miliseconds
                            arr_val.add(adress_event);                          //position-3: adress event
                            uidUserReq_arr.add(arr_val);

                        }
                    }
                    //Log.d("SchedduleEventRequest123","Number of request"+String.valueOf(uidUserReq_arr.size()));
                    callBack.findUserInfo(uidUserReq_arr);
                }else{
                    //Log.d("SchedduleEventRequest123","Request not exists");
                    callBack.findUserInfo(uidUserReq_arr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }






    public void show_EventsRequests(){
        //Log.d("CalendarFragment123","ShowEventReuqestCall123");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                changeFragment=true;
                scheduleEvent_arr.clear();
                Log.d("CalendarFragment123","EventRequest is Called-Exist Snapshot");
                if(snapshot.exists()){
                    //Log.d("CalendarFragment123","EventRequest is Called-Exist Snapshot");
                    getDB_ScheduleEventsRequest(new GetUserDataCallBack() {
                        @Override
                        public void findUserInfo(ArrayList<ArrayList<String>> arr_userUid) {
                              if(arr_userUid.size()>0){

                                  for(int i=0;i<arr_userUid.size();i++) {
                                      final int i_global = i;
                                      FirebaseDatabase.getInstance().getReference().child("users").child(arr_userUid.get(i).get(1).toString())
                                              .addListenerForSingleValueEvent(new ValueEventListener() {
                                                  @Override
                                                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        String email=snapshot.child("email").getValue(String.class);
                                                        String time_milis=arr_userUid.get(i_global).get(2);
                                                        String[] date_time=convertTimeMiliseconds_Date_HorMin(Long.parseLong(time_milis));
                                                        String adress_event=arr_userUid.get(i_global).get(3).toString();

                                                        scheduleEvent_arr.add(new ScheduleEventClass(arr_userUid.get(i_global).get(0),email,date_time[0],date_time[1],adress_event));

                                                        if( (i_global+1)==arr_userUid.size() ){
                                                            AdapterRecycleView_Calendar adapter=new AdapterRecycleView_Calendar(getContext(),CalendarFragment.this);
                                                            adapter.setCalendar_arr(scheduleEvent_arr);
                                                            recyclerView.setAdapter(adapter);
                                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                        }
                                                    }
                                                  }

                                                  @Override
                                                  public void onCancelled(@NonNull DatabaseError error) {

                                                  }
                                              });

                                  }

                              }else{
                                  AdapterRecycleView_Calendar adapter=new AdapterRecycleView_Calendar(getContext(),CalendarFragment.this);
                                  adapter.setCalendar_arr(scheduleEvent_arr);
                                  recyclerView.setAdapter(adapter);
                                  recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                              }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        //FirebaseDatabase.getInstance().getReference().child("events").addValueEventListener(valueEventListener);
        try {
            FirebaseDatabase.getInstance().getReference().child("events").addValueEventListener(valueEventListener);
        }catch (Exception e){
            Log.d("CalendarFragment123","Error addValueEventListener:"+e.getMessage());
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //FirebaseDatabase.getInstance().getReference().child("events").removeEventListener(valueEventListener);
        if(valueEventListener!=null){
            FirebaseDatabase.getInstance().getReference().child("events").removeEventListener(valueEventListener);
        }
        /*try {

        }catch (Exception e){
            Log.d("CalendarFragment123","Error removeListener:"+e.getMessage());
        }*/
    }


    public String[] convertTimeMiliseconds_Date_HorMin(long milliseconds){
        Instant instant = Instant.ofEpochMilli(milliseconds);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = localDateTime.format(dateFormatter);

        // Format the time part
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = localDateTime.format(timeFormatter);

        // Return the date and time as an array
        return new String[]{date, time};

    }




    public void CheckDB_EventPastDateAndShowCalendar(){
        //Check if an event with current useruid has value 0
        String current_userUid=FirebaseAuth.getInstance().getUid().toString();
        ArrayList<String> arr_eventKey=new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String uid1_request=dataSnapshot.child("uid_user1_request").getValue(String.class);
                        String uid2_receive=dataSnapshot.child("uid_user2_receive").getValue(String.class);

                        if(uid1_request.equals(current_userUid) || uid2_receive.equals(current_userUid)){
                            long timeMiliseconds_db=Long.parseLong( dataSnapshot.child("time_miliseconds_event").getValue(String.class) );
                            LocalDateTime localDateTime=LocalDateTime.now();
                            ZoneId zoneId = ZoneId.systemDefault();
                            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
                            long millis_currentDate = zonedDateTime.toInstant().toEpochMilli();

                            if(timeMiliseconds_db<millis_currentDate){
                                arr_eventKey.add(dataSnapshot.getKey().toString());
                            }

                        }

                    }


                    for(int i=0;i<arr_eventKey.size();i++){
                        String keyEvent=arr_eventKey.get(i).toString();
                        final int i_global=0;
                        FirebaseDatabase.getInstance().getReference().child("events").child(keyEvent).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("CalendarFragment123","Value Removed");
                                    if( (i_global+1)==arr_eventKey.size() ){
                                        show_EventsRequests();
                                    }
                                }
                            }
                        });



                    }

                    if(arr_eventKey.size()==0){
                        show_EventsRequests();
                    }


                    //

                }else{
                    //Log.d("CalendarFragment123","Events node in DB not exists");
                    show_EventsRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



}