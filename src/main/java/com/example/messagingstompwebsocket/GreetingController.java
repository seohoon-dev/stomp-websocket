package com.example.messagingstompwebsocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class GreetingController {
    static AtomicInteger roomId = new AtomicInteger(1);
    static Map<AtomicInteger, Integer> roomInfo = new HashMap<>();

    @MessageMapping("/add/room")
    @SendTo("/topic/update/room")
    public Room add(HelloMessage message) throws Exception {
        System.out.println("add method");

        roomInfo.put(roomId, 1);  // 방에 몇 명 있는지 체크

        return new Room(roomId.getAndIncrement(), message.getUsername());   // 방 만든 유저는 반환값으로 받은 roomId 를 구독
    }

    @MessageMapping("/room/{id}/chat")
    @SendTo("/topic/room/{id}/chat")
//    @SendTo({"/topic/1", "/topic/2"})
    public void first(@DestinationVariable String id, HelloMessage message) throws Exception {
        System.out.println("first method");

        Thread.sleep(500); // simulated delay
        test();
//        return new Greeting("First, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @SendTo("/topic/1")
    private void test() {
        System.out.println("test");
        new Greeting("Test,!");
    }

    @MessageMapping("/2")
    @SendTo("/topic/2")
    public Greeting second(HelloMessage message) throws Exception {
        System.out.println("second method");

        Thread.sleep(500); // simulated delay
        return new Greeting("Second, " + HtmlUtils.htmlEscape(message.getUsername()) + "!");
    }

}