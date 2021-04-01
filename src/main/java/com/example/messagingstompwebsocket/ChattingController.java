package com.example.messagingstompwebsocket;

import com.example.messagingstompwebsocket.domain.ChattingMessage;
import com.example.messagingstompwebsocket.domain.Greeting;
import com.example.messagingstompwebsocket.domain.Room;
import com.example.messagingstompwebsocket.domain.RoomCreationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import static com.example.messagingstompwebsocket.RoomController.roomId;
import static com.example.messagingstompwebsocket.RoomController.roomInfo;

@Controller
@RequiredArgsConstructor
public class ChattingController {

    @MessageMapping("/test")
    public String get(String message) {
        System.out.println("message = " + message);
        return "test response";
    }

    @MessageMapping("/create/room")
    @SendTo("/topic/create/room")
    public Room add(RoomCreationInfo roomCreationInfo) throws Exception {
        System.out.println("create method");
        int id = roomId.getAndIncrement();

        Room room = new Room(id, roomCreationInfo.getTitle(), 1);
        roomInfo.put(id, room);  // 방에 몇 명 있는지 체크

        return room;   // 방 만든 유저는 반환값으로 받은 roomId 를 구독
    }

    @MessageMapping("/room/{id}")
    @SendTo("/topic/room/{id}")
    public ChattingMessage chat(@DestinationVariable String id, ChattingMessage message) throws Exception {
        System.out.println("chat method");

        Thread.sleep(200); // simulated delay
        return message;
    }

    @SendTo({"/topic/1", "/topic/2"})
    private void test() {
        System.out.println("test");
        new Greeting("Test,!");
    }

}