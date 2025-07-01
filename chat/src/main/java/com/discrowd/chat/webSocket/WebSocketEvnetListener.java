package com.discrowd.chat.webSocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEvnetListener {
    // W przyszłości będziesz używać Feign client, by wysyłać do user service
    // @Autowired
    // private UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // Tutaj można wyciągnąć informacje z nagłówków połączenia STOMP, np. token JWT
        // String jwt = headerAccessor.getFirstNativeHeader("Authorization");

        // Po walidacji JWT, możesz ustawić atrybuty sesji, które będą dostępne w @MessageMapping
        // headerAccessor.getSessionAttributes().put("userId", parsedUserId);
        // headerAccessor.getSessionAttributes().put("username", parsedUsername);

        // Tutaj możesz zaktualizować status użytkownika na "online" w User Service
        // System.out.println("User connected: " + headerAccessor.getSessionId());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // Pobierz userId z atrybutów sesji
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (userId != null) {
            // Tutaj możesz zaktualizować status użytkownika na "offline" w User Service
            // System.out.println("User disconnected: " + username + " (ID: " + userId + ")");
        } else {
            // System.out.println("Anonymous user disconnected: " + headerAccessor.getSessionId());
        }
    }
}
