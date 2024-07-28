package com.example.fp3_android;

public class FriendsDB_Branch {

    String uid_user1,uid_user2,type_friend,user_send_request;

    public FriendsDB_Branch(String uid_user1, String uid_user2, String type_friend, String user_send_request) {
        this.uid_user1 = uid_user1;
        this.uid_user2 = uid_user2;
        this.type_friend = type_friend;
        this.user_send_request = user_send_request;
    }




    public String getUid_user1() {
        return uid_user1;
    }

    public void setUid_user1(String uid_user1) {
        this.uid_user1 = uid_user1;
    }

    public String getUid_user2() {
        return uid_user2;
    }

    public void setUid_user2(String uid_user2) {
        this.uid_user2 = uid_user2;
    }

    public String getType_friend() {
        return type_friend;
    }

    public void setType_friend(String type_friend) {
        this.type_friend = type_friend;
    }

    public String getUser_send_request() {
        return user_send_request;
    }

    public void setUser_send_request(String user_send_request) {
        this.user_send_request = user_send_request;
    }

}
