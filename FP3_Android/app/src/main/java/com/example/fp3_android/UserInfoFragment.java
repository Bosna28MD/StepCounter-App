package com.example.fp3_android;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    TextView username_txt,email_txt,birthday_txt;
    Button btnSettings,btnLogOut;
    private FirebaseAuth mAuth;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth=FirebaseAuth.getInstance();

        username_txt=view.findViewById(R.id.txtName_InfoUser);
        email_txt=view.findViewById(R.id.txtEmail_InfoUser);
        birthday_txt=view.findViewById(R.id.txtBirthDay_InfoUser);

        Initialize_UserDataView();

        //btnSettings=view.findViewById(R.id.btnSettings);
        btnLogOut=view.findViewById(R.id.btnLogOut);

        /*
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
                //sendPostRequest3("value1","value2","eCgOQQuUSbO097tcsn9Yxb:APA91bGRRsLqQhxxtavpYgJfielepPBfOUutNtD7mCY2aQ-FPxR8JxdIyHaZ_U04t5yLzgloD61M4Kv7K4U3Z-rvokUmULlf0FwHFOIxFJxow9Wu1DjZ2xiIeSfFTp4RIyCpt6Y3jVwq");
            }
        });*/


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut_User();
            }
        });


    }



    public void Initialize_UserDataView(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("UserData",MODE_PRIVATE);
        email_txt.setText("Email:  "+sharedPreferences.getString("email",""));
        username_txt.setText("Name:   "+sharedPreferences.getString("username",""));
        birthday_txt.setText("Birthday:  "+sharedPreferences.getString("dateOfBirthday",""));
    }


    public void LogOut_RemoveSharedPreferencesData(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("UserData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putBoolean("logged",false);
        editor.putBoolean("userInitialize_data",false);
        editor.putString("email","");
        editor.putString("uid","");
        editor.putString("username","");
        editor.putString("dateOfBirthday","");
        editor.apply();

    }




    public void removeTokenFCM_DB(){
        /*SharedPreferences sharedPreferences=getContext().getSharedPreferences("UserData",MODE_PRIVATE);

            String uid=sharedPreferences.getString("uid",null);
            if(uid==null || uid.equals("")){
                Toast.makeText(getContext(), "Some error appeared FCM Token(UID)", Toast.LENGTH_SHORT).show();
                return;
            }
        */

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid_currentUser=currentUser.getUid();


            FirebaseDatabase.getInstance().getReference().child("users").child(uid_currentUser).child("token")
                    .setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{

                            }
                        }
            });

    }



    public void removeTimeStamp_DB(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid_currentUser=currentUser.getUid();

        FirebaseDatabase.getInstance().getReference().child("users").child(uid_currentUser).child("timestamp_login")
                .setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }
                    }
                });
    }


    public void ResetValueStepCounter_null(){
        //StepCounter_Class stepCounterClass=new StepCounter_Class(getContext(),this);
        //stepCounterClass.appStart_Set_KeyFalse();
        StepCounter_Class.appStart_Set_KeyFalse(getContext());
        StepCounter_Class.setCurrentSteps("0",getContext());
    }



    public void LogOut_User(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Alert");
        builder.setMessage("If you logout your Step-Counter will be reseted. Do you wish to logout?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeTimeStamp_DB();
                removeTokenFCM_DB();
                FirebaseAuth.getInstance().signOut();
                //removeTokenFCM_DB();
                LogOut_RemoveSharedPreferencesData();
                ResetValueStepCounter_null();
                startActivity(new Intent(getContext(),Login_Create_Activity.class));
                getActivity().finish();
            }
        });


        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getContext(), "Button Decline Clicked", Toast.LENGTH_SHORT).show();
                //replaceFragment(new HomeFragment());

            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }















}