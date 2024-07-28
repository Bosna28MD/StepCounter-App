package com.example.fp3_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface CreateEventCallBack {
    void CreateEvent(String uid_request,String uid_receive);

}


interface CheckEventExistCallBack{
    void CheckEventScheduleIfExist(boolean checkExist);
}



interface checkEventTimeCallBack{
    void CheckEventWithAnotherUser(boolean checkExist);
}

public class ScheduleEventFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_event, container, false);
    }

    TextInputEditText txtInput_email,txtInput_username,txtInput_adress,txtInput_date,txtInput_hour_minutes;
    Button btnRequestSchedule;
    TextView txtErr;
    int year,day,month,hour,minute;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        String username=savedInstanceState.getString("username");
        String email=savedInstanceState.getString("email");

        if(username!=null && email!=null){
            Toast.makeText(getActivity(), "Username: "+username+" "+" Email: "+email, Toast.LENGTH_SHORT).show();
        }
        */

        //View view2 = inflater.inflate(R.layout.fragment_b, container, false);


        txtInput_email=view.findViewById(R.id.edtxt_email_reg);
        txtInput_username=view.findViewById(R.id.edtxt_name_reg);
        txtInput_adress=view.findViewById(R.id.edtxt_adress);
        txtInput_date=view.findViewById(R.id.edtxt_datePicker);
        txtInput_hour_minutes=view.findViewById(R.id.edtxt_timePicker);
        btnRequestSchedule=view.findViewById(R.id.btn_requestSchedule);
        txtErr=view.findViewById(R.id.txt_errSchedule);

        txtErr.setVisibility(View.GONE);



        Bundle bundle=getArguments();
        if(bundle!=null){
            String username=bundle.getString("username");
            String email=bundle.getString("email");
            txtInput_email.setText(email);
            txtInput_username.setText(username);
            //Toast.makeText(getActivity(), "Username: "+username+" "+" Email: "+email, Toast.LENGTH_SHORT).show();
        }



        




        btnRequestSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtErr.setTextColor(Color.parseColor("#F30606"));
                txtErr.setVisibility(View.GONE);

                String email=txtInput_email.getText().toString();
                String date=txtInput_date.getText().toString();
                String hour_min=txtInput_hour_minutes.getText().toString();
                String adress_event=txtInput_adress.getText().toString().trim();

                if(date.length()==0 || hour_min.length()==0 || adress_event.length()==0){
                    //Toast.makeText(getContext(), "Insert all fields", Toast.LENGTH_SHORT).show();
                    txtErr.setVisibility(View.VISIBLE);
                    txtErr.setText("Insert all fields");
                    return;
                }

                LocalDateTime localDateTime=LocalDateTime.now();
                String date_hour_min=date+" "+hour_min;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime_event = LocalDateTime.parse(date_hour_min, formatter);

                //String dateTimeCurrent_after6Hours=String.valueOf(localDateTime.plusHours(6));
                //Log.d("ScheduleEventFragment123","After 6 Hour:"+current_hour);
                LocalDateTime dateTimeCurrent_after6Hours= localDateTime.plusHours(6);


                if( dateTimeCurrent_after6Hours.isBefore(dateTime_event) ){
                    //Log.d("ScheduleEventFragment123","Request schedule-event send with success "+millis+" "+milis_after6HourCurrent);

                    ZoneId zoneId = ZoneId.systemDefault();
                    ZonedDateTime zonedDateTime = dateTime_event.atZone(zoneId);
                    long millis_selected_dateTime = zonedDateTime.toInstant().toEpochMilli();
                    //long milis_after6HourCurrent=dateTimeCurrent_after6Hours.atZone(zoneId).toInstant().toEpochMilli();


                    getUserUID_receive(txtInput_email.getText().toString(), new CreateEventCallBack() {
                        @Override
                        public void CreateEvent(String uid_request, String uid_receive) { // uid_request-it's me , uid_receive-it's user which I send request
                            //Log.d("ScheduleEventFragment123","Uid_req:"+uid_request+" uid_rec:"+uid_receive);

                            FirebaseDatabase.getInstance().getReference().child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        //int new_key=((int) snapshot.getChildrenCount() )+1;
                                        //Log.d("ScheduleEventFragment123","Second Time");

                                        checkEventBetweenUserExist(uid_request, uid_receive, new CheckEventExistCallBack() {
                                            @Override
                                            public void CheckEventScheduleIfExist(boolean checkExist) {
                                                if(!checkExist){

                                                    checkEventAlreadyExist_difference6Hour(uid_request, uid_receive, String.valueOf(millis_selected_dateTime), new checkEventTimeCallBack() {
                                                        @Override
                                                        public void CheckEventWithAnotherUser(boolean checkExist) {
                                                            if(!checkExist){
                                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events");
                                                                String key=ref.push().getKey();
                                                                ref.child(key).setValue( new CalendarEvent_Class(uid_request,uid_receive,String.valueOf(millis_selected_dateTime),"-1",adress_event) )
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    sendNotification_Request(uid_receive,date,hour_min);
                                                                                    Log.d("ScheduleEventFragment123","Success Any-Event request inserted id:"+key);
                                                                                    txtErr.setText("Success Request send");
                                                                                    txtErr.setTextColor(Color.parseColor("#00FF00"));
                                                                                    txtErr.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }
                                                                        });

                                                            }else{
                                                                txtErr.setText("An event in this interval-hour(6 hours) is set for you or other user");
                                                                txtErr.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    });



                                                }else{
                                                    txtErr.setText("An event with this user is already set");
                                                    txtErr.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });

                                    }else{
                                        //Log.d("ScheduleEventFragment123","First Time Run....");
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events");
                                        String key=ref.push().getKey();
                                        ref.child(key).setValue(new CalendarEvent_Class(uid_request,uid_receive,String.valueOf(millis_selected_dateTime),"-1",adress_event))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Log.d("ScheduleEventFragment123","Success 1-Event request inserted id:"+key);
                                                            sendNotification_Request(uid_receive,date,hour_min);
                                                            txtErr.setText("Success Request send");
                                                            txtErr.setTextColor(Color.parseColor("#00FF00"));
                                                            txtErr.setVisibility(View.VISIBLE);
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
                    });

                }else{
                    txtErr.setText("Data-Time difference is not acceptable(Read reminder)");
                    txtErr.setVisibility(View.VISIBLE);

                }

                /*
                if(localDateTime.isBefore(dateTime_event)){
                    Log.d("ScheduleEventFragment123","Local Time Before data selected");
                }else if( localDateTime.isAfter(dateTime_event) ){
                    Log.d("ScheduleEventFragment123","Local Time after data selected");
                }else if( localDateTime.isEqual(dateTime_event) ){
                    Log.d("ScheduleEventFragment123","Local Time equal data selected");
                }
                */

                //Log.d("ScheduleEventFragment123",dateTime_event.toString());
                //Log.d("ScheduleEventFragment123",date_hour_min);





                //createRequestEventSchedule(txtInput_email.getText().toString());
                //String email=FirebaseAuth.getInstance().getCurrentUser().getUid();
                //Toast.makeText(getContext(), "Email: "+email, Toast.LENGTH_SHORT).show();
                //createRequestEventSchedule();
                //Toast.makeText(getActivity(), "Email:"+txtInput_email.getText()+" Username:"+txtInput_username.getText(), Toast.LENGTH_SHORT).show();
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment_addFriend).commit();

            }
        });




        final Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        month=calendar.get(Calendar.MONTH);
        hour=calendar.get(Calendar.HOUR_OF_DAY) ;
        minute=calendar.get(Calendar.MINUTE);

        txtInput_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new DatePickerDialog((Activity)getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        //LocalDateTime localDateTime_Now=LocalDateTime.now();
                        //LocalDateTime.of();
                        if( (i1+1)<10 && i2<10 ){
                            txtInput_date.setText(i+"-0"+(i1+1)+"-0"+i2);
                        }else if((i1+1)<10 || i2<10){
                            if((i1+1)<10){
                                txtInput_date.setText(i+"-0"+(i1+1)+"-"+i2);
                            }else{
                                txtInput_date.setText(i+"-"+(i1+1)+"-0"+i2);
                            }
                        }
                        else{
                            txtInput_date.setText(i+"-"+(i1+1)+"-"+i2);
                        }
                        year=i;
                        month=i1;
                        day=i2;
                    }
                },year,month,day).show();
            }
        });


        //final Calendar calendar=Calendar.getInstance();



        txtInput_hour_minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int hour,minutes;
                /*TimePickerDialog.OnTimeSetListener onTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //hour=selectedHour;
                        //minutes=selectedMinute;
                        txtInput_hour_minutes.setText(selectedHour+":"+selectedMinute);
                    }
                };*/


                int style= AlertDialog.THEME_HOLO_DARK;
                new TimePickerDialog(getContext(), style, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedHour<10 && selectedMinute<10){
                            txtInput_hour_minutes.setText("0"+selectedHour+":0"+selectedMinute);
                        }else if(selectedHour<10){
                            txtInput_hour_minutes.setText("0"+selectedHour+":"+selectedMinute);
                        }else if(selectedMinute<10){
                            txtInput_hour_minutes.setText(selectedHour+":0"+selectedMinute);
                        }else{
                            txtInput_hour_minutes.setText(selectedHour+":"+selectedMinute);
                        }
                        //
                        hour=selectedHour;
                        minute=selectedMinute;
                    }
                },hour,minute,true).show();

            }
        });



    }


    private void createRequestEventSchedule(String email_user_receive){
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            String uid_user_receive="";
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                if( dataSnapshot.child("email").getValue(String.class).equals(email_user_receive) ){
                                    uid_user_receive=dataSnapshot.getKey().toString();
                                    break;
                                }
                                //Log.d("ScheduleEventFragment123","User:"+dataSnapshot.getKey()+" "+dataSnapshot.child("email").getValue(String.class));
                            }


                            FirebaseDatabase.getInstance().getReference().child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String current_userUid=FirebaseAuth.getInstance().getUid().toString();
                                    if(snapshot.exists()){
                                        int new_key=((int) snapshot.getChildrenCount() )+1;

                                    }else{
                                        /*FirebaseDatabase.getInstance().getReference().child("events").child("1")
                                                .setValue(new CalendarEvent_Class(current_userUid,uid_user_receive,"-1"))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });*/

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
        /*FirebaseDatabase.getInstance().getReference().child("events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //String email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            int new_key=((int) snapshot.getChildrenCount() )+1;
                            FirebaseDatabase.getInstance().getReference().child("events").child(String.valueOf(new_key))
                                    .setValue()

                        }else {

                            //Toast.makeText(getContext(), "Events Not Created", Toast.LENGTH_SHORT).show();
                            //Map<String,Object>
                            //FirebaseDatabase.getInstance().getReference().child("events").child("1")
                            //        .setValue().
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        */
    }


    private void getUserUID_receive(String email_user_receive,CreateEventCallBack callBack){
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            String uid_user_receive="";
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                if( dataSnapshot.child("email").getValue(String.class).equals(email_user_receive) ){
                                    uid_user_receive=dataSnapshot.getKey().toString();
                                    break;
                                }
                                //Log.d("ScheduleEventFragment123","User:"+dataSnapshot.getKey()+" "+dataSnapshot.child("email").getValue(String.class));
                            }

                            String uid_user_request=FirebaseAuth.getInstance().getUid().toString();
                            callBack.CreateEvent(uid_user_request,uid_user_receive);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



    public void checkEventBetweenUserExist(String uid_user1_request,String uid_user2_receive,CheckEventExistCallBack callBack){
        FirebaseDatabase.getInstance().getReference().child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean checkExist=false;
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String useruid1=dataSnapshot.child("uid_user1_request").getValue(String.class);
                        String useruid2=dataSnapshot.child("uid_user2_receive").getValue(String.class);


                        if( ( useruid1.equals(uid_user1_request) || useruid2.equals(uid_user1_request) )
                                &&  ( useruid1.equals(uid_user2_receive) || useruid2.equals(uid_user2_receive) )  ){

                            String type_request=dataSnapshot.child("type_request").getValue(String.class);
                            if(type_request.equals("1") || type_request.equals("-1")){ // 1-Accepted , -1-Request , 0-Finished
                                checkExist=true;
                                break;
                            }
                            //callBack.CheckEventScheduleIfExist(true);
                        }
                    }
                    callBack.CheckEventScheduleIfExist(checkExist);

                }else{
                    callBack.CheckEventScheduleIfExist(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void checkEventAlreadyExist_difference6Hour(String uid_user1_request,String uid_user2_receive,String timeMiliseconds_set_req,checkEventTimeCallBack callBack){
        FirebaseDatabase.getInstance().getReference().child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    boolean checkExist=false;

                    for ( DataSnapshot dataSnapshot:snapshot.getChildren() ){
                        String useruid1=dataSnapshot.child("uid_user1_request").getValue(String.class);
                        String useruid2=dataSnapshot.child("uid_user2_receive").getValue(String.class);


                        if(useruid1.equals(uid_user1_request) || useruid2.equals(uid_user1_request)
                                || useruid1.equals(uid_user2_receive) || useruid2.equals(uid_user2_receive) ){

                            String type_request=dataSnapshot.child("type_request").getValue(String.class);
                            if(!type_request.equals("0")){
                                String time_miliseconds_eventDB=dataSnapshot.child("time_miliseconds_event").getValue(String.class);
                                long time_db=Long.parseLong(time_miliseconds_eventDB);
                                long time_req=Long.parseLong(timeMiliseconds_set_req);

                                Instant instant1 = Instant.ofEpochMilli(time_db);
                                Instant instant2 = Instant.ofEpochMilli(time_req);

                                // Convert Instant to LocalDateTime in the system default time zone
                                LocalDateTime localDateTime_db = LocalDateTime.ofInstant(instant1, ZoneId.systemDefault());
                                LocalDateTime localDateTime_req = LocalDateTime.ofInstant(instant2, ZoneId.systemDefault());
                                Duration duration = Duration.between(localDateTime_db, localDateTime_req);

                                // Get the difference in hours
                                long hoursDifference = Math.abs(duration.toHours());
                                if(hoursDifference<6){ //If difference between hour selected and after is less of 6 hour difference then print an error
                                    checkExist=true;
                                    break;
                                }


                            }
                        }
                    }
                    callBack.CheckEventWithAnotherUser(checkExist);



                }else{
                    callBack.CheckEventWithAnotherUser(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    public void sendNotification_Request(String user_uid,String date,String hour){
        FirebaseDatabase.getInstance().getReference().child("users").child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String email=snapshot.child("email").getValue(String.class);
                    String token=snapshot.child("token").getValue(String.class);
                    if(!token.equals("")){
                        sendPostRequest(email,"request an event "+date+" "+hour,token);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public static void sendPostRequest(String key1, String key2, String token) {
        // Create an ExecutorService to run the network request on a background thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            HttpURLConnection con = null;
            try {
                String url = "https://us-central1-fp3-android.cloudfunctions.net/sendMessage";

                URL obj = new URL(url);
                con = (HttpURLConnection) obj.openConnection();

                // Setting basic post request
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");

                // Prepare JSON payload
                String jsonInputString = String.format("{ \"key1\": \"%s\", \"key2\": \"%s\", \"token\": \"%s\" }", key1, key2, token);

                // Send post request
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response code
                int responseCode = con.getResponseCode();
                Log.d("UserInfoFragment123", "ResponseCode:" + responseCode);

                // Read the response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        // Print the response
                        Log.d("UserInfoFragment123", "Response:" + response);
                        System.out.println("Response: " + response.toString());
                    }
                    Log.d("UserInfoFragment123", "Post Request Worked");
                } else {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        // Print the error response
                        Log.d("UserInfoFragment123", "Error response: " + response.toString());
                        System.out.println("Error response: " + response.toString());
                    }
                    Log.d("UserInfoFragment123", "POST request did not work.");
                    System.out.println("POST request did not work.");
                }
            } catch (Exception e) {
                Log.d("UserInfoFragment123", e.getMessage());
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        });
    }





}