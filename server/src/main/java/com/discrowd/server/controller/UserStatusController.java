package com.discrowd.server.controller;

import com.discrowd.server.model.status.UserActivityStatus;
import com.discrowd.server.model.status.UserStatusUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import com.discrowd.server.service.UserActivityStatusService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate; // Potrzebne do wysyłania prywatnych wiadomości
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;



@Controller
@RequiredArgsConstructor
public class UserStatusController {

    private final UserActivityStatusService userActivityStatusService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/server/join")
    public void userJoinServer(Principal principal, @Payload String serverId) {
        if (principal == null || serverId == null || serverId.isEmpty()) {
            System.err.println("Join server request ignored: Missing principal or serverId.");
            return;
        }
        String userId = principal.getName();

        System.out.println("User " + userId + " requests to join server context: " + serverId);

        userActivityStatusService.updateAndPublishUserStatus(userId, UserActivityStatus.ONLINE, serverId);

        Map<String, UserStatusUpdate> serverUserStatuses = userActivityStatusService.getServerActiveUsers(serverId);
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/initial.server." + serverId + ".statuses",
                serverUserStatuses
        );
        System.out.println("Sent initial active users for server " + serverId + " to user " + userId + ".");
    }

    @MessageMapping("/server/leave") // Endpoint STOMP: /app/server/leave
    public void userLeaveServer(Principal principal, @Payload String serverId) {
        if (principal == null || serverId == null || serverId.isEmpty()) {
            System.err.println("Leave server request ignored: Missing principal or serverId.");
            return;
        }
        String userId = principal.getName();
        System.out.println("User " + userId + " requests to leave server context: " + serverId);

        userActivityStatusService.updateAndPublishUserStatus(userId, UserActivityStatus.OFFLINE, serverId);
    }


}
