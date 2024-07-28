package com.example.fp3_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import androidx.fragment.app.Fragment;

public class StepCounter_Class implements SensorEventListener {


    Context context;
    Fragment fragment;
    public int previousSteps=0;
    public int totalSteps=0;
    static public int appStart=1;
    //static public boolean checkHomeFragmentVisible=false;
    static public HomeFragment homeFragment=null;
    /*
    public StepCounter_Class(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }
    */

    public StepCounter_Class(Context context) {
        this.context = context;
    }

    public static void saveData(int value,Context context){
        SharedPreferences sharePref=context.getSharedPreferences("UserData_StepCounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharePref.edit();
        editor.putInt("nrStep_Log",value); // nrStep_Log - Number of steps
        editor.apply();
    }


    public static void loadData(Context context,StepCounter_Class stepCounterClass){
        SharedPreferences sharePref = context.getSharedPreferences("UserData_StepCounter", Context.MODE_PRIVATE);
        stepCounterClass.previousSteps=Integer.valueOf(sharePref.getInt("nrStep_Log", '0'));
    }


    public static void appStart_Set_KeyTrue(Context context){
        SharedPreferences sharePref=context.getSharedPreferences("UserData_StepCounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharePref.edit();
        editor.putBoolean("key_initialization",true); //When user log in for the first time set value True
        editor.apply();
    }

    public static void appStart_Set_KeyFalse(Context context){
        SharedPreferences sharePref=context.getSharedPreferences("UserData_StepCounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharePref.edit();
        editor.putBoolean("key_initialization",false);
        editor.apply();
    }

    public static boolean appStart_Get_Key(Context context){
        SharedPreferences sharePref=context.getSharedPreferences("UserData_StepCounter", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor=sharePref.edit();
        boolean savedNumber = sharePref.getBoolean("key_initialization", false); // key - Check if user connected, number of steps set to 0
        return savedNumber;
    }


    public static void setCurrentSteps(String steps,Context context){
        SharedPreferences sharePref=context.getSharedPreferences("UserData_StepCounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharePref.edit();
        editor.putString("current_steps",steps);
        editor.apply();
    }

    public static String getCurrentSteps(Context context){
        SharedPreferences sharePref=context.getSharedPreferences("UserData_StepCounter", Context.MODE_PRIVATE);
        String current_steps=sharePref.getString("current_steps", "0");
        return current_steps;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            Log.d("StepCounterClass123",sensorEvent.values[0]+"");
            if(StepCounter_Class.appStart==0){
                StepCounter_Class.saveData((int)sensorEvent.values[0],this.context);
                loadData(this.context,this); //The value from SharedPreferences of steps is going into variable previousSteps
                StepCounter_Class.appStart=1;
            }

            totalSteps=((int)sensorEvent.values[0]);
            int currentSteps=totalSteps-previousSteps;

            if(currentSteps<0){
                //This "if" is for the moment when user for example reset the phone OS(operating system) and number of steps from OS
                //are also reseted
                StepCounter_Class.saveData((int)sensorEvent.values[0],this.context);
                StepCounter_Class.loadData(this.context,this);
                currentSteps=totalSteps-previousSteps;
            }

            StepCounter_Class.setCurrentSteps(String.valueOf(currentSteps),this.context);
            if(StepCounter_Class.homeFragment!=null){
                homeFragment.getTxtSteps().setText("Steps: "+currentSteps);
                homeFragment.getProgressBar().setProgress(currentSteps);
                //((HomeFragment)fragment).getTxtSteps().setText("Steps: "+currentSteps);
                //((HomeFragment)fragment).getProgressBar().setProgress(currentSteps);
            }



        }
    }


}
