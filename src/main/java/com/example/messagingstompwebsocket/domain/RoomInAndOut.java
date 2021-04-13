package com.example.messagingstompwebsocket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInAndOut {
    private int roomId;
    private String username;
}
