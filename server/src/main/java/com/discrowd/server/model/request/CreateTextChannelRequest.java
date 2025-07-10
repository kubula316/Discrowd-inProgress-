package com.discrowd.server.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTextChannelRequest {
    private String serverId;
    private String channelName;
    private String categoryId;
}
