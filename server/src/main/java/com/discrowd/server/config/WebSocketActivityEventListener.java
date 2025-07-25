package com.discrowd.server.config;


import com.discrowd.server.model.status.UserActivityStatus;
import com.discrowd.server.service.UserActivityStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketActivityEventListener {

    private final UserActivityStatusService userActivityStatusService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            String userId = user.getName();
            System.out.println("User connected to WebSocket (globally): " + userId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            String userId = user.getName();
            System.out.println("User disconnected from WebSocket (globally): " + userId);
            userActivityStatusService.updateAndPublishUserStatus(userId, UserActivityStatus.OFFLINE, null);
        }
    }
}
