package com.example.fp3_android;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LogIn_Fragment extends Fragment {

    private FirebaseAuth mAuth;
    TextInputEditText emailTxt,pwdTxt;
    Button btnLogIn;
    TextView errLogIn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in_, container, false);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth=FirebaseAuth.getInstance();

        /*try {
            mAuth=FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                startActivity(new Intent(getContext(),User_Activity.class));
                getActivity().finish();
                //Change Activity Home because user is already log-in
                //Toast.makeText(this, "Not LogIn", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.d("LogInFragment123",e.getMessage());
        }*/
        //startActivity(new Intent(getContext(),User_Activity.class));
        //getActivity().finish();




        //mAuth = FirebaseAuth.getInstance();

        emailTxt=view.findViewById(R.id.edtxt_email);
        pwdTxt=view.findViewById(R.id.edtxt_pwd);

        btnLogIn=view.findViewById(R.id.btn_login);


        errLogIn=view.findViewById(R.id.err_login);

        errLogIn.setVisibility(View.GONE);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                errLogIn.setVisibility(View.GONE);

                if(emailTxt.getText().toString().trim().length()==0 || pwdTxt.getText().toString().trim().length()==0){
                    errLogIn.setVisibility(View.VISIBLE);
                    errLogIn.setText("Empty Fields");
                    return;
                }

                String email=emailTxt.getText().toString().trim();
                String pwd=pwdTxt.getText().toString().trim();


                mAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    registerUserSharedPreferences(user.getEmail().toString(),user.getUid().toString()); //Register SharedPreferences
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


                                } else {
                                    // If sign in fails, display a message to the user.
                                    errLogIn.setVisibility(View.VISIBLE);
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                    Log.d("Error_Log_in123",task.getException().getMessage());

                                    switch (errorCode) {

                                        case "ERROR_INVALID_CUSTOM_TOKEN":
                                            errLogIn.setText("The custom token format is incorrect. Please check the documentation.");
                                            //Toast.makeText(MainActivity.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                            errLogIn.setText("The custom token corresponds to a different audience.");
                                            //Toast.makeText(MainActivity.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                                            break;

                                        /*Only this line running for unexisted email or wrong password*/
                                        case "ERROR_INVALID_CREDENTIAL":
                                            errLogIn.setText("the supplied auth credential is malformed or has expired.");
                                            //Toast.makeText(MainActivity.this, "The supplied auth credential is malformed", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_INVALID_EMAIL":
                                            errLogIn.setText("The email address is badly formatted.");
                                            //Toast.makeText(MainActivity.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                                            //etEmail.setError("The email address is badly formatted.");
                                            //etEmail.requestFocus();
                                            break;

                                        case "ERROR_WRONG_PASSWORD":
                                            errLogIn.setText("The password is invalid or the user does not have a password.");
                                            //Toast.makeText(MainActivity.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                                            //etPassword.setError("password is incorrect ");
                                            //etPassword.requestFocus();
                                            //etPassword.setText("");
                                            break;

                                        case "ERROR_USER_MISMATCH":
                                            errLogIn.setText("The supplied credentials do not correspond to the previously signed in user.");
                                            //Toast.makeText(MainActivity.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                                            break;
                                        /*
                                        case "ERROR_REQUIRES_RECENT_LOGIN":
                                            errLogIn.setText("This operation is sensitive and requires recent authentication");
                                            Toast.makeText(MainActivity.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                            errLogIn.setText("An account already exists with the same email address but different sign-in credentials");
                                            Toast.makeText(MainActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            errLogIn.setText("The email address is already in use by another account.");
                                            Toast.makeText(MainActivity.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                                            //etEmail.setError("The email address is already in use by another account.");
                                            //etEmail.requestFocus();
                                            break;

                                        case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                            errLogIn.setText("This credential is already associated with a different user account");
                                            Toast.makeText(MainActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_USER_DISABLED":
                                            errLogIn.setText("The user account has been disabled by an administrator.");
                                            Toast.makeText(MainActivity.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_USER_TOKEN_EXPIRED":
                                            errLogIn.setText("The user\\'s credential is no longer valid. The user must sign in again");
                                            Toast.makeText(MainActivity.this, "1-The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_USER_NOT_FOUND":
                                            errLogIn.setText("There is no user record corresponding to this identifie");
                                            Toast.makeText(MainActivity.this, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_INVALID_USER_TOKEN":
                                            errLogIn.setText("The user\\'s credential is no longer valid.2");
                                            Toast.makeText(MainActivity.this, "2-The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_OPERATION_NOT_ALLOWED":
                                            errLogIn.setText("This operation is not allowed");
                                            Toast.makeText(MainActivity.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_WEAK_PASSWORD":
                                            errLogIn.setText("The given password is invalid(weak password)");
                                            Toast.makeText(MainActivity.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                                            //etPassword.setError("The password is invalid it must 6 characters at least");
                                            //etPassword.requestFocus();
                                            break;
                                        */
                                    }



                                }
                            }
                        });


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


    @Override
    public void onStart() {
        super.onStart();



    }


    public void registerUserSharedPreferences(String email,String uid){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("UserData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("logged",true);
        editor.putBoolean("userInitialize_data",false); //To gel all important data about user
        editor.putString("email",email);
        editor.putString("uid",uid);
        editor.apply();
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



}