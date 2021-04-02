package com.example.messagingstompwebsocket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Room {
    private int id;
    private String title;
    private int count;
}
