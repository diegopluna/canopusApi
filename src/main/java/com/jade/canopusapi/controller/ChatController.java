package com.jade.canopusapi.controller;

import com.jade.canopusapi.models.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor messageHeaderAccessor) {
        messageHeaderAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        return chatMessage;
    }
}
