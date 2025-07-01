package com.discrowd.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String content;
    private Long senderId;
    private String senderUsername;
    @Indexed
    private Long channelId;
    @Indexed
    private LocalDateTime timestamp;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(String content, Long senderId, String senderUsername, Long channelId) {
        this();
        this.content = content;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.channelId = channelId;
    }
}