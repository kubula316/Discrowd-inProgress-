package com.discrowd.server.controller;

import com.discrowd.server.model.dto.ServerDetailsResponse;
import com.discrowd.server.model.entity.Message;
import com.discrowd.server.model.request.CreateCategoryRequest;
import com.discrowd.server.model.request.CreateTextChannelRequest;
import com.discrowd.server.model.request.CreateVoiceChannelRequest;
import com.discrowd.server.model.request.MessageRequest;
import com.discrowd.server.model.response.MessageResponse;
import com.discrowd.server.service.MessageService;
import com.discrowd.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ServerSesionController {

    private final MessageService messageService;

    private final ServerService serverService;

    @MessageMapping("/sendMessage/{serverId}")
    @SendTo("/topic/messages/{serverId}")
    public MessageResponse sendMessage(@DestinationVariable String serverId,
                                       @RequestBody MessageRequest request,
                                       Principal principal) {
        System.out.println(principal);
        Message message = messageService.saveMessage(request.getContent(), getUserIdFromPrincipal(principal), request.channelId);
        System.out.println("Sending message: " + request.getContent() + " to channel: " + request.channelId + " in server: " + serverId);
        return new MessageResponse(message);

    }

    @MessageMapping("/addChannel/{serverId}")
    @SendTo("/topic/server/{serverId}")
    public ServerDetailsResponse addCategoryChannel(@DestinationVariable String serverId,
                                                    @RequestBody CreateCategoryRequest request,
                                                    Principal principal) {
        System.out.println("Creating category: " + request.getCategoryName() + " in server: " + serverId);
        return serverService.createCategory(serverId, request.getCategoryName(), getUserIdFromPrincipal(principal));
    }

    @MessageMapping("/addTextChannel/{serverId}")
    @SendTo("/topic/server/{serverId}")
    public ServerDetailsResponse addTextChannel(@DestinationVariable String serverId,
                                                    @RequestBody CreateTextChannelRequest request,
                                                    Principal principal) {
        System.out.println("Creating text channel: " + request.getChannelName() + " in server: " + serverId);
        return serverService.createTextChannel(serverId, request.getChannelName(), getUserIdFromPrincipal(principal), request.getCategoryId());
    }

    @MessageMapping("/addVoiceChannel/{serverId}")
    @SendTo("/topic/server/{serverId}")
    public ServerDetailsResponse addVoiceChannel(@DestinationVariable String serverId,
                                                 @RequestBody CreateVoiceChannelRequest request,
                                                 Principal principal) {
        System.out.println("Creating text channel: " + request.getChannelName() + " in server: " + serverId);
        return serverService.createVoiceChannel(serverId, request.getChannelName(), getUserIdFromPrincipal(principal), request.getCategoryId());
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null.");
        }
        return Long.parseLong(principal.getName());
    }

}
