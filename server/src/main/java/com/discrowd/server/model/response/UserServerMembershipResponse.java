package com.discrowd.server.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserServerMembershipResponse {
    private Long membershipId;
    private Long userId;
    private Long serverId;
    private String role;
}