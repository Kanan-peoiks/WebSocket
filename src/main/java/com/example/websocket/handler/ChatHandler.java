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
            Collections.synchronizedSet(new HashSet<>()); // sessions-a yadda saxlamaq ucun
    //sinxrondur, cunki eyni anda mesaj gondermek ucun

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        sessions.add(session);

        System.out.println("User connected: " + session.getId()); //user qosulduqda

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();

        ChatMessage chatMessage =
                objectMapper.readValue(payload, ChatMessage.class); //json java obyektine cevrilir

        chatMessage.setTimestamp(LocalDateTime.now()); // zaman daxil edilir

        String responseJson =
                objectMapper.writeValueAsString(chatMessage); //json-a geri cevrilir

        for (WebSocketSession ses : sessions) { //sessionlar uzre dovr

            if (ses.isOpen() && !ses.getId().equals(session.getId())) {
              // s.isOpen() - istifadeci var
                //!ses.getId().equals(session.getId()) - istifadeci oz mesajini gormesin
                ses.sendMessage(new TextMessage(responseJson)); //send olunur
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) { //user cixdiqda

        sessions.remove(session); //session silinir

        System.out.println("User disconnected: " + session.getId());

    }
}

