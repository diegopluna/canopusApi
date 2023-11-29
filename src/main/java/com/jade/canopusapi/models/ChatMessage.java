package com.jade.canopusapi.models;


import com.jade.canopusapi.models.utils.MessageType;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private String avatar;
    private String id;
}
