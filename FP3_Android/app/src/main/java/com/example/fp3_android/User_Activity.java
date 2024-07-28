package com.example.fp3_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class User_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SensorManager mSensorManager=null;
    private Sensor stepSensor=null;
    StepCounter_Class stepCounterClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        StepCounter_Class.setCurrentSteps("0",getApplicationContext()); //Set at start steps in SharedPreferences to be 0 for example to be set for device who doesn't have sensor for StepCounter

        if(ContextCompat.checkSelfPermission(User_Activity.this, android.Manifest.permission.ACTIVITY_RECOGNITION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACTIVITY_RECOGNITION},101); //Request permision for Activity Recognition
        }

        if(ContextCompat.checkSelfPermission(User_Activity.this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},101); //Request permision for Activity Recognition
        }



        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(this,Login_Create_Activity.class));
            finish();
            return;
        }

        //Log.d("User_Activity123","User UID:"+currentUser.getUid().toString());

        /*FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid().toString()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String username=snapshot.child("username").getValue(String.class);
                            String email=snapshot.child("email").getValue(String.class);
                            String dateOfBirthday=snapshot.child("dateOfBirthday").getValue(String.class);
                            Log.d("User_Activity123","UID:"+currentUser.getUid().toString()+"\nEmail:"+email+"\nusername:"+username+"\ndateOfBirthday:"+dateOfBirthday);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        */

        //Toast.makeText(this, currentUser.getUid().toString(), Toast.LENGTH_SHORT).show();


        checkTokenDB(); //Check token from DB with token from app and check if it is the same
        getUserData_InsertSharedPreferences(); //Get username and birthday from DB and insert in SharedPreferences(this will be called only one time when user login)
        updateConnectionUser_TimeStamp_DB(); //When user connects in app update timestamp in database


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        replaceFragment(new HomeFragment());
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId()==R.id.home_bottom){
                replaceFragment(new HomeFragment());
            }else if(item.getItemId()==R.id.friends_bottom){
                replaceFragment(new FriendListFragment());
            }else if(item.getItemId()==R.id.leaderboard_bottom){
                replaceFragment(new RegistrationLeaderBoard());
            }
            else if(item.getItemId()==R.id.calendar_bottom){
                replaceFragment(new CalendarFragment());
            }else if(item.getItemId()==R.id.userinfo_bottom){
                replaceFragment(new UserInfoFragment());
            }


            return true;
        });



        InitializeStepCounter();

    }



    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }




    public void getUserData_InsertSharedPreferences(){
        //Get UserName and Birthday from Database and insert in SharedPreferences
        SharedPreferences sharedPreferences=this.getSharedPreferences("UserData",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("userInitialize_data",false)){
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid_FBA=currentUser.getUid().toString();     //uid_FBA - uid Firebase Authentication
        String email_FBA=currentUser.getEmail().toString();//email_FBA - email Firebase Authentication

        String uid=sharedPreferences.getString("uid",null);
        if(uid==null || uid.equals("")){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            //if(snapshot.child("username").exists() )
            editor.putString("uid",uid_FBA);
            editor.putString("email",email_FBA);
            editor.apply();
            uid=uid_FBA;
            //Toast.makeText(this, "UID not exist in SharedPreferences", Toast.LENGTH_SHORT).show();
            //return;
        }

        Log.d("UserActivity123","Uid: "+uid);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            //if(snapshot.child("username").exists() )
                                editor.putString("username",snapshot.child("username").getValue(String.class));
                                editor.putString("dateOfBirthday",snapshot.child("dateOfBirthday").getValue(String.class));
                                editor.putBoolean("userInitialize_data",true);
                                editor.apply();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }





    public void checkTokenDB(){
        /*SharedPreferences sharedPreferences=this.getSharedPreferences("UserData",MODE_PRIVATE);
        String uid=sharedPreferences.getString("uid",null);
        if(uid==null || uid.equals("")){
            Toast.makeText(this, "UID not exist in SharedPreferences", Toast.LENGTH_SHORT).show();
            return;
        }*/
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid=currentUser.getUid().toString();

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if(snapshot.exists()){
                        final String token_db=snapshot.getValue(String.class);
                        if(token_db==null || token_db.equals("")){
                            Toast.makeText(User_Activity.this, "<--Token null-->", Toast.LENGTH_SHORT).show();
                            return;
                        }
                         FirebaseMessaging.getInstance().getToken()
                                 .addOnCompleteListener(new OnCompleteListener<String>() {
                                     @Override
                                     public void onComplete(@NonNull Task<String> task) {
                                        if(task.isSuccessful()){
                                            String currentToken=task.getResult();
                                            if(!currentToken.equals(token_db)){
                                                updateToken(currentToken,uid);
                                            }
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




    public void updateToken(String token_update,String uid){
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("token")
                .setValue(token_update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(getContext(), "Token updated", Toast.LENGTH_SHORT).show();
                        }else{
                            //Toast.makeText(getContext(), "Some error appeared", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public void updateConnectionUser_TimeStamp_DB(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid=currentUser.getUid().toString();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("timestamp_login")
                                    .setValue(String.valueOf(System.currentTimeMillis())).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    }




    public void InitializeStepCounter(){
        stepCounterClass=new StepCounter_Class(getApplicationContext());
        if(!StepCounter_Class.appStart_Get_Key(getApplicationContext())){ //Utilizator logat pentru prima data
            StepCounter_Class.appStart_Set_KeyTrue(getApplicationContext());
            StepCounter_Class.appStart=0;

        }else{ //Utilizator deja logat de mai mult timp in cont
            StepCounter_Class.appStart=1;
            StepCounter_Class.loadData(getApplicationContext(),stepCounterClass);
        }

        mSensorManager=(SensorManager)getApplicationContext().getSystemService(getApplicationContext().SENSOR_SERVICE);
        stepSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(stepSensor==null){
            Toast.makeText(getApplicationContext(), "This device doesn't have Sensory", Toast.LENGTH_SHORT).show();
            //StepCounter_Class.setCurrentSteps("0",getApplicationContext());
            return;
        }
        mSensorManager.registerListener(stepCounterClass,stepSensor,SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(stepCounterClass);
    }



}