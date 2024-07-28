package com.example.fp3_android;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        insertNewToken_DB(token);   //If token is updated , update token also in database
        //Log.d("MyFirebaseMessagingService123","Token: "+token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("MyFirebaseMessagingService123","Push Notification Received");

        if (message.getData().size() > 0){
            if( message.getData().get("type_req").equals("event_request") ){
                String key1=message.getData().get("key1");
                String key2=message.getData().get("key2");
                showNotification( key1+" "+key2 );
            }
            else if( message.getData().get("type_req").equals("step_counter_register") ){ //Call at interval of 24-Hour to retrive steps
                //String key1=message.getData().get("key1");
                //String key2=message.getData().get("key2");
                showNotification("Steps Taked to DB");

                insertDb_Steps();



            }

        }

    }


    public void showNotification(String key_value){
        String channel_id="MyFirebaseChannel_Notification1";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Notification Title")
                .setContentText("Notification-Message: "+key_value)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                //.addAction(R.drawable.baseline_add_24,"Accept",pendingIntent_btn1)
                //.addAction(R.drawable.baseline_close_24,"Refuse",pendingIntent_btn2)

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=notificationManager.getNotificationChannel(channel_id);
            if(channel==null){
                int importance=NotificationManager.IMPORTANCE_HIGH;
                channel=new NotificationChannel(channel_id,"Some Description",importance);
                channel.setLightColor(Color.GREEN);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }
        }

        notificationManager.notify(0,builder.build());

    }



    public void insertNewToken_DB(String token){
        SharedPreferences sharedPreferences=this.getSharedPreferences("UserData",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("logged",false)){ //If user is logged then reset new token of specific user
            String uid=sharedPreferences.getString("uid",null);
            if(uid==null){
                Toast.makeText(this, "Some error appeared FCM Token(UID)", Toast.LENGTH_SHORT).show();
                //throw Exception("")
                //Log.d("")
                return;
            }

            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("token")
                    .setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MyFirebaseMessagingService.this, "Token updated", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MyFirebaseMessagingService.this, "Some error appeared", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    public void insertDb_Steps(){

        //SharedPreferences sharedPreferences=getSharedPreferences("UserData",MODE_PRIVATE);
        //String email_123=sharedPreferences.getString("email","Empty");

        SharedPreferences sharedPreferences=this.getSharedPreferences("UserData",MODE_PRIVATE);
        String uid=sharedPreferences.getString("uid","");
        if(uid==null || uid.equals("")){
            Log.d("MyFirebaseMessagingService123","Uid from sharedPreferences null or empty");
            return;
        }

        String current_steps=StepCounter_Class.getCurrentSteps(getApplicationContext());
        if(current_steps==null){
            current_steps="0";
        }
        String currentTimeMiliseconds=String.valueOf(System.currentTimeMillis());

        HashMap<String,String> hashMap_steps=new HashMap<>();
        hashMap_steps.put("steps",current_steps);
        FirebaseDatabase.getInstance().getReference().child("steps_db").child(uid).
            child(currentTimeMiliseconds).setValue(hashMap_steps).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Steps Inserted DB
                            StepCounter_Class.appStart_Set_KeyFalse(getApplicationContext());
                            StepCounter_Class.setCurrentSteps("0",getApplicationContext());
                        }
                    }
                });

        /*
        FirebaseDatabase.getInstance().getReference().child("steps_db").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int new_key=((int)snapshot.getChildrenCount())+1;
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("key1","val-"+new_key+" "+email_123);
                    hashMap.put("key2","val-"+new_key);
                    FirebaseDatabase.getInstance().getReference().child("steps_counter").child(String.valueOf(new_key))
                            .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                    }
                                }
                            });
                }else{
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("key1","val-1"+" "+email_123);
                    hashMap.put("key2","val-1");
                    FirebaseDatabase.getInstance().getReference().child("steps_counter").child("1")
                            .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

    }





}
