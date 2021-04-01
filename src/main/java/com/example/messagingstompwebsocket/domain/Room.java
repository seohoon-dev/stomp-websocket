package com.example.messagingstompwebsocket.domain;

public class Room {
    private int id;
    private String title;
    private int count;

    public Room(int id, String title, int count) {
        this.id = id;
        this.title = title;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
