package com.example.chatapplication.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phoneNumber;
    private String username;
    private Timestamp createdTimeStamp ;
    private String userId ;
    private String fcmToken;



    public UserModel() {

    }

    public UserModel(String phoneNumber, String username, Timestamp createdTimeStamp , String userId) {
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.createdTimeStamp = createdTimeStamp;
        this.userId = userId ;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
