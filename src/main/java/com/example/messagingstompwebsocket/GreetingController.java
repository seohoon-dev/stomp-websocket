package com.example.messagingstompwebsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(500); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/1")
    @SendTo("/topic/1")
    public Greeting first(HelloMessage message) throws Exception {
        Thread.sleep(500); // simulated delay
        return new Greeting("First, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/2")
    @SendTo("/topic/2")
    public Greeting second(HelloMessage message) throws Exception {
        Thread.sleep(500); // simulated delay
        return new Greeting("Second, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}