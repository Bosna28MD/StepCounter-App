package com.example.fp3_android;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;

interface UserUIDCallback {
    void onUserUIDReceived(ArrayList<String> userUIDArr);
}

public class Register_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_, container, false);
    }

    private FirebaseAuth mAuth;
    TextInputEditText txtEmail,txtUserName,txtBirthDay,txtPwd;
    Button btnRegister;
    TextView errRegister;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        txtEmail=view.findViewById(R.id.edtxt_email_reg);
        txtUserName=view.findViewById(R.id.edtxt_name_reg);
        txtBirthDay=view.findViewById(R.id.edtxt_birthday_reg);
        txtPwd=view.findViewById(R.id.edtxt_pwd_reg);

        btnRegister=view.findViewById(R.id.btn_register);
        //btnLogInActivity=findViewById(R.id.btnLogActivity);

        errRegister=view.findViewById(R.id.err_register);

        errRegister.setVisibility(View.GONE);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(txtEmail.getText().toString().trim().length()==0 || txtUserName.getText().toString().trim().length()==0 ||
                        txtBirthDay.getText().toString().trim().length()==0 || txtPwd.getText().toString().trim().length()==0){
                    //Toast.makeText(getContext(), "Empty Fields", Toast.LENGTH_SHORT).show();
                    errRegister.setVisibility(View.VISIBLE);
                    errRegister.setText("Empty Fields");
                    return;
                }

                String email=txtEmail.getText().toString().trim();
                String pwd=txtPwd.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    createUserDB(mAuth.getCurrentUser().getUid(), email, txtUserName.getText().toString().trim(),
                                            txtBirthDay.getText().toString().trim(), new UserUIDCallback() {
                                                @Override
                                                public void onUserUIDReceived(ArrayList<String> userUIDArr) {
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString())
                                                            .setValue(new UserDB_Branch(txtUserName.getText().toString().trim(),email,txtBirthDay.getText().toString().trim(),"",""))
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        createFriendDB(mAuth.getCurrentUser().getUid().toString(),userUIDArr);
                                                                        //replaceFragment(new LogIn_Fragment());
                                                                        registerUserSharedPreferences(user.getEmail().toString(),user.getUid().toString());
                                                                        FirebaseMessaging.getInstance().getToken()
                                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<String> task) {
                                                                                        String token=task.getResult();
                                                                                        insertTokenFCM_DB(token);
                                                                                        startActivity(new Intent(getContext(),User_Activity.class));
                                                                                        getActivity().finish();
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            });

                                    Toast.makeText(getContext(), "User Created", Toast.LENGTH_SHORT).show();
                                    //updateUI(user);
                                } else {
                                    errRegister.setVisibility(View.VISIBLE);
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                        //Toast.makeText(Register_Activity.this, "", Toast.LENGTH_SHORT).show();
                                        errRegister.setText("Email already in use");
                                    }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                        errRegister.setText("Invalid Email");//Invalid Email
                                    }
                                    else if(task.getException() instanceof FirebaseAuthWeakPasswordException){
                                        errRegister.setText("Password should be 8 characters or longer");
                                    }else{
                                        errRegister.setText("Error during sign up");
                                    }


                                }
                            }
                        });



            }
        });



        ((EditText)view.findViewById(R.id.edtxt_birthday_reg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                new DatePickerDialog((Activity)getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        ((EditText)view.findViewById(R.id.edtxt_birthday_reg)).setText(i+"-"+(i1+1)+"-"+i2);
                    }
                },year,month,day).show();
            }
        });




    }


    public void registerUserSharedPreferences(String email,String uid){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("UserData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("logged",true);
        editor.putBoolean("userInitialize_data",false);
        editor.putString("email",email);
        editor.putString("uid",uid);
        editor.apply();
    }

    public void createUser_EventsDB(){
        FirebaseDatabase.getInstance().getReference().child("events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            /*if(snapshot.getChildrenCount()>0){

                            }*/
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    public void insertTokenFCM_DB(String token){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("UserData",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("logged",false)){
            String uid=sharedPreferences.getString("uid",null);
            if(uid==null){
                Toast.makeText(getContext(), "Some error appeared FCM Token(UID)", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("token")
                    .setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    }



    public void createUserDB(String uid,String email,String username,String dateOfBirth,UserUIDCallback callback){
        /*
        ArrayList<String> userUID_arr=new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for (DataSnapshot childSnapshot : snapshot.getChildren()){
                                        userUID_arr.add(childSnapshot.getKey().toString()); //Add all user-uid(except new_user) to array
                                    }


                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                            .setValue(new UserDB_Branch(username,email,dateOfBirth,"","")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //Toast.makeText(Register_Activity.this, "User Created DB", Toast.LENGTH_SHORT).show();
                                                        createFriendDB(uid,userUID_arr);//uid-user new created
                                                                                        //userUID_arr-array with uid without user new created
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        */

        ArrayList<String> userUID_arr=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.getChildrenCount()>0){ // if number of user is greater than one create friend-relationship
                                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                                    userUID_arr.add(childSnapshot.getKey().toString()); //Add all user-uid(except new_user) to array
                                }

                                callback.onUserUIDReceived(userUID_arr); //Create user with friend-relationship


                            }
                        }else{
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                    .setValue(new UserDB_Branch(username,email,dateOfBirth,"",""))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //First user created
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




    public void createFriendDB(String uid,ArrayList<String> userUID_arr){


        FirebaseDatabase.getInstance().getReference().child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int new_key=( (int) snapshot.getChildrenCount() )+1;

                            for(int i=0;i<userUID_arr.size();i++){

                                FirebaseDatabase.getInstance().getReference().child("friends").child(String.valueOf(new_key+i))
                                        .setValue(new FriendsDB_Branch(uid,userUID_arr.get(i).toString(),"0",""))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    //Friend Relationship created
                                                }
                                            }
                                        });

                            }

                        }else{ //Not exists friend-branch , First friend-relationship
                            FirebaseDatabase.getInstance().getReference().child("friends").child("1")
                                    .setValue(new FriendsDB_Branch(uid,userUID_arr.get(0).toString(),"0",""))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }



    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getParentFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if(fragment.getClass().equals( (new LogIn_Fragment()).getClass() )){
            //Toast.makeText(getContext(), "Fragment Equals", Toast.LENGTH_SHORT).show();
            bottomNavigationView.setSelectedItemId(R.id.login_bottom);
        }else if( fragment.getClass().equals( (new Register_Fragment()).getClass() ) ){
            bottomNavigationView.setSelectedItemId(R.id.register_bottom);
        }

    }





}