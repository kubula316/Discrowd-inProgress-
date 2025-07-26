package com.discrowd.server.model.request;

import lombok.Data;

@Data
public class MessageRequest {
    public String content;
    public String channelId;
    public String imageUrl;
}
