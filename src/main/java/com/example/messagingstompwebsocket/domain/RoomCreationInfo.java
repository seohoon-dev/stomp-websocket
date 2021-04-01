package com.example.messagingstompwebsocket.domain;

public class RoomCreationInfo {
    private String creator;
    private String title;

    public RoomCreationInfo(String creator, String title) {
        this.creator = creator;
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
