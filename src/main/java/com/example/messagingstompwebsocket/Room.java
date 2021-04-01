package com.example.messagingstompwebsocket;

public class Room {
    private int roomId;
    private String username;

    public Room(int roomId, String username) {
        this.roomId = roomId;
        this.username = username;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
