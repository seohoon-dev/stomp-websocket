package com.example.messagingstompwebsocket.controller;

import com.example.messagingstompwebsocket.domain.ChattingMessage;
import com.example.messagingstompwebsocket.domain.Greeting;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChattingController {

    @MessageMapping("/room/{id}")
    @SendTo("/topic/room/{id}")
    public ChattingMessage chat(@DestinationVariable String id, ChattingMessage message) throws Exception {
        System.out.println("chat method");

        Thread.sleep(100); // simulated delay
        return message;
    }

    @SendTo({"/topic/1", "/topic/2"})
    private void test() {
        System.out.println("test");
        new Greeting("Test,!");
    }

}