package com.example.fp3_android;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    StepCounter_Class stepCounterClass;
    private SensorManager mSensorManager=null;
    private Sensor stepSensor=null;
    ProgressBar progressBar;
    TextView txtSteps,txtGoalSteps;
    Button btn1_stepsHome,btn2_stepsReportWeekly;


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar=view.findViewById(R.id.ProgressBar);
        txtSteps=view.findViewById(R.id.txtSteps);
        txtGoalSteps=view.findViewById(R.id.txtGoal);

        progressBar.setMax(30000); //Set by default 30 000 steps

        txtSteps.setText("Steps: "+StepCounter_Class.getCurrentSteps(getContext()));
        progressBar.setProgress(Integer.valueOf(StepCounter_Class.getCurrentSteps(getContext())));


        MaterialButtonToggleGroup toggleGroup=view.findViewById(R.id.togglebtnGroup1);
        btn1_stepsHome=view.findViewById(R.id.toggleBtn1);
        btn2_stepsReportWeekly=view.findViewById(R.id.toggleBtn2);


        toggleGroup.check(btn1_stepsHome.getId());
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    if(checkedId==btn1_stepsHome.getId()){
                        //Home Fragment
                    }else if(checkedId==btn2_stepsReportWeekly.getId()){
                        replaceFragment(new StepsWeeklyReportFragment());
                        //Toast.makeText(getContext(), "Fragment-2", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    toggleGroup.check(btn1_stepsHome.getId());
                }
            }
        });


        /*
        stepCounterClass=new StepCounter_Class(getContext(),this);

        if(!stepCounterClass.appStart_Get_Key() ){
            stepCounterClass.appStart_Set_KeyTrue();
            StepCounter_Class.appStart=0;

        }else{
            StepCounter_Class.appStart=1;
            stepCounterClass.loadData();
        }


        //resetSteps(); //If you press on step Counter the steps are reseted

        mSensorManager=(SensorManager)getContext().getSystemService(getContext().SENSOR_SERVICE);
        stepSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

         */



        /*FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d("HomeFragment123","Token: "+task.getResult());
            }
        });*/


    }


    @Override
    public void onResume() {
        super.onResume();

        //StepCounter_Class.checkHomeFragmentVisible=true;
        StepCounter_Class.homeFragment=HomeFragment.this;
        /*
        if(stepSensor==null){
            Toast.makeText(getContext(), "This device doesn't have Sensory", Toast.LENGTH_SHORT).show();
            return;
        }
        mSensorManager.registerListener(stepCounterClass,stepSensor,SensorManager.SENSOR_DELAY_NORMAL);
        */
    }


    @Override
    public void onPause() {
        super.onPause();
        //StepCounter_Class.checkHomeFragmentVisible=false;
        StepCounter_Class.homeFragment=null;

        //mSensorManager.unregisterListener(stepCounterClass);
    }


    public void resetSteps(){
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "Press Longer to reset steps", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Current Steps: "+StepCounter_Class.getCurrentSteps(getContext()), Toast.LENGTH_SHORT).show();
            }
        });

        /*
        progressBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                txtSteps.setText("Steps: "+0);
                progressBar.setProgress(0);
                StepCounter_Class.appStart=0;

                return true;
            }
        });
        */

    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }




    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getTxtSteps() {
        return txtSteps;
    }


    public StepCounter_Class getStepCounterClass() {
        return stepCounterClass;
    }
}
