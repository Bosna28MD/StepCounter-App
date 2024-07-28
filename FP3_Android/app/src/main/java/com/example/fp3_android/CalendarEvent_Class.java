package com.example.fp3_android;

public class CalendarEvent_Class {

    String uid_user1_request,uid_user2_receive,time_miliseconds_event,type_request,adress_event;

    /*public CalendarEvent_Class(String uid_user1_request, String uid_user2_receive, String time_miliseconds_event, String type_request) {
        this.uid_user1_request = uid_user1_request;
        this.uid_user2_receive = uid_user2_receive;
        this.time_miliseconds_event = time_miliseconds_event;
        this.type_request = type_request; // -1-request , 1-accepted , 0-finished
    }*/

    public CalendarEvent_Class(String uid_user1_request, String uid_user2_receive, String time_miliseconds_event, String type_request, String adress_event) {
        this.uid_user1_request = uid_user1_request;
        this.uid_user2_receive = uid_user2_receive;
        this.time_miliseconds_event = time_miliseconds_event;
        this.type_request = type_request;
        this.adress_event = adress_event;
    }

    public String getUid_user1_request() {
        return uid_user1_request;
    }

    public void setUid_user1_request(String uid_user1_request) {
        this.uid_user1_request = uid_user1_request;
    }

    public String getUid_user2_receive() {
        return uid_user2_receive;
    }

    public void setUid_user2_receive(String uid_user2_receive) {
        this.uid_user2_receive = uid_user2_receive;
    }

    public String getTime_miliseconds_event() {
        return time_miliseconds_event;
    }

    public void setTime_miliseconds_event(String time_miliseconds_event) {
        this.time_miliseconds_event = time_miliseconds_event;
    }

    public String getType_request() {
        return type_request;
    }

    public void setType_request(String type_request) {
        this.type_request = type_request;
    }

    public String getAdress_event() {
        return adress_event;
    }

    public void setAdress_event(String adress_event) {
        this.adress_event = adress_event;
    }
}
