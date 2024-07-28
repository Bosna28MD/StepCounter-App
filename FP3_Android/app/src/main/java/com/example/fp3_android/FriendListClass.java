package com.example.fp3_android;

public class FriendListClass {
    String id_friendList,email,username,birthday,uid_userfriend;

    public FriendListClass(String id_friendList, String email, String username, String birthday, String uid_userfriend) {
        this.id_friendList = id_friendList;
        this.email = email;
        this.username = username;
        this.birthday = birthday;
        this.uid_userfriend = uid_userfriend;
    }


    public FriendListClass(String id_friendList, String uid_userfriend) {
        this.id_friendList = id_friendList; //id of "friends" database
        this.uid_userfriend = uid_userfriend; //uid of the other user(not current one)
    }

    public String getId_friendList() {
        return id_friendList;
    }

    public void setId_friendList(String id_friendList) {
        this.id_friendList = id_friendList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUid_userfriend() {
        return uid_userfriend;
    }

    public void setUid_userfriend(String uid_userfriend) {
        this.uid_userfriend = uid_userfriend;
    }
}
