package com.jade.canopusapi.model;


import com.jade.canopusapi.model.utils.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
}
