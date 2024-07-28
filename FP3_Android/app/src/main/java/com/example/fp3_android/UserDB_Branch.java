package com.example.fp3_android;

public class UserDB_Branch {

    String username,email,dateOfBirthday,token,timestamp_login;

    public UserDB_Branch(String username, String email, String dateOfBirthday, String token,String timestamp_login) {
        this.username = username;
        this.email = email;
        this.dateOfBirthday = dateOfBirthday;
        this.token = token;
        this.timestamp_login=timestamp_login;
    }


    public UserDB_Branch(String username, String email, String dateOfBirthday, String token) {
        this.username = username;
        this.email = email;
        this.dateOfBirthday = dateOfBirthday;
        this.token = token;
        //this.timestamp_login=timestamp_login;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(String dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimestamp_login() {
        return timestamp_login;
    }

    public void setTimestamp_login(String timestamp_login) {
        this.timestamp_login = timestamp_login;
    }

}
