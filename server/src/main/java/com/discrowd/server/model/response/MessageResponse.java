package com.discrowd.server.model.response;



import com.discrowd.server.model.entity.Message;

import java.time.LocalDateTime;

public class MessageResponse {
    public String id;
    public String content;
    public Long senderId;
    public String channelId;
    public LocalDateTime timestamp;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.senderId = message.getSenderId();
        this.channelId = message.getChannelId();
        this.timestamp = message.getTimestamp();
    }
}
