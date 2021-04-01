package com.example.messagingstompwebsocket;

public class HelloMessage {
    private String messageType;

    private String username;

    public HelloMessage(String messageType, String username) {
        this.messageType = messageType;
        this.username = username;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public HelloMessage() {
    }

    public HelloMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}