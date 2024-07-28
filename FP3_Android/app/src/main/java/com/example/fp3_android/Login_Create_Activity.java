package com.example.fp3_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Create_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.d("Log_in_Create_Activity123","LogIn Activity Start");

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(this,User_Activity.class));
            finish();
            return;
        }

        CheckSharedPreferencesUserDataRemove();

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        replaceFragment(new LogIn_Fragment());
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId()==R.id.login_bottom){
                replaceFragment(new LogIn_Fragment());
            }else if(item.getItemId()==R.id.register_bottom){
                replaceFragment(new Register_Fragment());
            }


            return true;
        });



    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();


    }


    public void CheckSharedPreferencesUserDataRemove(){
        //If somehow firebase un-log a user, data from SharedPreferences have to be removed
        SharedPreferences sharedPreferences=this.getSharedPreferences("UserData",MODE_PRIVATE);

            if(sharedPreferences.getBoolean("logged",false)){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("logged",false);
                editor.putBoolean("userInitialize_data",false);
                editor.putString("email","");
                editor.putString("uid","");
                editor.putString("username","");
                editor.putString("dateOfBirthday","");
                editor.apply();
            }

    }


}