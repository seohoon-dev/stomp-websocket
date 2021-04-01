package com.example.messagingstompwebsocket.domain;

import lombok.Data;

@Data
public class ChattingMessage {
    private String name;
    private String content;
}