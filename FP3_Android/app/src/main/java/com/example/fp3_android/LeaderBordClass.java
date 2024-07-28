package com.example.fp3_android;

public class LeaderBordClass {
    String position,uid,email,steps;

    public LeaderBordClass(String uid, String email, String steps) {
        this.uid = uid;
        this.email = email;
        this.steps = steps;
    }

    public LeaderBordClass(String uid, String steps) {
        this.uid = uid;
        this.steps = steps;
    }

    public LeaderBordClass(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
