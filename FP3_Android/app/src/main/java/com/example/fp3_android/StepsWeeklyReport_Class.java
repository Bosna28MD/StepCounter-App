package com.example.fp3_android;

public class StepsWeeklyReport_Class {
    String position,day,steps;

    public StepsWeeklyReport_Class(String position, String day, String steps) {
        this.position = position;
        this.day = day;
        this.steps = steps;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}
