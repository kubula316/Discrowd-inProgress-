package com.discrowd.chat.controller;

import com.discrowd.chat.model.Message;
import com.discrowd.chat.model.dto.MessageRequest;
import com.discrowd.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;


    @MessageMapping("/sendMessage/{chanelId}")
    @SendTo("/topic/room/{chanelId}")
    public Message sendMessage(@RequestBody MessageRequest messageRequest, SimpMessageHeaderAccessor headerAccessor) {

        Long userId = Long.parseLong(headerAccessor.getSessionAttributes().get("userId").toString());
        String username = headerAccessor.getSessionAttributes().get("username").toString();


        Message message = new Message(
                messageRequest.getContent(),
                userId,
                username,
                messageRequest.getChannelId()
        );
        messageService.saveMessage(messageRequest.getContent(), userId, username, messageRequest.getChannelId());

        return message;
    }
}
