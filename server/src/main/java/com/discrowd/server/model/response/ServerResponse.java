package com.discrowd.server.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerResponse {
    private Long id;
    private String name;
    private String iconUrl;
    private Long ownerId;
}
