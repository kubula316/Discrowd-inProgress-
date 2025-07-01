package com.discrowd.chat.model.dto;

import com.discrowd.chat.model.Message;

import java.time.LocalDateTime;

public class MessageResponse {
    public String id;
    public String content;
    public Long senderId;
    public String senderUsername;
    public Long channelId;
    public LocalDateTime timestamp;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.senderId = message.getSenderId();
        this.senderUsername = message.getSenderUsername();
        this.channelId = message.getChannelId();
        this.timestamp = message.getTimestamp();
    }
}
