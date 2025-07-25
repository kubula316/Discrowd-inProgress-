package com.discrowd.server.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Indexed
    private String channelId;
    @Indexed
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(String content, Long senderId, String channelId) {
        this();
        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
    }
}