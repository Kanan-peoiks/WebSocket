package com.example.websocket.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {

    private String sender;
    private String content;
    private String type;
    private LocalDateTime timestamp;

}