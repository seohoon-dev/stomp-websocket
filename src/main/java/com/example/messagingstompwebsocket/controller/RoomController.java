package com.example.messagingstompwebsocket.controller;

import com.example.messagingstompwebsocket.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final ChattingController chattingController;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public static AtomicInteger roomId = new AtomicInteger(1);
    public static Map<Integer, Room> roomInfo = new HashMap<>();

    @PostConstruct
    public void init() {
        String[] titles = {"즐챗", "소통", "대화"};
        for (String title : titles) {
            create(new RoomTitle(title));
        }
    }

    @PostMapping("/api/room")
    public int create(@RequestBody RoomTitle roomTitle) {
        System.out.println("create method");
        System.out.println("roomTitle = " + roomTitle.getTitle());

        int id = roomId.getAndIncrement();

        Room room = new Room(id, roomTitle.getTitle(), 0);
        roomInfo.put(id, room);  // 방에 몇 명 있는지 체크

        // 방 목록
        simpMessagingTemplate.convertAndSend("/topic/list", list());

        return id;
    }

    @MessageMapping("/list")
    @SendTo("/topic/list")
    public List<Room> list() {
        System.out.println("list method");

        return new ArrayList<>(roomInfo.values());
    }

    @MessageMapping("/join")
    public void join(int id) {
        Room room = roomInfo.get(id);
        room.setCount(room.getCount() + 1);

        System.out.println("room.getCount() = " + room.getCount());

        roomInfo.put(id, room);
    }

    @MessageMapping("/exit")
    public void exit(int id) {
        System.out.println("id = " + id);

        Room room = roomInfo.get(id);
        int count = room.getCount() - 1;

        if (count < 1) {
            roomInfo.remove(id);
        } else {
            room.setCount(count);
            roomInfo.put(id, room);
            System.out.println("room.getCount() = " + room.getCount());
        }

        // 방 목록
        simpMessagingTemplate.convertAndSend("/topic/list", list());
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class RoomTitle {
    private String title;
}
