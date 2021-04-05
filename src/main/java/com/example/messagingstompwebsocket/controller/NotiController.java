package com.example.messagingstompwebsocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class NotiController {
    private static final Map<String, SseEmitter> CLIENTS = new ConcurrentHashMap<>();

    @GetMapping("/api/sub/noti")
    public SseEmitter subscribe(String id) {
        log.info("subscribe");
        SseEmitter emitter = new SseEmitter(0L);
        CLIENTS.put(id, emitter);

        return emitter;
    }

    @GetMapping("/api/pub/noti")
    public void publish() {
        log.info("publish");
        Set<String> deadIds = new HashSet<>();

        CLIENTS.forEach((id, emitter) -> {
            try {
                emitter.send("test notification message", MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                deadIds.add(id);
                log.warn("disconnected id : {}", id);
            }
        });

        deadIds.forEach(CLIENTS::remove);
    }
}
