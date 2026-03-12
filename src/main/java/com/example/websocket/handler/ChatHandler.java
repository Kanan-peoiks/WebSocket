package com.example.websocket.handler;

import com.example.websocket.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ChatHandler extends TextWebSocketHandler {

        private final ObjectMapper objectMapper;

        public ChatHandler(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

    private static final Set<WebSocketSession> sessions =
            Collections.synchronizedSet(new HashSet<>());


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        sessions.add(session);

        System.out.println("User connected: " + session.getId());

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();

        ChatMessage chatMessage =
                objectMapper.readValue(payload, ChatMessage.class);

        chatMessage.setTimestamp(LocalDateTime.now());

        String responseJson =
                objectMapper.writeValueAsString(chatMessage);

        for (WebSocketSession s : sessions) {

            if (s.isOpen() && !s.getId().equals(session.getId())) {

                s.sendMessage(new TextMessage(responseJson));

            }

        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        sessions.remove(session);

        System.out.println("User disconnected: " + session.getId());

    }
}

