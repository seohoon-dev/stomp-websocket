package com.example.messagingstompwebsocket;

import com.example.messagingstompwebsocket.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final ChattingController chattingController;

    public static AtomicInteger roomId = new AtomicInteger(1);
    public static Map<Integer, Room> roomInfo = new HashMap<>();

    @PostMapping("/api/room")
    public int create(String title) {
        System.out.println("create method");
        int id = roomId.getAndIncrement();

        Room room = new Room(id, title, 0);
        roomInfo.put(id, room);  // 방에 몇 명 있는지 체크

        return id;
    }

    @MessageMapping("/join")
    public void join(int id) {
        Room room = roomInfo.get(id);
        room.setCount(room.getCount() + 1);

        System.out.println("room.getCount() = " + room.getCount());

        roomInfo.put(id, room);
    }

}
