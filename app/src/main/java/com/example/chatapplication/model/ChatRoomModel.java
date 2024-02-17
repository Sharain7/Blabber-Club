package com.example.chatapplication.model;


import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomModel {
    private String chatRoomId;
    private ArrayList<String> userIds;
    private Timestamp lastMessageTimestamp;
    private String lastMessageSenderId ;
    private String lastMessage;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatRoomId, ArrayList<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId ) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;

    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
