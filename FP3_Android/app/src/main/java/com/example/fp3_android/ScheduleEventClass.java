package com.example.fp3_android;

public class ScheduleEventClass {

    String key,email,date,hour,adress;


    /*public ScheduleEventClass(String key, String email, String date, String hour) {
        this.key = key;
        this.email = email;
        this.date = date;
        this.hour = hour;
    }*/

    public ScheduleEventClass(String key, String email, String date, String hour, String adress) {
        this.key = key;
        this.email = email;
        this.date = date;
        this.hour = hour;
        this.adress = adress;
    }

    public ScheduleEventClass(String email, String date, String hour) {
        this.email = email;
        this.date = date;
        this.hour = hour;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
