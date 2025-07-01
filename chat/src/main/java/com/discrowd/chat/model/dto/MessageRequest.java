package com.discrowd.chat.model.dto;

import lombok.Data;

@Data
public class MessageRequest {
    public String content;
    public Long channelId;
}
